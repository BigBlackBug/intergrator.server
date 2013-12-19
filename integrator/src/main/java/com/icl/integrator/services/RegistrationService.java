package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import com.icl.integrator.model.HttpAction;
import com.icl.integrator.model.HttpServiceEndpoint;
import com.icl.integrator.model.JMSAction;
import com.icl.integrator.model.JMSServiceEndpoint;
import com.icl.integrator.util.connectors.ConnectionException;
import com.icl.integrator.util.connectors.EndpointConnector;
import com.icl.integrator.util.connectors.EndpointConnectorFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ObjectMapper serializer;

    @Autowired
    private EndpointConnectorFactory connectorFactory;

    @Autowired
    private PersistenceService persistenceService;

    public <T extends ActionDescriptor>
    Map<String, ResponseFromTargetDTO<Void>> register
            (TargetRegistrationDTO<T> registrationDTO)
            throws TargetRegistrationException {
        Map<String, ResponseFromTargetDTO<Void>> result = new HashMap<>();

        EndpointDTO endpoint = registrationDTO.getEndpoint();
        List<ActionEndpointDTO<T>> actions = new ArrayList<>();
        for (ActionEndpointDTO<T> actionEndpoint :
                registrationDTO.getActions()) {
            try {
                if (!actionEndpoint.isForceRegister()) {
                    testConnection(endpoint, actionEndpoint);
                }
                actions.add(actionEndpoint);
            } catch (ConnectionException ex) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setErrorMessage(ex.getMessage());
                errorDTO.setDeveloperMessage(ex.getCause().getMessage());
                ResponseFromTargetDTO<Void> dto =
                        new ResponseFromTargetDTO<>(errorDTO);
                result.put(actionEndpoint.getActionName(), dto);
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
                    Map<String, ResponseFromTargetDTO<Void>> result)
            throws TargetRegistrationException {
        String serviceName = registrationDTO.getServiceName();
        JMSEndpointDescriptorDTO descriptor =
                (JMSEndpointDescriptorDTO) endpoint.getDescriptor();
        List<JMSAction> httpActions = getJmsActions(actions);
        JMSServiceEndpoint serviceEntity =
                createJmsEntity(descriptor, serviceName);
        try {
            serviceEntity = persistenceService.saveService(serviceEntity);
        } catch (PersistenceException ex) {
            logger.error("GG", ex);
            throw new TargetRegistrationException("Ошибка регистрации", ex);
        }
        for (JMSAction action : httpActions) {
            ResponseFromTargetDTO<Void> responseDTO;
            try {
                action.setJmsServiceEndpoint(serviceEntity);
                serviceEntity.addAction(action);
                persistenceService.saveAction(action);
                responseDTO = new ResponseFromTargetDTO<>(true);
            } catch (PersistenceException ex) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setErrorMessage("Ошибка регистрации");
                errorDTO.setDeveloperMessage(ex.getMessage());
                responseDTO = new ResponseFromTargetDTO<>(errorDTO);
            }
            result.put(action.getActionName(), responseDTO);
        }
    }

    @Transactional
    private <T extends ActionDescriptor>
    void processHttp(EndpointDTO endpoint,
                     TargetRegistrationDTO registrationDTO,
                     List<ActionEndpointDTO<T>> actions,
                     Map<String, ResponseFromTargetDTO<Void>> result)
            throws TargetRegistrationException {
        String serviceName = registrationDTO.getServiceName();
        HttpEndpointDescriptorDTO descriptor =
                (HttpEndpointDescriptorDTO) endpoint.getDescriptor();
        List<HttpAction> httpActions = getHttpActions(actions);
        HttpServiceEndpoint serviceEntity =
                createHttpEntity(descriptor, serviceName);
        try {
            serviceEntity = persistenceService.saveService(serviceEntity);
        } catch (PersistenceException ex) {
            logger.error("GG", ex);
            throw new TargetRegistrationException("Ошибка регистрации", ex);
        }
        for (HttpAction action : httpActions) {
            ResponseFromTargetDTO<Void> responseDTO;
            try {
                action.setHttpServiceEndpoint(serviceEntity);
                serviceEntity.addAction(action);
                persistenceService.saveAction(action);
                responseDTO = new ResponseFromTargetDTO<>(true);
            } catch (PersistenceException ex) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setErrorMessage("Ошибка регистрации");
                errorDTO.setDeveloperMessage(ex.getMessage());
                responseDTO = new ResponseFromTargetDTO<>(errorDTO);
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
        List<JMSAction> httpActions = new ArrayList<>();
        for (ActionEndpointDTO<T> actionDescriptor : actions) {
            QueueDTO queueDTO =
                    (QueueDTO) actionDescriptor.getActionDescriptor();
            String actionName = actionDescriptor.getActionName();
            JMSAction actionMapping = new JMSAction();
            actionMapping.setUsername(queueDTO.getUsername());
            actionMapping.setActionName(actionName);
            actionMapping.setPassword(queueDTO.getPassword());
            actionMapping.setQueueName(queueDTO.getQueueName());
            httpActions.add(actionMapping);
        }
        return httpActions;
    }

    private <T extends ActionDescriptor> HttpServiceEndpoint createHttpEntity(
            HttpEndpointDescriptorDTO descriptor,
            String serviceName) {
        HttpServiceEndpoint endpoint = new HttpServiceEndpoint();
        endpoint.setServiceName(serviceName);
        endpoint.setServiceURL(descriptor.getHost());
        endpoint.setServicePort(descriptor.getPort());
//        endpoint.setHttpActions(actions);
//        for (HttpAction action : actions) {
//            action.setHttpServiceEndpoint(endpoint);
//        }
        return endpoint;
    }

    private JMSServiceEndpoint createJmsEntity(
            JMSEndpointDescriptorDTO descriptor,
            String serviceName) {
        JMSServiceEndpoint endpoint = new JMSServiceEndpoint();
        endpoint.setConnectionFactory(descriptor.getConnectionFactory());
        String jndiProperties = null;
        try {
            jndiProperties = serializer.
                    writeValueAsString(descriptor.getJndiProperties());
        } catch (JsonProcessingException e) {
            //never happens
        }
        endpoint.setJndiProperties(jndiProperties);
        endpoint.setServiceName(serviceName);
//        endpoint.setJmsActions(actions);
//        for (JMSAction action : actions) {
//            action.setJmsServiceEndpoint(endpoint);
//        }
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

}
