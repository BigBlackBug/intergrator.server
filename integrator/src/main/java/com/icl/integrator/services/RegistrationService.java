package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import com.icl.integrator.model.*;
import com.icl.integrator.util.connectors.ConnectionException;
import com.icl.integrator.util.connectors.EndpointConnector;
import com.icl.integrator.util.connectors.EndpointConnectorFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 16.12.13
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
@Service
public class RegistrationService {

    private static Log logger = LogFactory.getLog(RegistrationService.class);

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EndpointConnectorFactory connectorFactory;

    @Autowired
    private PersistenceService persistenceService;

    @Autowired
    private DeliveryCreator deliveryCreator;

    @Transactional(noRollbackFor = ConnectionException.class)
    public <T extends ActionDescriptor>
    List<ActionRegistrationResultDTO> register(
            TargetRegistrationDTO<T> registrationDTO)
            throws TargetRegistrationException {
	    List<ActionRegistrationResultDTO> result = new ArrayList<>();

        EndpointDescriptor endpoint = registrationDTO.getEndpoint();
        List<ActionEndpointDTO<T>> actions = new ArrayList<>();
        List<ActionRegistrationDTO<T>> actionRegistrations =
                registrationDTO.getActionRegistrations();
        if (actionRegistrations != null) {
            for (ActionRegistrationDTO<T> actionRegistration : actionRegistrations) {
                ActionEndpointDTO<T> action = actionRegistration.getAction();
                try {
                    if (!actionRegistration.isForceRegister()) {
                        testConnection(endpoint, action);
                    }
                    actions.add(action);
                } catch (ConnectionException ex) {
                    ResponseDTO<Void> dto = new ResponseDTO<>(new ErrorDTO(ex));
                    result.add(new ActionRegistrationResultDTO(action.getActionName(), dto));
                }
            }
        }
	    List<ActionRegistrationResultDTO> regResult =
			    registerService(endpoint, registrationDTO, actions);
	    regResult.addAll(result);
	    return regResult;
    }

    @Transactional(noRollbackFor = Exception.class)
    private <T extends ActionDescriptor>
    List<ActionRegistrationResultDTO> registerService(EndpointDescriptor endpoint,
                                                   TargetRegistrationDTO registrationDTO,
                                                   List<ActionEndpointDTO<T>> actions)
    throws TargetRegistrationException {
        String serviceName = registrationDTO.getServiceName();
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    IntegratorUser creator = (IntegratorUser) authentication.getPrincipal();
	    DeliverySettingsDTO deliverySettings =
                registrationDTO.getDeliverySettings();
        AbstractEndpointEntity serviceEntity =
                createEntity(endpoint, serviceName, deliverySettings, creator);
        try {
            serviceEntity = persistenceService.saveOrUpdate(serviceEntity);
        } catch (DataAccessException ex) {
            logger.error("GG", ex);
            throw new TargetRegistrationException("Ошибка регистрации", ex);
        }
	    List<ActionRegistrationResultDTO> result = new ArrayList<>();
        for (ActionEndpointDTO<T> actionDTO : actions) {
            ResponseDTO<Void> responseDTO;
            AbstractActionEntity action;
            try {
                action = getAction(actionDTO, serviceEntity.getId(), creator);
                action.setEndpoint(serviceEntity);
                serviceEntity.addAction(action);
                persistenceService.saveOrUpdate(action);
                responseDTO = new ResponseDTO<>(true);
            } catch (Exception ex) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setErrorMessage("Ошибка регистрации");
                errorDTO.setDeveloperMessage(ex.getMessage());
                responseDTO = new ResponseDTO<>(errorDTO);
            }
            result.add(new ActionRegistrationResultDTO(actionDTO.getActionName(), responseDTO));
        }
	    return result;
    }

    @Transactional
    private <T extends ActionDescriptor> AbstractActionEntity getAction(
            ActionEndpointDTO<T> actionEndpoint, UUID serviceID, IntegratorUser creator)
            throws TargetRegistrationException {
        AbstractActionEntity actionEntity;
        T actionDescriptor = actionEndpoint.getActionDescriptor();
        String actionName = actionEndpoint.getActionName();
        if (actionDescriptor instanceof HttpActionDTO) {
            HttpActionDTO httpActionDTO = (HttpActionDTO) actionDescriptor;
            actionEntity = persistenceService.findHttpAction(serviceID, httpActionDTO.getPath());
            if (actionEntity != null) {
                updateAction(actionEntity, actionName, creator);
            } else {
                actionEntity = createHttpAction(actionName, httpActionDTO, creator);
            }
        } else {
            QueueDTO queueDTO = (QueueDTO) actionDescriptor;
            actionEntity = persistenceService
                    .findJmsAction(serviceID, queueDTO.getQueueName(),
                                   queueDTO.getUsername(),
                                   queueDTO.getPassword());
            if (actionEntity != null) {
                updateAction(actionEntity, actionName, creator);
            } else {
                actionEntity = createJmsAction(actionName, queueDTO, creator);
            }
        }
        return actionEntity;
    }

    private JMSAction createJmsAction(String actionName, QueueDTO queueDTO, IntegratorUser creator) {
        JMSAction jmsAction = new JMSAction();
        jmsAction.setActionMethod(queueDTO.getActionMethod());
        jmsAction.setUsername(queueDTO.getUsername());
        jmsAction.setPassword(queueDTO.getPassword());
        jmsAction.setQueueName(queueDTO.getQueueName());
        jmsAction.setActionName(actionName);
        jmsAction.setCreator(creator);
        return jmsAction;
    }

    private HttpAction createHttpAction(String actionName, HttpActionDTO httpActionDTO, IntegratorUser creator) {
        HttpAction httpAction = new HttpAction();
        httpAction.setActionURL(httpActionDTO.getPath());
        httpAction.setActionName(actionName);
        httpAction.setActionMethod(httpActionDTO.getActionMethod());
        httpAction.setCreator(creator);
        return httpAction;
    }

    private void updateAction(AbstractActionEntity actionEntity, String actionName, IntegratorUser creator)
            throws TargetRegistrationException {
        if (actionEntity.isGenerated()) {
            actionEntity.setCreator(creator);
            actionEntity.setActionName(actionName);
        } else {
            throw new TargetRegistrationException(
                    "Такое действие уже зарегистрировано под именем '" + actionName + "'");
        }
    }

    @Transactional
    private AbstractEndpointEntity createEntity(
            EndpointDescriptor descriptor,
            String serviceName, DeliverySettingsDTO deliverySettings, IntegratorUser creator)
            throws TargetRegistrationException {
        AbstractEndpointEntity endpoint;
        if (descriptor instanceof HttpEndpointDescriptorDTO) {
            HttpEndpointDescriptorDTO realDescriptor = (HttpEndpointDescriptorDTO) descriptor;
            String host = realDescriptor.getHost();
            if (host.equalsIgnoreCase("localhost") || host.equalsIgnoreCase("127.0.0.1")) {
                throw new TargetRegistrationException(
                        "Пожалуйста, используйте реальный адрес машины, а не localhost");
            }
            endpoint = persistenceService
                    .findHttpService(host, realDescriptor.getPort());
            if (endpoint != null) {
                updateEndpoint(endpoint, serviceName, deliverySettings,creator);
            } else {
                endpoint = createHttp(serviceName, realDescriptor, deliverySettings,creator);
            }
        } else {
            JMSEndpointDescriptorDTO realDescriptor = (JMSEndpointDescriptorDTO) descriptor;
            String jndiProperties;
            try {
                jndiProperties = mapper.
                        writeValueAsString(realDescriptor.getJndiProperties());
            } catch (JsonProcessingException e) {
                throw new TargetRegistrationException(e);
            }
            endpoint = persistenceService
                    .findJmsService(realDescriptor.getConnectionFactory(), jndiProperties);
            if (endpoint != null) {
                updateEndpoint(endpoint, serviceName, deliverySettings,creator);
            } else {
                endpoint = createJms(serviceName, realDescriptor.getConnectionFactory(),
                                     jndiProperties,
                                     deliverySettings,creator);
            }
        }
        return endpoint;
    }

    private JMSServiceEndpoint createJms(String serviceName, String connectionFactory,
                                         String jndiProperties,
                                         DeliverySettingsDTO deliverySettings, IntegratorUser creator) {
        JMSServiceEndpoint endpoint = new JMSServiceEndpoint();
        endpoint.setCreator(creator);
        endpoint.setConnectionFactory(connectionFactory);
        endpoint.setJndiProperties(jndiProperties);
        endpoint.setServiceName(serviceName);
        DeliverySettings settings;
        if (deliverySettings == null) {
            settings = DeliverySettings.createDefaultSettings();
        } else {
            settings = new DeliverySettings();
            settings.setRetryDelay(deliverySettings.getRetryDelay());
            settings.setRetryNumber(deliverySettings.getRetryNumber());
        }
        settings.setEndpoint(endpoint);
        endpoint.setDeliverySettings(settings);
        return endpoint;
    }

    private HttpServiceEndpoint createHttp(String serviceName, HttpEndpointDescriptorDTO descriptor,
                                           DeliverySettingsDTO deliverySettings, IntegratorUser creator) {
        HttpServiceEndpoint endpoint = new HttpServiceEndpoint();
        endpoint.setServiceName(serviceName);
        endpoint.setCreator(creator);
        endpoint.setServiceURL(descriptor.getHost());
        endpoint.setServicePort(descriptor.getPort());
        DeliverySettings settings;
        if (deliverySettings == null) {
            settings = DeliverySettings.createDefaultSettings();
        } else {
            settings = new DeliverySettings();
            settings.setRetryDelay(deliverySettings.getRetryDelay());
            settings.setRetryNumber(deliverySettings.getRetryNumber());
        }
        settings.setEndpoint(endpoint);
        endpoint.setDeliverySettings(settings);
        return endpoint;
    }

    private void updateEndpoint(AbstractEndpointEntity endpoint,
                                String serviceName, DeliverySettingsDTO deliverySettings, IntegratorUser creator)
            throws TargetRegistrationException {
        if (endpoint.isGenerated()) {
            endpoint.setServiceName(serviceName);
            endpoint.setCreator(creator);
            DeliverySettings settings;
            if (deliverySettings != null) {
                settings = new DeliverySettings();
                settings.setRetryDelay(deliverySettings.getRetryDelay());
                settings.setRetryNumber(deliverySettings.getRetryNumber());
                settings.setEndpoint(endpoint);
                endpoint.setDeliverySettings(settings);
            }
        } else {
            throw new TargetRegistrationException(
                    "Данный сервис уже зарегистрирован под именем '" +
                            endpoint.getServiceName() + "'"
            );
        }
    }

    private <T extends ActionDescriptor>
    void testConnection(EndpointDescriptor endpoint, ActionEndpointDTO<T> action)
            throws ConnectionException {
        EndpointConnector connector =
                connectorFactory.createEndpointConnector(
                        endpoint, action.getActionDescriptor());
        connector.testConnection();
    }

    private void testConnection(DestinationDescriptor destinationDescriptor)
            throws ConnectionException {
        EndpointConnector connector =
                connectorFactory.createEndpointConnector(destinationDescriptor);
        connector.testConnection();
    }

    public <Y> List<ResponseDTO<Void>> register(AutoDetectionRegistrationDTO<Y> packet)
            throws Exception {
        AutoDetectionPacket autoDetectionPacket = new AutoDetectionPacket();
        autoDetectionPacket.setDeliveryPacketType(packet.getDeliveryPacketType());
        List<ResponseDTO<Void>> result = new ArrayList<>();
        String referenceObject = mapper.writeValueAsString(packet.getReferenceObject());

        autoDetectionPacket.setReferenceObject(referenceObject);
        List<RegistrationDestinationDescriptor> destinationDescriptors =
                packet.getDestinationDescriptors();
        for (RegistrationDestinationDescriptor regDD : destinationDescriptors) {
            DestinationDescriptor destinationDescriptor = regDD.getDestinationDescriptor();
            DestinationEntity persistentDestination;
            try {
                if (!regDD.isForceRegister()) {
                    testConnection(destinationDescriptor);
                }

                persistentDestination = deliveryCreator.persistDestination(destinationDescriptor);
                autoDetectionPacket.addDestination(
                        new DestinationEntity(
                                persistentDestination.getService(),
                                persistentDestination.getAction()));
                result.add(new ResponseDTO<Void>(true));
            } catch (Exception ex) {
                result.add(new ResponseDTO<Void>(new ErrorDTO(ex)));
            }
        }
        if (!autoDetectionPacket.getDestinations().isEmpty()) {
            persistenceService.persist(autoDetectionPacket);
        }
        return result;
    }
}
