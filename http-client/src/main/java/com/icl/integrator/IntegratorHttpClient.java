package com.icl.integrator;

import com.icl.integrator.api.IntegratorHttpAPI;
import com.icl.integrator.dto.PingDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.SourceDataDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 20.01.14
 * Time: 10:48
 * To change this template use File | Settings | File Templates.
 */
public class IntegratorHttpClient implements IntegratorHttpAPI {

    private final String host;

    private final String path;

    private final int port;

    public IntegratorHttpClient(String host, int port) {
        this.path = getClass().getInterfaces()[0]
                .getAnnotation(RequestMapping.class).value()[0];
        this.host = host;
        this.port = port;
    }

    @Override
    public void process(SourceDataDTO packet) {
        HttpMethodDescriptor methodPair = getMethodPath(
                "process", SourceDataDTO.class);
        try {
            sendRequest(packet, Void.class, methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

    @Override
    public Map<String, String> ping() {
        HttpMethodDescriptor methodPair = getMethodPath("ping");
        try {
            return (Map<String, String>) sendRequest(
                    null, Map.class, methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

    @Override
    public ResponseFromTargetDTO<Map> registerTarget(
            TargetRegistrationDTO registrationDTO) {
        HttpMethodDescriptor methodPair = getMethodPath(
                "registerTarget", TargetRegistrationDTO.class);
        try {
            return (ResponseFromTargetDTO<Map>)
                    sendRequest(registrationDTO, ResponseFromTargetDTO.class,
                                methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

    @Override
    public ResponseFromTargetDTO<Boolean> isAvailable(PingDTO pingDTO) {
        HttpMethodDescriptor methodPair = getMethodPath(
                "registerTarget", TargetRegistrationDTO.class);
        try {
            return (ResponseFromTargetDTO<Boolean>)
                    sendRequest(pingDTO, ResponseFromTargetDTO.class,
                                methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

    private HttpMethodDescriptor getMethodPath(String methodName,
                                               Class<?>... parameterTypes) {
        Method m = null;
        try {
            m = getClass().getInterfaces()[0].getMethod(
                    methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
        }
        RequestMapping annotation = m.getAnnotation(RequestMapping.class);
        return new HttpMethodDescriptor(annotation.value()[0],
                                        annotation.method()[0]);
    }

    private <Response, Request> Response sendRequest(
            Request data, Class<Response> responseClass,
            HttpMethodDescriptor methodDescriptor)
            throws RestClientException, MalformedURLException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(
                new MappingJackson2HttpMessageConverter());
        RequestMethod methodType = methodDescriptor.getMethodType();
        URL url = new URL("HTTP", host, port,
                          path + methodDescriptor.getMethodPath());
        String urlString = url.toString();
        if (methodType.equals(RequestMethod.GET)) {
            return restTemplate.getForObject(urlString, responseClass,
                                             data);
        } else if (methodType.equals(RequestMethod.POST)) {
            return restTemplate.postForObject(urlString, data,
                                              responseClass);
        } else {
            throw new NotImplementedException();
        }
    }

    private static class HttpMethodDescriptor {

        private final String methodPath;

        private final RequestMethod methodType;

        private HttpMethodDescriptor(String methodPath,
                                     RequestMethod methodType) {

            this.methodPath = methodPath;
            this.methodType = methodType;
        }

        private String getMethodPath() {
            return methodPath;
        }

        private RequestMethod getMethodType() {
            return methodType;
        }
    }
}
