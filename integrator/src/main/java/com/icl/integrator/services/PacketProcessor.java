package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.HttpActionDTO;
import com.icl.integrator.dto.registration.QueueDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import com.icl.integrator.model.*;
import com.icl.integrator.util.EndpointType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 10:01
 * To change this template use File | Settings | File Templates.
 */
public class PacketProcessor {

	private static Log logger = LogFactory.getLog(PacketProcessor.class);

	@Autowired
	private DeliveryService deliveryService;

	@Autowired
	private PersistenceService persistenceService;

	@Autowired
	private ObjectMapper objectMapper;

	//    public Map<String, ResponseDTO<UUID>> process(DeliveryDTO packet) {
//        Map<String, ResponseDTO<UUID>> serviceToRequestID = new
//                HashMap<>();
//        for (DestinationDTO destination : packet.getDestinations()) {
//            ResponseDTO<UUID> response;
//            try {
//                UUID requestID = deliveryService.deliver(destination, packet);
//                response = new ResponseDTO<>(requestID, UUID.class);
//            } catch (IntegratorException ex) {
//                ErrorDTO error = new ErrorDTO(ex);
//                response = new ResponseDTO<>(error);
//            }
//            serviceToRequestID.put(destination.getServiceName(), response);
//        }
//        return serviceToRequestID;
//    }

	private HttpAction createNewAction(HttpServiceEndpoint service,
	                                   HttpActionDTO httpActionDTO) {
		HttpAction httpAction = new HttpAction();
		httpAction.setActionURL(httpActionDTO.getPath());
		httpAction.setActionName(generateActionName(service.getServiceName()));
		httpAction.setEndpoint(service);
		service.addAction(httpAction);
		return httpAction;
	}

	private JMSAction createJmsAction(JMSServiceEndpoint service,
	                                  QueueDTO queueDTO) {
		JMSAction jmsAction = new JMSAction();
		jmsAction.setPassword(queueDTO.getPassword());
		jmsAction.setQueueName(queueDTO.getQueueName());
		jmsAction.setUsername(queueDTO.getUsername());
		jmsAction.setActionName(generateActionName(service.getServiceName()));
		jmsAction.setEndpoint(service);
		service.addAction(jmsAction);
		return jmsAction;
	}

	private String generateActionName(String serviceName) {
		return "ACTION_GENERATED_FOR_'" + serviceName + "'AT_" +
				System.currentTimeMillis();
	}

	private String generateServiceName() {
		return "SERVICE_GENERATED_AT_" + System.currentTimeMillis();
	}

	private ResponseDeliveryInfo createResponseDeliveryInfo(
			DestinationDescriptor destination) {
		if (destination == null) {
			return null;
		}

		AbstractActionEntity actionEntity = null;
		AbstractEndpointEntity endpointEntity = null;

		DestinationDescriptor.DescriptorType descriptorType =
				destination.getDescriptorType();
		if (descriptorType == DestinationDescriptor.DescriptorType.RAW) {
			//TODO rethink. maybe delete instead of doing fullscan?
			RawDestinationDescriptor realSourceService =
					(RawDestinationDescriptor) destination;
			EndpointDTO endpoint = realSourceService.getEndpoint();
			EndpointType endpointType = endpoint.getEndpointType();
			EndpointDescriptor descriptor = endpoint.getDescriptor();

			if (endpointType == EndpointType.HTTP) {
				HttpEndpointDescriptorDTO realDescriptor =
						(HttpEndpointDescriptorDTO) descriptor;
				HttpActionDTO httpActionDTO =
						(HttpActionDTO) realSourceService
								.getActionDescriptor();
				HttpServiceEndpoint service =
						persistenceService.findHttpService(
								realDescriptor.getHost(),
								realDescriptor.getPort());
				if (service == null) {
					service = new HttpServiceEndpoint();
					DeliverySettings defaultSettings =
							DeliverySettings.createDefaultSettings();
					service.setDeliverySettings(defaultSettings);
					defaultSettings.setEndpoint(service);
					service.setServiceName(generateServiceName());
					service.setServiceURL(realDescriptor.getHost());
					service.setServicePort(realDescriptor.getPort());
					actionEntity = createNewAction(service, httpActionDTO);
					persistenceService.persist(service);
					endpointEntity = service;
				} else {
					HttpAction action = persistenceService.findHttpAction(
							service.getId(), httpActionDTO.getPath());
					if (action == null) {
						action = createNewAction(service, httpActionDTO);
						actionEntity = persistenceService.merge(action);
					}
				}
			} else if (endpointType == EndpointType.JMS) {
				JMSEndpointDescriptorDTO realDescriptor =
						(JMSEndpointDescriptorDTO) descriptor;
				QueueDTO queueDTO =
						(QueueDTO) realSourceService
								.getActionDescriptor();
				Map<String, String> jndiProperties =
						realDescriptor.getJndiProperties();
				String jndiPropertiesString = null;
				try {
					jndiPropertiesString =
							objectMapper.writeValueAsString(jndiProperties);
				} catch (JsonProcessingException e) {
				}
				JMSServiceEndpoint service =
						persistenceService.findJmsService(
								realDescriptor.getConnectionFactory(),
								jndiPropertiesString);
				if (service == null) {
					service = new JMSServiceEndpoint();
					DeliverySettings defaultSettings =
							DeliverySettings.createDefaultSettings();
					service.setDeliverySettings(defaultSettings);
					defaultSettings.setEndpoint(service);
					service.setServiceName(generateServiceName());
					service.setConnectionFactory(
							realDescriptor.getConnectionFactory());
					service.setJndiProperties(jndiPropertiesString);
					actionEntity = createJmsAction(service, queueDTO);
					persistenceService.persist(service);
					endpointEntity = service;
				} else {
					JMSAction action = persistenceService.findJmsAction(
							service.getId(), queueDTO.getQueueName(),
							queueDTO.getUsername(), queueDTO.getPassword());
					if (action == null) {
						action = createJmsAction(service, queueDTO);
						actionEntity = persistenceService.merge(action);
					}
				}
			}

			//TODO create new or find based on
		} else if (descriptorType ==
				DestinationDescriptor.DescriptorType.SERVICE) {
			ServiceDestinationDescriptor realSourceService =
					(ServiceDestinationDescriptor) destination;
			EndpointType endpointType = realSourceService.getEndpointType();
			if (endpointType == EndpointType.HTTP) {
				endpointEntity =
						persistenceService.getHttpService(
								realSourceService.getServiceName());
				actionEntity = persistenceService
						.getHttpAction(realSourceService.getActionName(),
						               endpointEntity.getId());
			} else if (endpointType == EndpointType.JMS) {
				endpointEntity =
						persistenceService.getJmsService(
								realSourceService.getServiceName());
				actionEntity = persistenceService.getJmsAction(
						realSourceService.getActionName(),
						endpointEntity.getId());
			}
		}
		return new ResponseDeliveryInfo(endpointEntity, actionEntity);
	}

	public Map<String, ResponseDTO<UUID>> process(DeliveryPacket deliveryPacket,
	                                              DestinationDescriptor destinationDescriptor) {
		//mb null
		ResponseDeliveryInfo responseDeliveryInfo = createResponseDeliveryInfo(
				destinationDescriptor);
		Map<String, ResponseDTO<UUID>> serviceToRequestID = new HashMap<>();
		for (Delivery delivery : deliveryPacket.getDeliveries()) {
			ResponseDTO<UUID> response;
			try {
				UUID requestID =
						deliveryService.deliver(delivery, responseDeliveryInfo);
				response = new ResponseDTO<>(requestID, UUID.class);
			} catch (Exception ex) {
				ErrorDTO error = new ErrorDTO(ex);
				response = new ResponseDTO<>(error);
			}
			serviceToRequestID
					.put(delivery.getEndpoint().getServiceName(), response);
		}
		return serviceToRequestID;
	}
}
