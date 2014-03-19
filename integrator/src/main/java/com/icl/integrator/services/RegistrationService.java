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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Transactional(noRollbackFor=ConnectionException.class)
    public <T extends ActionDescriptor>
    Map<String, ResponseDTO<Void>> register(
            TargetRegistrationDTO<T> registrationDTO)
            throws TargetRegistrationException {
        Map<String, ResponseDTO<Void>> result = new HashMap<>();

        EndpointDescriptor endpoint = registrationDTO.getEndpoint();
        List<ActionEndpointDTO<T>> actions = new ArrayList<>();
        List<ActionRegistrationDTO<T>> actionRegistrations =
                registrationDTO.getActionRegistrations();
        if(actionRegistrations!=null){
            for (ActionRegistrationDTO<T> actionRegistration :actionRegistrations) {
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

    @Transactional(noRollbackFor=Exception.class)
    private <T extends ActionDescriptor>
    void processJMS(EndpointDescriptor endpoint,
                    TargetRegistrationDTO registrationDTO,
                    List<ActionEndpointDTO<T>> actions,
                    Map<String, ResponseDTO<Void>> result)
            throws TargetRegistrationException {
        String serviceName = registrationDTO.getServiceName();
        JMSEndpointDescriptorDTO descriptor = (JMSEndpointDescriptorDTO) endpoint;
	    JMSServiceEndpoint serviceEntity =
			    createJmsEntity(descriptor, serviceName,
			                    registrationDTO.getDeliverySettings());

	    try {
		    serviceEntity = persistenceService.saveOrUpdate(serviceEntity);
	    } catch (DataAccessException ex) {
		    logger.error("GG", ex);
		    throw new TargetRegistrationException("Ошибка регистрации", ex);
	    }
	    for (ActionEndpointDTO<T> actionDTO : actions) {
		    ResponseDTO<Void> responseDTO;
		    try {
			    JMSAction action = getJmsAction(actionDTO,serviceEntity.getId());
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
		    result.put(actionDTO.getActionName(), responseDTO);
	    }
    }

	//TODO refactor
    @Transactional(noRollbackFor=Exception.class)
    private <T extends ActionDescriptor>
    void processHttp(EndpointDescriptor endpoint,
                     TargetRegistrationDTO registrationDTO,
                     List<ActionEndpointDTO<T>> actions,
                     Map<String, ResponseDTO<Void>> result)
            throws TargetRegistrationException {
        String serviceName = registrationDTO.getServiceName();
        HttpEndpointDescriptorDTO descriptor =
                (HttpEndpointDescriptorDTO) endpoint;

	    DeliverySettingsDTO deliverySettings =
			    registrationDTO.getDeliverySettings();
	    HttpServiceEndpoint serviceEntity =
			    createHttpEntity(descriptor, serviceName,
			                     deliverySettings);
        try {
            serviceEntity = persistenceService.saveOrUpdate(serviceEntity);
        } catch (DataAccessException ex) {
            logger.error("GG", ex);
            throw new TargetRegistrationException("Ошибка регистрации", ex);
        }
//	    List<HttpAction> httpActions = getHttpActions(actions,serviceEntity.getId());
        for (ActionEndpointDTO<T> actionDTO : actions) {
            ResponseDTO<Void> responseDTO;
	        HttpAction action;
            try {
	            action = getHttpAction(actionDTO, serviceEntity.getId());
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
            result.put(actionDTO.getActionName(), responseDTO);
        }
    }
	@Transactional
	private <T extends ActionDescriptor> HttpAction getHttpAction(
		ActionEndpointDTO<T> actionDescriptor, UUID serviceID) throws TargetRegistrationException{
		HttpActionDTO httpActionDTO =
				(HttpActionDTO) actionDescriptor.getActionDescriptor();
		String actionName = actionDescriptor.getActionName();
		HttpAction httpAction = persistenceService
				.findHttpAction(serviceID, httpActionDTO.getPath());
		if(httpAction!=null){
			if(httpAction.isGenerated()){
				httpAction.setGenerated(false);
				httpAction.setActionName(actionName);
			}else{
				throw new TargetRegistrationException("Такое действие уже зарегистрировано под именем '"+actionName+"'");
			}
		} else{
			httpAction = new HttpAction();
	        httpAction.setActionURL(httpActionDTO.getPath());
            httpAction.setActionName(actionName);
			httpAction.setActionMethod(httpActionDTO.getActionMethod());
			httpAction.setGenerated(false);
		}
        return httpAction;
	}

//	@Transactional
//    private <T extends ActionDescriptor> List<HttpAction> getHttpActions(
//            List<ActionEndpointDTO<T>> actions, UUID serviceID) {
//        List<HttpAction> httpActions = new ArrayList<>();
//        for (ActionEndpointDTO<T> actionDescriptor : actions) {
//            HttpActionDTO httpActionDTO =
//                    (HttpActionDTO) actionDescriptor.getActionDescriptor();
//	        String actionName = actionDescriptor.getActionName();
//	        HttpAction httpAction = persistenceService
//			        .findHttpAction(serviceID, httpActionDTO.getPath());
//	        if(httpAction!=null){
//		        if(httpAction.isGenerated()){
//			        httpAction.setGenerated(false);
//			        httpAction.setActionName(actionName);
//		        }else{
//			        throw new
//		        }
//	        }
////	        if (httpAction == null) {
////		        httpAction = new HttpAction();
////		        httpAction.setActionURL(httpActionDTO.getPath());
////	        }
////	        httpAction.setActionName(actionName);
//
//	        httpActions.add(httpAction);
//        }
//        return httpActions;
//    }
	@Transactional
	private <T extends ActionDescriptor> JMSAction getJmsAction(
			ActionEndpointDTO<T>actionDescriptor, UUID serviceID) {
		QueueDTO queueDTO =
				(QueueDTO) actionDescriptor.getActionDescriptor();
		String actionName = actionDescriptor.getActionName();
		JMSAction jmsAction = persistenceService
				.findJmsAction(serviceID, queueDTO.getQueueName(),
				               queueDTO.getUsername(),
				               queueDTO.getPassword());
		if(jmsAction!=null){
			if(jmsAction.isGenerated()){
				jmsAction.setGenerated(false);
				jmsAction.setActionName(actionName);
			}else{
				throw new TargetRegistrationException("Такое действие уже зарегистрировано под именем '"+actionName+"'");
			}
		} else{
			jmsAction = new JMSAction();
			jmsAction.setActionMethod(queueDTO.getActionMethod());
			jmsAction.setUsername(queueDTO.getUsername());
			jmsAction.setPassword(queueDTO.getPassword());
			jmsAction.setQueueName(queueDTO.getQueueName());
			jmsAction.setActionName(actionName);
			jmsAction.setGenerated(false);
		}

		return jmsAction;
	}
//	@Transactional
//    private <T extends ActionDescriptor> List<JMSAction> getJmsActions(
//		    List<ActionEndpointDTO<T>> actions, UUID serviceID) {
//        List<JMSAction> jmsActions = new ArrayList<>();
//        for (ActionEndpointDTO<T> actionDescriptor : actions) {
//            QueueDTO queueDTO =
//                    (QueueDTO) actionDescriptor.getActionDescriptor();
//            String actionName = actionDescriptor.getActionName();
//	        JMSAction jmsAction = persistenceService
//			        .findJmsAction(serviceID, queueDTO.getQueueName(),
//			                       queueDTO.getUsername(),
//			                       queueDTO.getPassword());
//	        if (jmsAction == null) {
//		        jmsAction = new JMSAction();
//		        jmsAction.setUsername(queueDTO.getUsername());
//		        jmsAction.setPassword(queueDTO.getPassword());
//		        jmsAction.setQueueName(queueDTO.getQueueName());
//	        }
//	        jmsAction.setActionName(actionName);
//
//	        jmsActions.add(jmsAction);
//        }
//        return jmsActions;
//    }
	@Transactional
	private HttpServiceEndpoint createHttpEntity(
			HttpEndpointDescriptorDTO descriptor,
			String serviceName, DeliverySettingsDTO deliverySettings) throws TargetRegistrationException{
		HttpServiceEndpoint endpoint = persistenceService
				.findHttpService(descriptor.getHost(), descriptor.getPort());
		if (endpoint != null) {
			if(endpoint.isGenerated()){
				endpoint.setServiceName(serviceName);
				endpoint.setGenerated(false);
				DeliverySettings settings;
				if (deliverySettings != null) {
					settings = new DeliverySettings();
					settings.setRetryDelay(deliverySettings.getRetryDelay());
					settings.setRetryNumber(deliverySettings.getRetryNumber());
					settings.setEndpoint(endpoint);
					endpoint.setDeliverySettings(settings);
				}
			}else{
				throw new TargetRegistrationException(
						"Данный сервис уже зарегистрирован под именем '" +
								endpoint.getServiceName() + "'");
			}
		} else {
			endpoint = new HttpServiceEndpoint();
			endpoint.setServiceName(serviceName);
			endpoint.setGenerated(false);
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
		}
		return endpoint;
	}

	@Transactional
	private JMSServiceEndpoint createJmsEntity(
			JMSEndpointDescriptorDTO descriptor,
			String serviceName, DeliverySettingsDTO deliverySettings) throws TargetRegistrationException{
		String jndiProperties;
		try {
			jndiProperties = mapper.
					writeValueAsString(descriptor.getJndiProperties());
		} catch (JsonProcessingException e) {
			throw new TargetRegistrationException(e);
		}
		String connectionFactory = descriptor.getConnectionFactory();
		JMSServiceEndpoint endpoint = persistenceService
				.findJmsService(connectionFactory,jndiProperties);
		if (endpoint != null) {
			if(endpoint.isGenerated()){
				endpoint.setServiceName(serviceName);
				endpoint.setGenerated(false);
				DeliverySettings settings;
				if (deliverySettings != null) {
					settings = new DeliverySettings();
					settings.setRetryDelay(deliverySettings.getRetryDelay());
					settings.setRetryNumber(deliverySettings.getRetryNumber());
					settings.setEndpoint(endpoint);
					endpoint.setDeliverySettings(settings);
				}
			}else{
				throw new TargetRegistrationException(
						"Данный сервис уже зарегистрирован под именем '" +
								endpoint.getServiceName() + "'");
			}
		} else {
			endpoint = new JMSServiceEndpoint();
			endpoint.setGenerated(false);
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
		}
		return endpoint;
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
		String referenceObject =
				mapper.writeValueAsString(packet.getReferenceObject());

		autoDetectionPacket.setReferenceObject(referenceObject);
		List<RegistrationDestinationDescriptor> destinationDescriptors =
				packet.getDestinationDescriptors();
		for (RegistrationDestinationDescriptor regDD : destinationDescriptors) {
			DestinationDescriptor destinationDescriptor =
					regDD.getDestinationDescriptor();
			DestinationEntity persistentDestination;
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
		if(!autoDetectionPacket.getDestinations().isEmpty()){
			persistenceService.persist(autoDetectionPacket);
		}
		return result;
	}
}
