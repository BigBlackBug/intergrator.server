package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.editor.EditActionDTO;
import com.icl.integrator.dto.editor.EditServiceDTO;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import com.icl.integrator.dto.util.EndpointType;
import com.icl.integrator.model.*;
import com.icl.integrator.util.IntegratorException;
import com.icl.integrator.util.connectors.ConnectionException;
import com.icl.integrator.util.connectors.EndpointConnector;
import com.icl.integrator.util.connectors.EndpointConnectorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 21.01.14
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
@Service
public class IntegratorWorkerService {

	@Autowired
	private EndpointConnectorFactory connectorFactory;

	@Autowired
	private PersistenceService persistenceService;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private ObjectMapper objectMapper;

	@Transactional(noRollbackFor = ConnectionException.class)
	public <T extends ActionDescriptor>
	List<ActionRegistrationResultDTO> registerService(
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
				registrationService.registerService(endpoint, registrationDTO, actions);
		regResult.addAll(result);
		return regResult;
	}

	private <T extends ActionDescriptor>
	void testConnection(EndpointDescriptor endpoint, ActionEndpointDTO<T> action)
			throws ConnectionException {
		EndpointConnector connector =
				connectorFactory.createEndpointConnector(endpoint, action.getActionDescriptor());
		connector.testConnection();
	}

	@Transactional
	public <T extends ActionDescriptor> void addAction(
			AddActionDTO<T> actionDTO) throws IntegratorException {
		ActionRegistrationDTO<T> actionReg = actionDTO.getActionRegistration();
		String serviceName = actionDTO.getServiceName();
		AbstractEndpointEntity entity = persistenceService.findService(serviceName);
		EndpointType endpointType = entity.getType();
		ActionEndpointDTO<T> action = actionReg.getAction();
		EndpointType actionEndpointType = action.getActionDescriptor().getEndpointType();
		if (endpointType != actionEndpointType) {
			throw new IntegratorException(
					String.format("Тип добавляемого действия '%s' не совпадает с" +
							              " типом сервиса '%s'", actionEndpointType, endpointType)
			);
		}
		String actionName = action.getActionName();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		IntegratorUser creator = (IntegratorUser) authentication.getPrincipal();

		if (endpointType == EndpointType.HTTP) {
			HttpServiceEndpoint serviceEndpoint = (HttpServiceEndpoint) entity;
			HttpActionDTO httpActionDTO = (HttpActionDTO) action.getActionDescriptor();

			HttpAction httpAction = persistenceService.findHttpAction(
					serviceEndpoint.getId(), httpActionDTO.getPath());
			if (httpAction != null) {
				if (httpAction.isGenerated()) {
					httpAction.setCreator(creator);
					httpAction.setActionName(actionName);
				} else {
					throw new TargetRegistrationException(
							"Такое действие уже зарегистрировано под именем '" + actionName + "'");
				}
			} else {
				httpAction = new HttpAction();
				httpAction.setActionURL(httpActionDTO.getPath());
				httpAction.setActionName(actionName);
				httpAction.setCreator(creator);
				httpAction.setActionMethod(httpActionDTO.getActionMethod());
				httpAction.setEndpoint(serviceEndpoint);
				serviceEndpoint.addAction(httpAction);
			}
		} else if (endpointType == EndpointType.JMS) {
			JMSServiceEndpoint serviceEndpoint = (JMSServiceEndpoint) entity;
			QueueDTO queueDTO = (QueueDTO) action.getActionDescriptor();
			JMSAction jmsAction = persistenceService
					.findJmsAction(serviceEndpoint.getId(),
					               queueDTO.getQueueName(),
					               queueDTO.getUsername(),
					               queueDTO.getPassword());

			if (jmsAction != null) {
				if (jmsAction.isGenerated()) {
					jmsAction.setCreator(creator);
					jmsAction.setActionName(actionName);
				} else {
					throw new TargetRegistrationException(
							"Такое действие уже зарегистрировано под именем '" + actionName + "'");
				}
			} else {
				jmsAction = new JMSAction();
				jmsAction.setActionMethod(queueDTO.getActionMethod());
				jmsAction.setUsername(queueDTO.getUsername());
				jmsAction.setPassword(queueDTO.getPassword());
				jmsAction.setQueueName(queueDTO.getQueueName());
				jmsAction.setActionName(actionName);
				jmsAction.setCreator(creator);
				jmsAction.setEndpoint(serviceEndpoint);
				serviceEndpoint.addAction(jmsAction);
			}
		}
	}

	public <T extends ActionDescriptor>
	List<ActionEndpointDTO<T>> getSupportedActions(String serviceName) {
		List<AbstractActionEntity> actions = persistenceService.getActions(serviceName);
		List<ActionEndpointDTO<T>> result = new ArrayList<>();
		for (AbstractActionEntity actionEntity : actions) {
			ActionEndpointDTO<T> dto = convertActionToDTO(actionEntity);
			result.add(dto);
		}
		return result;
	}

	public Boolean pingService(ServiceDestinationDescriptor serviceDescriptor)
			throws ConnectionException {
		EndpointConnector connector = connectorFactory.createEndpointConnector(serviceDescriptor);
		connector.testConnection();
		return true;
	}

	public List<ServiceDTO> getServiceList() {
		return persistenceService.getAllServices();
	}

	@SuppressWarnings("unchecked")
	public <Y extends ActionDescriptor> FullServiceDTO<Y>
	getServiceInfo(String serviceName) {
		AbstractEndpointEntity<AbstractActionEntity> entity =
				persistenceService.findService(serviceName);
		String creatorName = entity.getCreator().getUsername();
		DeliverySettings deliverySettings = entity.getDeliverySettings();
		DeliverySettingsDTO deliverySettingsDTO = new DeliverySettingsDTO(
				deliverySettings.getRetryNumber(), deliverySettings.getRetryDelay());
		EndpointDescriptor endpointDescriptor = createEndpointDescriptor(entity);
		List<ActionEndpointDTO<ActionDescriptor>> result = new ArrayList<>();
		for (AbstractActionEntity action : entity.getActions()) {
			result.add(new ActionEndpointDTO<>(action.getActionName(), createActionDTO(action)));
		}
		return (FullServiceDTO<Y>) new FullServiceDTO<>(serviceName, endpointDescriptor,
		                                                deliverySettingsDTO, creatorName,
		                                                result);
	}

	private ActionDescriptor createActionDTO(AbstractActionEntity action) {
		if (action.getType() == EndpointType.HTTP) {
			HttpAction realAction = (HttpAction) action;
			return new HttpActionDTO(
					realAction.getActionURL(), realAction.getActionMethod());
		} else {
			JMSAction realAction = (JMSAction) action;
			return new QueueDTO(realAction.getQueueName(),
			                    realAction.getUsername(),
			                    realAction.getPassword(),
			                    realAction.getActionMethod());
		}
	}

	public List<DeliveryActionsDTO> getAllActionMap() {
		Map<String, List<AbstractEndpointEntity>> allActionMap =
				persistenceService.getAllActionMap();
		List<DeliveryActionsDTO> list = new ArrayList<>();
		for (Map.Entry<String, List<AbstractEndpointEntity>> entry : allActionMap.entrySet()) {
			List<AbstractEndpointEntity> endpoints = entry.getValue();
			List<ServiceDTO> serviceList = new ArrayList<>();
			for (AbstractEndpointEntity endpoint : endpoints) {
				serviceList.add(new ServiceDTO(endpoint.getServiceName(),
				                               endpoint.getType(),
				                               endpoint.getCreator().getUsername()));
			}
			list.add(new DeliveryActionsDTO(entry.getKey(), serviceList));
		}
		return list;
	}

	public <T extends ActionDescriptor>
	List<ServiceAndActions<T>> getServicesSupportingActionType(ActionMethod actionMethod) {
		Map<AbstractEndpointEntity, List<AbstractActionEntity>> servicesSupportingActionType =
				persistenceService.getServicesSupportingActionType(actionMethod);
		List<ServiceAndActions<T>> result = new ArrayList<>();
		for (Map.Entry<AbstractEndpointEntity, List<AbstractActionEntity>>
				entry : servicesSupportingActionType.entrySet()) {
			List<AbstractActionEntity> actions = entry.getValue();
			List<ActionEndpointDTO<T>> actionDTOs = new ArrayList<>();
			for (AbstractActionEntity action : actions) {
				ActionEndpointDTO<T> dto = convertActionToDTO(action);
				actionDTOs.add(dto);
			}
			result.add(new ServiceAndActions<>(entityToDTO(entry.getKey()), actionDTOs));
		}
		return result;
	}

	private ServiceDTO entityToDTO(AbstractEndpointEntity endpoint) {
		return new ServiceDTO(endpoint.getServiceName(), endpoint.getType(),
		                      endpoint.getCreator().getUsername());
	}

	private <T extends ActionDescriptor> ActionEndpointDTO<T>
	convertActionToDTO(AbstractActionEntity actionEntity) {
		EndpointType type = actionEntity.getType();
		switch (type) {
			case HTTP: {
				HttpAction httpAction = (HttpAction) actionEntity;
				HttpActionDTO httpActionDTO = new HttpActionDTO(httpAction.getActionURL(),
				                                                   actionEntity.getActionMethod());
				return new ActionEndpointDTO(actionEntity.getActionName(), httpActionDTO);
			}
			case JMS: {
				JMSAction httpAction = (JMSAction) actionEntity;
				QueueDTO httpActionDTO = new QueueDTO(httpAction.getQueueName(),
				                                              httpAction.getUsername(),
				                                              httpAction.getPassword(),
				                                              actionEntity.getActionMethod());
				return new ActionEndpointDTO(actionEntity.getActionName(), httpActionDTO);
			}
		}
		return null;
	}

	//TODO remove nonexistent. СУКА ТЕСТЫ ТЫ НАПИШЕШЬ КОГДА-НИБУДЬ???
	public void removeService(String serviceName) {
		persistenceService.removeService(serviceName);
	}

	public void registerUser(UserCredentialsDTO packet) {
		final IntegratorUser user = new IntegratorUser();
		user.setUsername(packet.getUsername());
		user.setPassword(DigestUtils.md5DigestAsHex(packet.getPassword().getBytes()));
		user.setRole(RoleEnum.ROLE_USER);
		persistenceService.persist(user);
	}

	//TODO как-нибудь отрефакторить всё это говно с ифами и прочим.
	@Transactional
	public void editService(EditServiceDTO editServiceDTO) throws IntegratorException {
		String serviceName = editServiceDTO.getServiceName();
		DeliverySettingsDTO newDeliverySettings = editServiceDTO.getDeliverySettings();
		EndpointDescriptor endpointDescriptor = editServiceDTO.getEndpointDescriptor();
		String newServiceName = editServiceDTO.getNewServiceName();
		AbstractEndpointEntity endpointEntity = persistenceService.findService(serviceName);
		if (newServiceName != null) {
			endpointEntity.setServiceName(newServiceName);
		}
		if (newDeliverySettings != null) {
			DeliverySettings deliverySettings = endpointEntity.getDeliverySettings();
			deliverySettings.setRetryDelay(newDeliverySettings.getRetryDelay());
			deliverySettings.setRetryNumber(newDeliverySettings.getRetryNumber());
		}
		if (endpointDescriptor != null) {
			EndpointType newEndpointType = endpointDescriptor.getEndpointType();
			EndpointType oldEndpointType = endpointEntity.getType();
			if (newEndpointType != oldEndpointType) {
				throw new IntegratorException(
						MessageFormat.format(
								"Тип нового дескриптора ({0}) не совпадает с типом сервиса ({1})",
								newEndpointType,
								oldEndpointType)
				);
			} else {
				if (newEndpointType == EndpointType.HTTP) {
					HttpEndpointDescriptorDTO httpDesc =
							(HttpEndpointDescriptorDTO) endpointDescriptor;
					HttpServiceEndpoint ep = (HttpServiceEndpoint) endpointEntity;
					ep.setServicePort(httpDesc.getPort());
					ep.setServiceURL(httpDesc.getHost());
				} else if (newEndpointType == EndpointType.JMS) {
					JMSEndpointDescriptorDTO jmsDesc =
							(JMSEndpointDescriptorDTO) endpointDescriptor;
					JMSServiceEndpoint ep = (JMSServiceEndpoint) endpointEntity;
					ep.setConnectionFactory(jmsDesc.getConnectionFactory());
					String jndiProperties;
					try {
						jndiProperties =
								objectMapper.writeValueAsString(jmsDesc.getJndiProperties());
					} catch (JsonProcessingException e) {
						throw new TargetRegistrationException(e);
					}
					ep.setJndiProperties(jndiProperties);
				}
			}
		}
	}

	@Transactional
	public void editAction(EditActionDTO editActionDTO) {
		String actionName = editActionDTO.getActionName();
		String serviceName = editActionDTO.getServiceName();
		String newActionName = editActionDTO.getNewActionName();
		ActionDescriptor actionDescriptor = editActionDTO.getActionDescriptor();
		AbstractEndpointEntity service = persistenceService.findService(serviceName);
		AbstractActionEntity actionEntity = service.getActionByName(actionName);
		if (actionEntity == null) {
			throw new IntegratorException(
					String.format("Сервис %s не поддерживает действие с именем %s",
					              serviceName, actionName)
			);
		}
		if (newActionName != null) {
			actionEntity.setActionName(newActionName);
		}
		if (actionDescriptor != null) {
			EndpointType newEndpointType = actionDescriptor.getEndpointType();
			EndpointType oldEndpointType = actionEntity.getType();
			if (newEndpointType != oldEndpointType) {
				throw new IntegratorException(
						MessageFormat.format(
								"Тип нового дескриптора ({0}) не совпадает с типом действия ({1})",
								newEndpointType,
								oldEndpointType)
				);
			} else {
				if (!editActionDTO.isForceChanges()) {
					EndpointConnector endpointConnector = connectorFactory
							.createEndpointConnector(createEndpointDescriptor(service),
							                         actionDescriptor);
					endpointConnector.testConnection();
				}
				actionEntity.setActionMethod(actionDescriptor.getActionMethod());
				if (newEndpointType == EndpointType.HTTP) {
					HttpActionDTO httpDesc = (HttpActionDTO) actionDescriptor;
					HttpAction ep = (HttpAction) actionEntity;
					ep.setActionURL(httpDesc.getPath());
				} else if (newEndpointType == EndpointType.JMS) {
					QueueDTO queueDesc = (QueueDTO) actionDescriptor;
					JMSAction ep = (JMSAction) actionEntity;
					ep.setQueueName(queueDesc.getQueueName());
					ep.setUsername(queueDesc.getUsername());
					ep.setPassword(queueDesc.getPassword());
				}
			}
		}
	}

	private EndpointDescriptor createEndpointDescriptor(AbstractEndpointEntity service) {
		if (service.getType() == EndpointType.HTTP) {
			HttpServiceEndpoint httpService = (HttpServiceEndpoint) service;
			return new HttpEndpointDescriptorDTO(httpService.getServiceURL(),
			                                     httpService.getServicePort());
		} else {
			JMSServiceEndpoint jmsService = (JMSServiceEndpoint) service;
			Map<String, String> props;
			try {
				TypeReference<Map<String, String>> typeReference =
						new TypeReference<Map<String, String>>() {
						};
				props = objectMapper.readValue(jmsService.getJndiProperties(), typeReference);
			} catch (IOException e) {
				throw new IntegratorException("Unexpected error", e);
			}
			return new JMSEndpointDescriptorDTO(jmsService.getConnectionFactory(), props);
		}
	}
}
