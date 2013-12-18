package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.EndpointDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 16.12.13
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
@Service
public class RegistrationService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ObjectMapper serializer;

    @Autowired
    private EndpointConnectorFactory connectorFactory;

    @Transactional
    public <T extends ActionDescriptor> void register(
            TargetRegistrationDTO<T> registrationDTO) throws
            TargetRegistrationException {
        EndpointDTO endpoint = registrationDTO.getEndpoint();
        try {
            for (ActionEndpointDTO<T> actionEndpoint : registrationDTO
                    .getActions()) {
                if (!actionEndpoint.isForceRegister()) {
                    testConnection(endpoint, actionEndpoint);
                }
                //TODO refactor to register the ones that are available
            }
        } catch (ConnectionException ex) {
            throw new TargetRegistrationException(
                    "Connection failed", ex);
        }

        String serviceName = registrationDTO.getServiceName();
        switch (endpoint.getEndpointType()) {
            case HTTP: {
                HttpEndpointDescriptorDTO descriptor =
                        (HttpEndpointDescriptorDTO) endpoint.getDescriptor();
                List<ActionEndpointDTO<T>> actions =
                        registrationDTO.getActions();
                List<HttpAction> httpActions = getHttpActions(actions);
                HttpServiceEndpoint serviceEntity = createHttpEntity
                        (descriptor, serviceName, httpActions);
                em.persist(serviceEntity);
                break;
            }
            case JMS: {
                JMSEndpointDescriptorDTO descriptor =
                        (JMSEndpointDescriptorDTO) endpoint.getDescriptor();
                List<ActionEndpointDTO<T>> actions =
                        registrationDTO.getActions();
                List<JMSAction> httpActions = getJmsActions(actions);
                JMSServiceEndpoint serviceEntity = createJmsEntity
                        (descriptor, serviceName, httpActions);
                em.persist(serviceEntity);
                break;
            }

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
            String serviceName,
            List<HttpAction> actions) {
        HttpServiceEndpoint endpoint = new HttpServiceEndpoint();
        endpoint.setServiceName(serviceName);
        endpoint.setServiceURL(descriptor.getHost());
        endpoint.setServicePort(descriptor.getPort());
        endpoint.setHttpActions(actions);
        for (HttpAction action : actions) {
            action.setHttpServiceEndpoint(endpoint);
        }
        return endpoint;
    }

    private JMSServiceEndpoint createJmsEntity(
            JMSEndpointDescriptorDTO descriptor,
            String serviceName, List<JMSAction> actions) {
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
        endpoint.setJmsActions(actions);
        for (JMSAction action : actions) {
            action.setJmsServiceEndpoint(endpoint);
        }
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
