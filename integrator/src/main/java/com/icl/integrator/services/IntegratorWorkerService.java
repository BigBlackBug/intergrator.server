package com.icl.integrator.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.DeliveryActionsDTO;
import com.icl.integrator.dto.FullServiceDTO;
import com.icl.integrator.dto.ServiceAndActions;
import com.icl.integrator.dto.ServiceDTO;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import com.icl.integrator.dto.util.EndpointType;
import com.icl.integrator.model.*;
import com.icl.integrator.util.IntegratorException;
import com.icl.integrator.util.connectors.ConnectionException;
import com.icl.integrator.util.connectors.EndpointConnector;
import com.icl.integrator.util.connectors.EndpointConnectorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
	private ObjectMapper objectMapper;

	@Autowired
	private DeliveryCreator deliveryCreator;

	@Transactional
	public <T extends ActionDescriptor> void addAction(
			AddActionDTO<T> actionDTO) throws IntegratorException {
		ActionRegistrationDTO<T> actionReg =
				actionDTO.getActionRegistration();
		ServiceDTO service = actionDTO.getService();
		EndpointType endpointType = service.getEndpointType();

		ActionEndpointDTO<T> action = actionReg.getAction();
        EndpointType actionEndpointType = action.getActionDescriptor().getEndpointType();
        if (endpointType != actionEndpointType) {
            throw new IntegratorException(
                    String.format("Тип добавляемого действия '%s' не совпадает с" +
                                          " типом сервиса '%s'", actionEndpointType, endpointType));
        }
        String actionName = action.getActionName();

        if (endpointType == EndpointType.HTTP) {
            HttpServiceEndpoint serviceEndpoint =
					persistenceService.getHttpService(service.getServiceName());
			HttpActionDTO httpActionDTO = (HttpActionDTO) action.getActionDescriptor();

			HttpAction httpAction = persistenceService
					.findHttpAction(serviceEndpoint.getId(),
                                    httpActionDTO.getPath());
			if (httpAction != null) {
				if (httpAction.isGenerated()) {
					httpAction.setGenerated(false);
					httpAction.setActionName(actionName);
				} else {
					throw new TargetRegistrationException(
							"Такое действие уже зарегистрировано под именем '" + actionName + "'");
				}
			} else {
				httpAction = new HttpAction();
				httpAction.setActionURL(httpActionDTO.getPath());
				httpAction.setActionName(actionName);
				httpAction.setGenerated(false);
				httpAction.setActionMethod(httpActionDTO.getActionMethod());
				httpAction.setEndpoint(serviceEndpoint);
				serviceEndpoint.addAction(httpAction);
			}
		} else if (endpointType == EndpointType.JMS) {
			JMSServiceEndpoint serviceEndpoint =
					persistenceService.getJmsService(service.getServiceName());
			QueueDTO queueDTO = (QueueDTO) action.getActionDescriptor();
			JMSAction jmsAction = persistenceService
					.findJmsAction(serviceEndpoint.getId(),
                                   queueDTO.getQueueName(),
                                   queueDTO.getUsername(),
                                   queueDTO.getPassword());

			if (jmsAction != null) {
				if (jmsAction.isGenerated()) {
					jmsAction.setGenerated(false);
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
				jmsAction.setGenerated(false);
				jmsAction.setEndpoint(serviceEndpoint);
				serviceEndpoint.addAction(jmsAction);
			}
		}
	}

	public List<ActionEndpointDTO> getSupportedActions(ServiceDTO serviceDTO) {
        List<AbstractActionEntity> actions =
                persistenceService.getActions(serviceDTO.getServiceName());
        List<ActionEndpointDTO> result = new ArrayList<>();
        for (AbstractActionEntity actionEntity : actions) {
            result.add(convertActionToDTO(actionEntity));
        }
        return result;
    }

	public Boolean pingService(ServiceDestinationDescriptor serviceDescriptor)
            throws ConnectionException{
		EndpointConnector connector = connectorFactory.createEndpointConnector(serviceDescriptor);
		connector.testConnection();
		return true;
	}

	public List<ServiceDTO> getServiceList() {
		List<ServiceDTO> result = new ArrayList<>();
		List<HttpServiceEndpoint> httpServices =
				persistenceService.getHttpServices();
		for (HttpServiceEndpoint service : httpServices) {
			result.add(new ServiceDTO(service.getServiceName(),
			                          EndpointType.HTTP));
		}
		List<JMSServiceEndpoint> jmsServices =
				persistenceService.getJmsServices();
		for (JMSServiceEndpoint service : jmsServices) {
			result.add(new ServiceDTO(service.getServiceName(),
			                          EndpointType.JMS));
		}
		return result;
	}

	public <Y extends ActionDescriptor> FullServiceDTO<Y>
	getServiceInfo(ServiceDTO serviceDTO) {
		FullServiceDTO< Y> result = null;
		EndpointType endpointType = serviceDTO.getEndpointType();
		if (endpointType == EndpointType.HTTP) {
			FullServiceDTO<HttpActionDTO> httpResult = new FullServiceDTO<>();
			HttpServiceEndpoint httpService = persistenceService
					.getHttpService(serviceDTO.getServiceName());
            DeliverySettings deliverySettings = httpService.getDeliverySettings();
            DeliverySettingsDTO deliverySettingsDTO = new DeliverySettingsDTO(
                    deliverySettings.getRetryNumber(), deliverySettings.getRetryDelay());
            httpResult.setDeliverySettings(deliverySettingsDTO);
			HttpEndpointDescriptorDTO httpEndpointDescriptorDTO =
					new HttpEndpointDescriptorDTO(httpService.getServiceURL()
							, httpService.getServicePort());
			httpResult.setEndpoint(httpEndpointDescriptorDTO);
			httpResult.setActions(getHttpActionDTOs(httpService));
			result = (FullServiceDTO<Y>) httpResult;
		} else if (endpointType == EndpointType.JMS) {
			FullServiceDTO<QueueDTO> jmsResult = new FullServiceDTO<>();
			JMSServiceEndpoint jmsService = persistenceService
					.getJmsService(serviceDTO.getServiceName());
            DeliverySettings deliverySettings = jmsService.getDeliverySettings();
            DeliverySettingsDTO deliverySettingsDTO = new DeliverySettingsDTO(
                    deliverySettings.getRetryNumber(), deliverySettings.getRetryDelay());
            jmsResult.setDeliverySettings(deliverySettingsDTO);
			Map<String, String> props = null;
			try {
				TypeReference<Map<String, String>> typeReference =
						new TypeReference<Map<String, String>>() {
						};
				props = objectMapper
						.readValue(jmsService.getJndiProperties(),
                                   typeReference);
			} catch (IOException e) {
			}
			JMSEndpointDescriptorDTO httpEndpointDescriptorDTO =
					new JMSEndpointDescriptorDTO(
							jmsService.getConnectionFactory(), props);
			jmsResult.setEndpoint(httpEndpointDescriptorDTO);
			jmsResult.setActions(getJmsActionDTOs(jmsService));
			result = (FullServiceDTO<Y>) jmsResult;
		}
		result.setServiceName(serviceDTO.getServiceName());
		return result;
	}

	public List<ActionEndpointDTO<HttpActionDTO>>
	getHttpActionDTOs(HttpServiceEndpoint service) {
		List<ActionEndpointDTO<HttpActionDTO>> result = new ArrayList<>();
		for (AbstractActionEntity action : service.getActions()) {
			HttpAction realAction = (HttpAction) action;
			HttpActionDTO httpActionDTO = new HttpActionDTO(
					realAction.getActionURL(),realAction.getActionMethod());
			result.add(new ActionEndpointDTO<>(
					action.getActionName(), httpActionDTO));
		}
		return result;
	}

	public List<ActionEndpointDTO<QueueDTO>>
	getJmsActionDTOs(JMSServiceEndpoint service) {
		List<ActionEndpointDTO<QueueDTO>> result = new ArrayList<>();
		for (AbstractActionEntity action : service.getActions()) {
			JMSAction realAction = (JMSAction) action;
			QueueDTO actionDTO = new QueueDTO(realAction.getQueueName(),
			                                  realAction.getUsername(),
			                                  realAction.getPassword(),
			                                  realAction.getActionMethod());
			result.add(new ActionEndpointDTO<>(
					action.getActionName(), actionDTO));
		}
		return result;
	}


    public List<DeliveryActionsDTO> getAllActionMap() {
        Map<String, List<AbstractEndpointEntity>> allActionMap =
                persistenceService.getAllActionMap();
        List<DeliveryActionsDTO> list = new ArrayList<>();
        for (Map.Entry<String, List<AbstractEndpointEntity>> entry : allActionMap.entrySet()) {
            List<AbstractEndpointEntity> endpoints = entry.getValue();
            List<ServiceDTO> serviceList = new ArrayList<>();
            for (AbstractEndpointEntity endpoint : endpoints) {
                serviceList.add(new ServiceDTO(endpoint.getServiceName(), endpoint.getType()));
            }
            list.add(new DeliveryActionsDTO(entry.getKey(), serviceList));
        }
        return list;
    }

    public <T extends ActionDescriptor>
    Map<String, ServiceAndActions<T>> get(ActionMethod actionMethod) {
        Map<AbstractEndpointEntity, List<AbstractActionEntity>> servicesSupportingActionType =
                persistenceService.getServicesSupportingActionType(actionMethod);
        Map<String, ServiceAndActions<T>> result = new HashMap<>();
        for (Map.Entry<AbstractEndpointEntity, List<AbstractActionEntity>>
                entry : servicesSupportingActionType.entrySet()) {
            List<AbstractActionEntity> actions = entry.getValue();
            List<ActionEndpointDTO<T>> actionDTOs = new ArrayList<>();
            for (AbstractActionEntity e : actions) {
                actionDTOs.add((ActionEndpointDTO<T>) convertActionToDTO(e));
            }
            result.put(entry.getKey().getServiceName(),
                       new ServiceAndActions<>(entityToDTO(entry.getKey()),actionDTOs));
        }
        return result;
    }

    private ServiceDTO entityToDTO(AbstractEndpointEntity key) {
        return new ServiceDTO(key.getServiceName(), key.getType());
    }

    private ActionEndpointDTO<ActionDescriptor> convertActionToDTO
            (AbstractActionEntity
                     actionEntity) {
        EndpointType type = actionEntity.getType();
        switch (type) {
            case HTTP: {
                HttpAction httpAction = (HttpAction) actionEntity;
                ActionDescriptor httpActionDTO = new HttpActionDTO(httpAction.getActionURL(),
                                                                   actionEntity.getActionMethod());
                return new ActionEndpointDTO<>(actionEntity.getActionName(), httpActionDTO);
            }
            case JMS: {
                JMSAction httpAction = (JMSAction) actionEntity;
                ActionDescriptor httpActionDTO = new QueueDTO(httpAction.getQueueName(),
                                                              httpAction.getUsername(),
                                                              httpAction.getPassword(),
                                                              actionEntity.getActionMethod());
                return new ActionEndpointDTO<>(actionEntity.getActionName(), httpActionDTO);
            }
        }
        return null;
    }
}
