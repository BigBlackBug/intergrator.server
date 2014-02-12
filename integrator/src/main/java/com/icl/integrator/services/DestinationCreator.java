package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.*;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by BigBlackBug on 2/11/14.
 */
@Service
public class DestinationCreator {

	private static Log logger = LogFactory.getLog(DestinationCreator.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PersistenceService persistenceService;

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
		return "ACTION_GENERATED_FOR_'" + serviceName + "'_AT_" +
				System.currentTimeMillis();
	}

	private String generateServiceName() {
		return "SERVICE_GENERATED_AT_" + System.currentTimeMillis();
	}

	@Transactional
	public PersistentDestination getPersistentDestination(
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
					HttpAction action =
							createNewAction(service, httpActionDTO);
					endpointEntity = persistenceService.persist(service);
					actionEntity = persistenceService.refresh(action);
				} else {
					endpointEntity = service;
					HttpAction action = persistenceService.findHttpAction(
							service.getId(), httpActionDTO.getPath());
					if (action == null) {
						action = createNewAction(service, httpActionDTO);
						actionEntity = persistenceService.persist(action);
					} else {
						actionEntity = action;
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
					JMSAction action =
							createJmsAction(service, queueDTO);
					endpointEntity = persistenceService.persist(service);
					actionEntity = persistenceService.refresh(action);
				} else {
					endpointEntity = service;
					JMSAction action = persistenceService.findJmsAction(
							service.getId(), queueDTO.getQueueName(),
							queueDTO.getUsername(), queueDTO.getPassword());
					if (action == null) {
						action = createJmsAction(service, queueDTO);
						actionEntity = persistenceService.persist(action);
					} else {
						actionEntity = action;
					}
				}
			}
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
		return new PersistentDestination(endpointEntity, actionEntity);
	}

	@Transactional
	public Delivery createDelivery(AbstractEndpointEntity service,
	                               AbstractActionEntity action){
		service = persistenceService
				.find(AbstractEndpointEntity.class, service.getId());
		action = persistenceService
				.find(AbstractActionEntity.class, action.getId());
		Delivery sourceDelivery = new Delivery();
		sourceDelivery.setAction(action);
		sourceDelivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
		sourceDelivery.setEndpoint(service);
		persistenceService.persist(sourceDelivery);
		return sourceDelivery;
	}

	@Transactional
	public Holder createDeliveryPacket(DeliveryDTO deliveryDTO)
			throws JsonProcessingException {
		logger.info("Creating a delivery packet");
		Map<String, ResponseDTO<UUID>> serviceToRequestID = new HashMap<>();
		DeliveryPacket deliveryPacket = new DeliveryPacket();
		deliveryPacket.setDeliveryData(
				objectMapper.writeValueAsString(deliveryDTO.getRequestData()));
		deliveryPacket.setRequestDate(new Date());
		for (ServiceDTO destination : deliveryDTO.getDestinations()) {
			try {
				AbstractActionEntity actionEntity = null;
				AbstractEndpointEntity endpointEntity = null;
				EndpointType endpointType = destination.getEndpointType();
				if (endpointType == EndpointType.HTTP) {
					endpointEntity =
							persistenceService.getHttpService(
									destination.getServiceName());
					actionEntity = persistenceService
							.getHttpAction(deliveryDTO.getAction(),
							               endpointEntity.getId());
				} else if (endpointType == EndpointType.JMS) {
					endpointEntity =
							persistenceService.getJmsService(
									destination.getServiceName());
					actionEntity = persistenceService.getJmsAction(
							deliveryDTO.getAction(), endpointEntity.getId());
				}
				Delivery delivery = createDelivery(
						endpointEntity,actionEntity);

				delivery.setDeliveryPacket(deliveryPacket);
				deliveryPacket.addDelivery(delivery);
				persistenceService.persist(deliveryPacket);
			} catch (Exception ex) {
				logger.error("Error creating delivery packet for destination",
				             ex);
				ErrorDTO error = new ErrorDTO(ex);
				serviceToRequestID.put(destination.getServiceName(),
				                       new ResponseDTO<UUID>(error));
			}
		}
		return new Holder(serviceToRequestID, deliveryPacket);
	}

	public static class Holder {

		private final Map<String, ResponseDTO<UUID>> responseMap;

		private final DeliveryPacket packet;

		private Holder(
				Map<String, ResponseDTO<UUID>> responseMap,
				DeliveryPacket packet) {
			this.responseMap = responseMap;
			this.packet = packet;
		}

		public Map<String, ResponseDTO<UUID>> getResponseMap() {
			return responseMap;
		}

		public DeliveryPacket getPacket() {
			return packet;
		}
	}


}
