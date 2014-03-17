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
import com.icl.integrator.dto.util.EndpointType;
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

	private HttpAction createHttpAction(HttpServiceEndpoint service,
	                                    HttpActionDTO httpActionDTO) {
		HttpAction httpAction = new HttpAction();
		httpAction.setActionURL(httpActionDTO.getPath());
		httpAction.setGenerated(true);
		httpAction.setActionName(generateActionName(service.getServiceName()));
		httpAction.setEndpoint(service);
		httpAction.setActionMethod(httpActionDTO.getActionMethod());
		service.addAction(httpAction);
		return httpAction;
	}

	private JMSAction createJmsAction(JMSServiceEndpoint service,
	                                  QueueDTO queueDTO) {
		JMSAction jmsAction = new JMSAction();
		jmsAction.setPassword(queueDTO.getPassword());
		jmsAction.setGenerated(true);
		jmsAction.setQueueName(queueDTO.getQueueName());
		jmsAction.setUsername(queueDTO.getUsername());
		jmsAction.setActionName(generateActionName(service.getServiceName()));
		jmsAction.setActionMethod(queueDTO.getActionMethod());
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
	public DestinationEntity persistDestination(DestinationDescriptor destination) {
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
					service.setGenerated(true);
					service.setServiceName(generateServiceName());
					service.setServiceURL(realDescriptor.getHost());
					service.setServicePort(realDescriptor.getPort());
					HttpAction action = createHttpAction(service, httpActionDTO);
					endpointEntity = persistenceService.persist(service);
					actionEntity = action;
				} else {
					endpointEntity = service;
					HttpAction action = persistenceService.findHttpAction(
							service.getId(), httpActionDTO.getPath());
					if (action == null) {
						action = createHttpAction(service, httpActionDTO);
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
					service.setGenerated(true);
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
		DestinationEntity destinationEntity = new DestinationEntity(endpointEntity, actionEntity);
		return persistenceService.saveOrUpdate(destinationEntity);
	}

	@Transactional
	public <T> Delivery createDelivery(AbstractEndpointEntity service,
	                                   AbstractActionEntity action,
	                                   T data,
	                                   DestinationEntity destinationEntity) throws JsonProcessingException {
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
		delivery.setResponseHandlerDestination(destinationEntity);
//		delivery.setGeneralDelivery(isGeneral);
		persistenceService.persist(delivery);
		return delivery;
	}

	private <T> Deliveries createDeliveries(DeliveryDTO deliveryDTO, T data,
	                                        DestinationDescriptor responseHandlerDescriptor) {
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
				DestinationEntity destinationEntity = persistDestination(responseHandlerDescriptor);
				Delivery delivery = createDelivery(endpointEntity, actionEntity,
				                                   data, destinationEntity);
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
	public Deliveries createDeliveries(DeliveryDTO deliveryDTO,
	                                   DestinationDescriptor responseHandlerDescriptor)
			throws JsonProcessingException {
		logger.info("Creating a delivery packet");

		RequestDataDTO requestData = deliveryDTO.getRequestData();
		Deliveries deliveries;
		if (requestData.getDeliveryPacketType() == DeliveryPacketType.UNDEFINED) {
			deliveries = createDeliveries(deliveryDTO, requestData, responseHandlerDescriptor);
		} else {
			if (deliveryDTO.getDestinations() != null) {
				deliveries =
						createDeliveries(deliveryDTO, requestData, responseHandlerDescriptor);
			} else {
				deliveries = createAutoDetectedDeliveries(deliveryDTO, responseHandlerDescriptor);
			}
		}
		return deliveries;
	}

	@Transactional
	private Deliveries createAutoDetectedDeliveries(DeliveryDTO deliveryDTO,
	                                                DestinationDescriptor responseHandlerDescriptor)
			throws IntegratorException {
		RequestDataDTO requestData = deliveryDTO.getRequestData();
		DeliveryPacketType deliveryPacketType = requestData.getDeliveryPacketType();
		List<AutoDetectionPacket> autoDetectionPackets =
				persistenceService.findAutoDetectionPackets(deliveryPacketType);
		if (autoDetectionPackets.isEmpty()) {
			throw new IntegratorException("Не могу автоматически определить целевые сервисы");
		}
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
						DestinationEntity destinationEntity =
								persistDestination(responseHandlerDescriptor);
						Delivery delivery = createDelivery(
								destination.getService(),
								destination.getAction(),
								requestData,
								destinationEntity);
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
