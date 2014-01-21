package com.icl.integrator.httpclient;

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

    public IntegratorHttpClient(String host, String deployPath, int port) {
        this.path = createControllerPath(deployPath);
        this.host = host;
        this.port = port;
    }

    public IntegratorHttpClient(String host, int port) {
        this(host, "", port);
    }

    private String createControllerPath(String deployPath) {
        String controllerPath = getClass().getInterfaces()[0]
                .getAnnotation(RequestMapping.class).value()[0];
        StringBuilder sb = new StringBuilder();

        if (!deployPath.isEmpty() && !deployPath.startsWith("/")) {
            sb.append("/");
        }
        sb.append(deployPath).append(controllerPath);
        return sb.toString();
    }

    @Override
    public void deliver(SourceDataDTO packet) {
        HttpMethodDescriptor methodPair = getMethodPath(
                "deliver", SourceDataDTO.class);
        try {
            sendRequest(packet, Void.class, methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

    @Override
    public Boolean ping() {
        HttpMethodDescriptor methodPair = getMethodPath("ping");
        try {
            return sendRequest(null, Boolean.class, methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseFromTargetDTO<Map> registerService(
            TargetRegistrationDTO registrationDTO) {
        HttpMethodDescriptor methodPair = getMethodPath(
                "registerService", TargetRegistrationDTO.class);
        try {
            return (ResponseFromTargetDTO<Map>)
                    sendRequest(registrationDTO, ResponseFromTargetDTO.class,
                                methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseFromTargetDTO<Boolean> isAvailable(PingDTO pingDTO) {
        HttpMethodDescriptor methodPair = getMethodPath(
                "isAvailable", PingDTO.class);
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
            throw new MethodNotSupportedException();
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
