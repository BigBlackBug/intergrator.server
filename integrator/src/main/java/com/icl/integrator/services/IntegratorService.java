package com.icl.integrator.services;

import com.icl.integrator.dto.ServiceDTO;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.model.HttpAction;
import com.icl.integrator.model.HttpServiceEndpoint;
import com.icl.integrator.model.JMSAction;
import com.icl.integrator.model.JMSServiceEndpoint;
import com.icl.integrator.util.EndpointType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 21.01.14
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
@Service
public class IntegratorService {

    @Autowired
    private PersistenceService persistenceService;

    @Transactional
    public <T extends ActionDescriptor> void addAction(
            AddActionDTO<T> actionDTO) {
        ActionEndpointDTO<T> action = actionDTO.getAction();
        ServiceDTO service = actionDTO.getService();
        EndpointType endpointType = service.getEndpointType();

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

}
