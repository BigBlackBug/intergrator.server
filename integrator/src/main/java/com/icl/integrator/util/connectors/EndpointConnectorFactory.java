package com.icl.integrator.util.connectors;

import com.icl.integrator.dto.DestinationDTO;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.HttpActionDTO;
import com.icl.integrator.dto.registration.QueueDTO;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import com.icl.integrator.model.JMSServiceEndpoint;
import com.icl.integrator.services.EndpointResolverService;
import com.icl.integrator.util.EndpointType;
import com.icl.integrator.util.IntegratorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */
@Component
public class EndpointConnectorFactory {

    @Autowired
    private EndpointResolverService endpointResolverService;

    public EndpointConnector createEndpointConnector(String serviceName,
                                                     EndpointType endpointType,
                                                     String action) {
        return createEndpointConnector(
                new DestinationDTO(serviceName, endpointType),
                action);
    }

    public EndpointConnector createEndpointConnector(
            DestinationDTO destination, String action)
            throws IntegratorException {
        switch (destination.getEndpointType()) {
            case HTTP: {
                URL url = endpointResolverService
                        .getServiceURL(destination.getServiceName(), action);
                return new HTTPEndpointConnector(url);
            }
            case JMS: {
                JMSServiceEndpoint jmsEndpoint = endpointResolverService
                        .getJmsEndpoint(destination.getServiceName());
                return new JMSEndpointConnector(
                        jmsEndpoint, jmsEndpoint.getActionByName(action));
            }
            default: {
                return null;
            }
        }
    }

    public EndpointConnector createEndpointConnector(EndpointDTO endpoint,
                                                     ActionDescriptor descriptor) {
        EndpointType endpointType = endpoint.getEndpointType();
        switch (endpointType) {
            case HTTP: {
                HttpEndpointDescriptorDTO endpointDescriptor =
                        (HttpEndpointDescriptorDTO) endpoint.getDescriptor();
                HttpActionDTO actionDescriptor = (HttpActionDTO) descriptor;
                try {
                    URL url = new URL("HTTP", endpointDescriptor.getHost(),
                                      endpointDescriptor.getPort(),
                                      actionDescriptor.getPath());
                    return new HTTPEndpointConnector(url);
                } catch (MalformedURLException e) {
                    throw new IntegratorException(e);
                }
            }
            case JMS: {
                JMSEndpointDescriptorDTO endpointDescriptor =
                        (JMSEndpointDescriptorDTO) endpoint.getDescriptor();
                QueueDTO actionDescriptor = (QueueDTO) descriptor;
                return new JMSEndpointConnector(endpointDescriptor,
                                                actionDescriptor);
            }
            default: {
                return null;
            }
        }
    }
}
