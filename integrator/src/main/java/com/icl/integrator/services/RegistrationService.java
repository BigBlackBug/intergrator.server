package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.registration.*;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 16.12.13
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
@Component
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

    public <T extends ActionDescriptor>
    Map<String, ResponseDTO<Void>> register(
            TargetRegistrationDTO<T> registrationDTO)
            throws TargetRegistrationException {
        Map<String, ResponseDTO<Void>> result = new HashMap<>();

        EndpointDTO endpoint = registrationDTO.getEndpoint();
        List<ActionEndpointDTO<T>> actions = new ArrayList<>();
        for (ActionRegistrationDTO<T> actionRegistration :
                registrationDTO.getActionRegistrations()) {
            ActionEndpointDTO<T> action = actionRegistration.getAction();
            try {
                if (!actionRegistration.isForceRegister()) {
                    testConnection(endpoint, action);
                }
                actions.add(action);
            } catch (ConnectionException ex) {
                ResponseDTO<Void> dto =
                        new ResponseDTO<>(new ErrorDTO(ex));
                result.put(action.getActionName(), dto);
            }
        }
        switch (endpoint.getEndpointType()) {
            case HTTP: {
                processHttp(endpoint, registrationDTO, actions, result);
                break;
            }
            case JMS: {
                processJMS(endpoint, registrationDTO, actions, result);
                break;
            }
        }
        return result;
    }

    @Transactional
    private <T extends ActionDescriptor>
    void processJMS(EndpointDTO endpoint,
                    TargetRegistrationDTO registrationDTO,
                    List<ActionEndpointDTO<T>> actions,
                    Map<String, ResponseDTO<Void>> result)
            throws TargetRegistrationException {
        String serviceName = registrationDTO.getServiceName();
        JMSEndpointDescriptorDTO descriptor =
                (JMSEndpointDescriptorDTO) endpoint.getDescriptor();
        List<JMSAction> httpActions = getJmsActions(actions);
	    JMSServiceEndpoint serviceEntity =
			    createJmsEntity(descriptor, serviceName,
			                    registrationDTO.getDeliverySettings());
        try {
            serviceEntity = persistenceService.merge(serviceEntity);
        } catch (DataAccessException ex) {
            logger.error("GG", ex);
            throw new TargetRegistrationException("Ошибка регистрации", ex);
        }
        for (JMSAction action : httpActions) {
            ResponseDTO<Void> responseDTO;
            try {
                action.setEndpoint(serviceEntity);
                serviceEntity.addAction(action);
                persistenceService.merge(action);
                responseDTO = new ResponseDTO<>(true);
            } catch (DataAccessException ex) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setErrorMessage("Ошибка регистрации");
                errorDTO.setDeveloperMessage(ex.getMessage());
                responseDTO = new ResponseDTO<>(errorDTO);
            }
            result.put(action.getActionName(), responseDTO);
        }
    }

	//TODO refactor
    @Transactional
    private <T extends ActionDescriptor>
    void processHttp(EndpointDTO endpoint,
                     TargetRegistrationDTO registrationDTO,
                     List<ActionEndpointDTO<T>> actions,
                     Map<String, ResponseDTO<Void>> result)
            throws TargetRegistrationException {
        String serviceName = registrationDTO.getServiceName();
        HttpEndpointDescriptorDTO descriptor =
                (HttpEndpointDescriptorDTO) endpoint.getDescriptor();
        List<HttpAction> httpActions = getHttpActions(actions);
	    DeliverySettingsDTO deliverySettings =
			    registrationDTO.getDeliverySettings();
	    HttpServiceEndpoint serviceEntity =
			    createHttpEntity(descriptor, serviceName,
			                     deliverySettings);
        try {
            serviceEntity = persistenceService.merge(serviceEntity);
        } catch (DataAccessException ex) {
            logger.error("GG", ex);
            throw new TargetRegistrationException("Ошибка регистрации", ex);
        }
        for (HttpAction action : httpActions) {
            ResponseDTO<Void> responseDTO;
            try {
                action.setEndpoint(serviceEntity);
                serviceEntity.addAction(action);
                persistenceService.merge(action);
                responseDTO = new ResponseDTO<>(true);
            } catch (DataAccessException ex) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setErrorMessage("Ошибка регистрации");
                errorDTO.setDeveloperMessage(ex.getMessage());
                responseDTO = new ResponseDTO<>(errorDTO);
            }
            result.put(action.getActionName(), responseDTO);
        }
    }

    private <T extends ActionDescriptor> List<HttpAction> getHttpActions(
            List<ActionEndpointDTO<T>> actions) {
        List<HttpAction> httpActions = new ArrayList<>();
        for (ActionEndpointDTO<T> actionDescriptor : actions) {
            HttpActionDTO httpActionDTO =
                    (HttpActionDTO) actionDescriptor.getActionDescriptor();
            String actionName = actionDescriptor.getActionName();
            HttpAction actionMapping = new HttpAction();
            actionMapping.setActionName(actionName);
            actionMapping.setActionURL(httpActionDTO.getPath());
            httpActions.add(actionMapping);
        }
        return httpActions;
    }

    private <T extends ActionDescriptor> List<JMSAction> getJmsActions(
            List<ActionEndpointDTO<T>> actions) {
        List<JMSAction> jmsActions = new ArrayList<>();
        for (ActionEndpointDTO<T> actionDescriptor : actions) {
            QueueDTO queueDTO =
                    (QueueDTO) actionDescriptor.getActionDescriptor();
            String actionName = actionDescriptor.getActionName();
            JMSAction actionMapping = new JMSAction();
            actionMapping.setUsername(queueDTO.getUsername());
            actionMapping.setActionName(actionName);
            actionMapping.setPassword(queueDTO.getPassword());
            actionMapping.setQueueName(queueDTO.getQueueName());
            jmsActions.add(actionMapping);
        }
        return jmsActions;
    }

	private HttpServiceEndpoint createHttpEntity(
			HttpEndpointDescriptorDTO descriptor,
			String serviceName, DeliverySettingsDTO deliverySettings) {
		HttpServiceEndpoint endpoint = new HttpServiceEndpoint();
		endpoint.setServiceName(serviceName);
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

	private JMSServiceEndpoint createJmsEntity(
			JMSEndpointDescriptorDTO descriptor,
			String serviceName, DeliverySettingsDTO deliverySettings) {
		JMSServiceEndpoint endpoint = new JMSServiceEndpoint();
		endpoint.setConnectionFactory(descriptor.getConnectionFactory());
		String jndiProperties = null;
		try {
			jndiProperties = mapper.
					writeValueAsString(descriptor.getJndiProperties());
		} catch (JsonProcessingException e) {
			throw new TargetRegistrationException(e);
		}
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

    private <T extends ActionDescriptor>
    void testConnection(EndpointDTO endpoint, ActionEndpointDTO<T> action)
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
		autoDetectionPacket.setDeliveryType(packet.getDeliveryType());
		List<ResponseDTO<Void>> result = new ArrayList<>();
		String referenceObject =
				mapper.writeValueAsString(packet.getReferenceObject());

		autoDetectionPacket.setReferenceObject(referenceObject);
		List<RegistrationDestinationDescriptor> destinationDescriptors =
				packet.getDestinationDescriptors();
		for (RegistrationDestinationDescriptor regDD : destinationDescriptors) {
			DestinationDescriptor destinationDescriptor =
					regDD.getDestinationDescriptor();
			PersistentDestination persistentDestination;
			try {
				if (!regDD.isForceRegister()) {
					testConnection(destinationDescriptor);
				}

				persistentDestination = deliveryCreator
						.persistDestination(destinationDescriptor);
				autoDetectionPacket.addDestination(
						new DestinationEntity(
								persistentDestination.getService(),
								persistentDestination.getAction()));
				result.add(new ResponseDTO<Void>(true));
			} catch (Exception ex) {
				result.add(new ResponseDTO<Void>(new ErrorDTO(ex)));
			}
		}
		persistenceService.persist(autoDetectionPacket);
		return result;
	}
}
