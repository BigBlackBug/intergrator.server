package com.icl.integrator.util;

import org.springframework.web.client.RestTemplate;

import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */

public class HTTPEndpointConnector implements EndpointConnector {

    private final URL url;

    public HTTPEndpointConnector(URL url) {
        this.url = url;
    }

    @Override
    public <Request, Response> Response sendRequest(Request data,
                                                    Class<Response> responseClass)
            throws Exception {
        RestTemplate restTemplate = new RestTemplate();
//        RequestToTargetDTO dto = new RequestToTargetDTO();
//        dto.setAdditionalData(packet.getAdditionalData());
//        dto.setData(packet.getData());
        return restTemplate.postForObject(url.toURI(), data,
                                          responseClass);
    }
}
