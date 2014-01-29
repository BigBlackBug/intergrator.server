package com.icl.integrator.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import com.icl.integrator.model.HttpAction;
import com.icl.integrator.model.HttpServiceEndpoint;
import com.icl.integrator.model.JMSAction;
import com.icl.integrator.model.JMSServiceEndpoint;
import com.icl.integrator.util.EndpointType;
import com.icl.integrator.util.IntegratorException;
import com.icl.integrator.util.connectors.EndpointConnector;
import com.icl.integrator.util.connectors.EndpointConnectorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private ObjectMapper objectMapper;

    @Transactional
    public <T extends ActionDescriptor> void addAction(
            AddActionDTO<T> actionDTO) throws IntegratorException {
        ActionRegistrationDTO<T> actionReg =
                actionDTO.getActionRegistrationDTO();
        ServiceDTO service = actionDTO.getService();
        EndpointType endpointType = service.getEndpointType();

        ActionEndpointDTO<T> action = actionReg.getAction();
        String actionName = action.getActionName();

        if (endpointType == EndpointType.HTTP) {
            HttpServiceEndpoint serviceEndpoint =
                    persistenceService.getHttpService(service.getServiceName());
            HttpActionDTO newActionDTO = (HttpActionDTO)
                    action.getActionDescriptor();
            HttpAction newAction = new HttpAction();
            newAction.setActionName(actionName);
            newAction.setActionURL(newActionDTO.getPath());
            newAction.setHttpServiceEndpoint(serviceEndpoint);
            serviceEndpoint.addAction(newAction);
        } else if (endpointType == EndpointType.JMS) {
            JMSServiceEndpoint serviceEndpoint =
                    persistenceService.getJmsService(service.getServiceName());
            QueueDTO queueDTO = (QueueDTO) action.getActionDescriptor();
            JMSAction newAction = new JMSAction();
            newAction.setActionName(actionName);
            newAction.setPassword(queueDTO.getPassword());
            newAction.setQueueName(queueDTO.getQueueName());
            newAction.setUsername(queueDTO.getUsername());
            newAction.setJmsServiceEndpoint(serviceEndpoint);
            serviceEndpoint.addAction(newAction);
        }
    }

    public List<String> getSupportedActions(ServiceDTO serviceDTO) {
        EndpointType endpointType = serviceDTO.getEndpointType();
        switch (endpointType) {
            case HTTP: {
                return persistenceService.getHttpActions(
                        serviceDTO.getServiceName());
            }
            case JMS: {
                return persistenceService.getJmsActions(
                        serviceDTO.getServiceName());
            }
            default: {
                return null;
            }
        }
    }

    public Boolean pingService(PingDTO pingDTO) {
        EndpointConnector connector = connectorFactory
                .createEndpointConnector(
                        new DestinationDTO(pingDTO.getServiceName(),
                                           pingDTO.getEndpointType()),
                        pingDTO.getAction());
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

    public <T extends EndpointDescriptor,
            Y extends ActionDescriptor> FullServiceDTO<T, Y>
    getServiceInfo(ServiceDTO serviceDTO) {
        FullServiceDTO<T, Y> result = null;
        EndpointType endpointType = serviceDTO.getEndpointType();
        if (endpointType == EndpointType.HTTP) {
            FullServiceDTO<HttpEndpointDescriptorDTO,
                    HttpActionDTO> httpResult = new FullServiceDTO<>();
            HttpServiceEndpoint httpService = persistenceService
                    .getHttpService(serviceDTO.getServiceName());
            HttpEndpointDescriptorDTO httpEndpointDescriptorDTO =
                    new HttpEndpointDescriptorDTO(httpService.getServiceURL()
                            , httpService.getServicePort());
            EndpointDTO<HttpEndpointDescriptorDTO> endpointDTO =
                    new EndpointDTO<>(endpointType, httpEndpointDescriptorDTO);
            httpResult.setServiceEndpoint(endpointDTO);
            httpResult.setActions(getHttpActionDTOs(httpService));
            result = (FullServiceDTO<T, Y>) httpResult;
        } else if (endpointType == EndpointType.JMS) {
            FullServiceDTO<JMSEndpointDescriptorDTO,
                    QueueDTO> jmsResult = new FullServiceDTO<>();
            JMSServiceEndpoint jmsService = persistenceService
                    .getJmsService(serviceDTO.getServiceName());
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
            EndpointDTO<JMSEndpointDescriptorDTO> endpointDTO =
                    new EndpointDTO<>(endpointType, httpEndpointDescriptorDTO);
            jmsResult.setServiceEndpoint(endpointDTO);
            jmsResult.setActions(getJmsActionDTOs(jmsService));
            result = (FullServiceDTO<T, Y>) jmsResult;
        }
        result.setServiceName(serviceDTO.getServiceName());
        return result;
    }

    public List<ActionEndpointDTO<HttpActionDTO>>
    getHttpActionDTOs(HttpServiceEndpoint service) {
        List<ActionEndpointDTO<HttpActionDTO>> result = new ArrayList<>();
        for (HttpAction action : service.getHttpActions()) {
            HttpActionDTO httpActionDTO = new HttpActionDTO(
                    action.getActionURL());
            result.add(new ActionEndpointDTO<>(
                    action.getActionName(), httpActionDTO));
        }
        return result;
    }

    public List<ActionEndpointDTO<QueueDTO>>
    getJmsActionDTOs(JMSServiceEndpoint service) {
        List<ActionEndpointDTO<QueueDTO>> result = new ArrayList<>();
        for (JMSAction action : service.getJmsActions()) {
            QueueDTO actionDTO = new QueueDTO(action.getQueueName(),
                                              action.getUsername(),
                                              action.getPassword());
            result.add(new ActionEndpointDTO<>(
                    action.getActionName(), actionDTO));
        }
        return result;
    }
}
