package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
import com.icl.integrator.util.IntegratorException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by BigBlackBug on 2/11/14.
 */
@Service
public class DeliveryCreator {

	private static Log logger = LogFactory.getLog(DeliveryCreator.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JsonMatcher jsonMatcher;

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
	public PersistentDestination persistDestination(
			DestinationDescriptor destination) {
		if (destination == null) {
			return null;
		}

		AbstractActionEntity actionEntity = null;
		AbstractEndpointEntity endpointEntity = null;

		DestinationDescriptor.DescriptorType descriptorType =
				destination.getDescriptorType();
		if (descriptorType == DestinationDescriptor.DescriptorType.RAW) {
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
					actionEntity =action;
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
					actionEntity = action;
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
								realSourceService.getService());
				actionEntity = persistenceService
						.getHttpAction(realSourceService.getAction(),
						               endpointEntity.getId());
			} else if (endpointType == EndpointType.JMS) {
				endpointEntity =
						persistenceService.getJmsService(
								realSourceService.getService());
				actionEntity = persistenceService.getJmsAction(
						realSourceService.getAction(),
						endpointEntity.getId());
			}
		}
		return new PersistentDestination(endpointEntity, actionEntity);
	}

	@Transactional
	public <T> Delivery createDelivery(AbstractEndpointEntity service,
	                                   AbstractActionEntity action,
	                                   T data) throws JsonProcessingException {
		service = persistenceService
				.find(AbstractEndpointEntity.class, service.getId());
		action = persistenceService
				.find(AbstractActionEntity.class, action.getId());
		Delivery delivery = new Delivery();
		delivery.setAction(action);
		delivery.setDeliveryData(objectMapper.writeValueAsString(data));
		delivery.setRequestDate(new Date());
		delivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
		delivery.setEndpoint(service);
		persistenceService.persist(delivery);
		return delivery;
	}

	private <T> Deliveries createDeliveries(DeliveryDTO deliveryDTO, T data) {
		Deliveries deliveries = new Deliveries();
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
						endpointEntity, actionEntity, data);
				persistenceService.persist(delivery);
				deliveries.addDelivery(delivery);
			} catch (Exception ex) {
				logger.error("Error creating delivery packet for destination",
				             ex);
				deliveries.addError(destination.getServiceName(), ex);
			}
		}
		return deliveries;
	}

	@Transactional
	public Deliveries createDeliveries(DeliveryDTO deliveryDTO)
			throws JsonProcessingException {       //TODO exc handle
		logger.info("Creating a delivery packet");

		RequestDataDTO requestData = deliveryDTO.getRequestData();
		Deliveries deliveries;
		if (requestData.getDeliveryType() == DeliveryType.UNDEFINED) {
			deliveries = createDeliveries(deliveryDTO, requestData);
		} else {
			if (deliveryDTO.getDestinations() != null) {
				deliveries =
						createDeliveries(deliveryDTO, requestData);
			} else {
				deliveries = createAutoDetectedDeliveries(deliveryDTO);
			}
		}
		return deliveries;
	}

	@Transactional
	private Deliveries createAutoDetectedDeliveries(DeliveryDTO deliveryDTO) {
		RequestDataDTO requestData = deliveryDTO.getRequestData();
		DeliveryType deliveryType = requestData.getDeliveryType();
		List<AutoDetectionPacket> autoDetectionPackets =
				persistenceService.findAutoDetectionPackets(deliveryType);
		Deliveries deliveries = new Deliveries();
		for (AutoDetectionPacket packet : autoDetectionPackets) {
			Object data = requestData.getData();
			JsonNode dataJson = objectMapper.valueToTree(data);
			String referenceObject = packet.getReferenceObject();
			JsonNode referenceJson;
			try {
				referenceJson = objectMapper.readTree(referenceObject);
			} catch (IOException e) {
				throw new IntegratorException(e);
			}
			if (jsonMatcher.matches(dataJson, referenceJson)) {
				List<DestinationEntity> destinations = packet.getDestinations();
				for (DestinationEntity destination : destinations) {
					try {
						Delivery delivery = createDelivery(
								destination.getService(),
								destination.getAction(), requestData);
						deliveries.addDelivery(delivery);
					} catch (JsonProcessingException e) {
						throw new IntegratorException(e);
					}
				}

			}
		}
		return deliveries;
	}

}
