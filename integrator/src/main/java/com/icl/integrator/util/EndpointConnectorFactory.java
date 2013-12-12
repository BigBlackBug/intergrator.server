package com.icl.integrator.util;

import com.icl.integrator.dto.EndpointDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
			EndpointDTO destination, String action) {
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
}
