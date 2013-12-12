package com.icl.integrator.util.connectors;

import com.icl.integrator.dto.DestinationDTO;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.services.AddressMappingService;
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
    private AddressMappingService addressService;

    public EndpointConnector createEndpointConnector(
            DestinationDTO destination, String action) {
        switch (destination.getEndpointType()) {
            case HTTP: {
                URL url = addressService
                        .getServiceURL(destination.getServiceName(),
                                       action);
                return new HTTPEndpointConnector(url);
            }
            default: {
                return null;
            }
        }
    }

    public EndpointConnector createEndpointConnector(EndpointDTO endpoint)
            throws IntegratorException {
        EndpointType endpointType = endpoint.getEndpointType();
        switch (endpointType) {
            case HTTP: {
                HttpEndpointDescriptorDTO descriptor =
                        (HttpEndpointDescriptorDTO)
                                endpoint.getDescriptor();
                try {
                    URL url = new URL("HTTP", descriptor.getHost(),
                                      descriptor.getPort(),
                                      descriptor.getPath());
                    return new HTTPEndpointConnector(url);
                } catch (MalformedURLException e) {
                    throw new IntegratorException(e);
                }
            }
            default: {
                return null;
            }
        }
    }
}
