package com.icl.integrator.httpclient;

import com.icl.integrator.deserializer.IntegratorObjectMapper;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.springapi.IntegratorHttpAPI;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
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

    private final RestTemplate restTemplate;

    public IntegratorHttpClient(String host, String deployPath, int port,
                                IntegratorClientSettings clientSettings) {
        this.path = createControllerPath(deployPath);
        this.host = host;
        this.port = port;
        HttpComponentsClientHttpRequestFactory rf = new HttpComponentsClientHttpRequestFactory();
        rf.setReadTimeout(clientSettings.getReadTimeout());
        rf.setConnectTimeout(clientSettings.getConnectionTimeout());
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new IntegratorObjectMapper());
        this.restTemplate = new RestTemplate(rf);
        restTemplate.getMessageConverters().clear();
        restTemplate.getMessageConverters().add(converter);
    }

    public IntegratorHttpClient(String host, int port, IntegratorClientSettings clientSettings) {
        this(host, "", port, clientSettings);
    }

    public IntegratorHttpClient(String host, String deployPath, int port) {
        this(host, deployPath, port, IntegratorClientSettings.createDefaultSettings());
    }

    public IntegratorHttpClient(String host, int port) {
        this(host, port, IntegratorClientSettings.createDefaultSettings());
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

    /**
     * {@inheritDoc}
     * @throws IntegratorClientException
     */
    @Override
    public <T extends DestinationDescriptor> ResponseDTO<Boolean> ping(
            IntegratorPacket<Void, T> responseHandler) throws IntegratorClientException {
        try {
            HttpMethodDescriptor methodPair = getMethodPath("ping", IntegratorPacket.class);
            ParameterizedTypeReference<ResponseDTO<Boolean>>
                    type = new ParameterizedTypeReference<ResponseDTO<Boolean>>() {
            };
            return sendRequest(responseHandler, type, methodPair);
        } catch (Exception e) {
            throw new IntegratorClientException(e);
        }
    }

    /**
     * @see IntegratorHttpClient#ping(IntegratorPacket)
     * @throws IntegratorClientException
     */
    public ResponseDTO<Boolean> ping() throws IntegratorClientException {
        return ping(new IntegratorPacket<Void, DestinationDescriptor>());
    }

    /**
     * @see IntegratorHttpClient#getServiceList(IntegratorPacket)
     * @throws IntegratorClientException
     */
    public ResponseDTO<List<ServiceDTO>> getServiceList() throws IntegratorClientException {
        return getServiceList(new IntegratorPacket<Void, DestinationDescriptor>());
    }

    /**
     * @see IntegratorHttpClient#getSupportedActions(IntegratorPacket)
     * @throws IntegratorClientException
     */
    public <T extends ActionDescriptor>
    ResponseDTO<List<ActionEndpointDTO<T>>>
    getSupportedActions(ServiceDTO serviceDTO) throws IntegratorClientException {
        return getSupportedActions(new IntegratorPacket<>(serviceDTO));
    }

    /**
     * @see IntegratorHttpClient#getServiceInfo(IntegratorPacket)
     * @throws IntegratorClientException
     */
    public <Y extends ActionDescriptor>
    ResponseDTO<FullServiceDTO<Y>>
    getServiceInfo(ServiceDTO serviceDTO) throws IntegratorClientException {
        return getServiceInfo(new IntegratorPacket<>(serviceDTO));
    }

    /**
     * @see IntegratorHttpClient#deliver(IntegratorPacket)
     * @throws IntegratorClientException
     */
    public ResponseDTO<Map<String, ResponseDTO<String>>> deliver(
            DeliveryDTO delivery) throws IntegratorClientException {
        return deliver(new IntegratorPacket<>(delivery));
    }

    @Override
    public <T extends DestinationDescriptor>
    ResponseDTO<Map<String, ResponseDTO<String>>>
    deliver(IntegratorPacket<DeliveryDTO, T> delivery) throws IntegratorClientException {
        try {
            HttpMethodDescriptor methodPair = getMethodPath("deliver", IntegratorPacket.class);
            ParameterizedTypeReference<ResponseDTO<Map<String, ResponseDTO<String>>>>
                    type =
                    new ParameterizedTypeReference<ResponseDTO<Map<String,
                            ResponseDTO<String>>>>() {
                    };

            return sendRequest(delivery, type, methodPair);
        } catch (Exception e) {
            throw new IntegratorClientException(e);
        }
    }

    /**
     * @see IntegratorHttpClient#registerService(IntegratorPacket)
     * @throws IntegratorClientException
     */
    public <T extends ActionDescriptor>
    ResponseDTO<RegistrationResultDTO> registerService(
            TargetRegistrationDTO<T> registrationDTO) throws IntegratorClientException {
        return registerService(new IntegratorPacket<>(registrationDTO));
    }

    /**
     * {@inheritDoc}
     * @throws IntegratorClientException
     */
    @Override
    public <T extends ActionDescriptor, Y extends DestinationDescriptor>
    ResponseDTO<RegistrationResultDTO>
    registerService(IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO)
            throws IntegratorClientException {
        try {
            HttpMethodDescriptor methodPair =
                    getMethodPath("registerService", IntegratorPacket.class);
            ParameterizedTypeReference<ResponseDTO<RegistrationResultDTO>>
                    type =
                    new ParameterizedTypeReference<ResponseDTO<RegistrationResultDTO>>() {
                    };
            return sendRequest(registrationDTO, type, methodPair);
        } catch (Exception e) {
            throw new IntegratorClientException(e);
        }
    }

    /**
     * @see IntegratorHttpClient#isAvailable(IntegratorPacket)
     * @throws IntegratorClientException
     */
    public ResponseDTO<Boolean> isAvailable(ServiceDestinationDescriptor serviceDescriptor)
            throws IntegratorClientException {
        IntegratorPacket<ServiceDestinationDescriptor, DestinationDescriptor>
                integratorPacket =
                new IntegratorPacket<>();
        integratorPacket.setPacket(serviceDescriptor);
        return isAvailable(integratorPacket);
    }

    /**
     * {@inheritDoc}
     * @throws IntegratorClientException
     */
    @Override
    public <T extends DestinationDescriptor> ResponseDTO<Boolean> isAvailable(
            IntegratorPacket<ServiceDestinationDescriptor, T> pingDTO)
            throws IntegratorClientException {
        try {
            HttpMethodDescriptor methodPair = getMethodPath("isAvailable", IntegratorPacket.class);
            ParameterizedTypeReference<ResponseDTO<Boolean>>
                    type = new ParameterizedTypeReference<ResponseDTO<Boolean>>() {
            };
            return sendRequest(pingDTO, type, methodPair);
        } catch (Exception e) {
            throw new IntegratorClientException(e);
        }
    }

    /**
     * {@inheritDoc}
     * @throws IntegratorClientException
     */
    @Override
    public <T extends DestinationDescriptor> ResponseDTO<List<ServiceDTO>> getServiceList(
            IntegratorPacket<Void, T> responseHandler) throws IntegratorClientException {
        try {
            HttpMethodDescriptor methodPair =
                    getMethodPath("getServiceList", IntegratorPacket.class);

            ParameterizedTypeReference<ResponseDTO<List<ServiceDTO>>>
                    type =
                    new ParameterizedTypeReference<ResponseDTO<List<ServiceDTO>>>() {
                    };
            return sendRequest(responseHandler, type, methodPair);
        } catch (Exception e) {
            throw new IntegratorClientException(e);
        }
    }

    /**
     * {@inheritDoc}
     * @throws IntegratorClientException
     */
    @Override
    public <T extends DestinationDescriptor, Y extends ActionDescriptor>
    ResponseDTO<List<ActionEndpointDTO<Y>>> getSupportedActions(
            IntegratorPacket<ServiceDTO, T> serviceDTO) throws IntegratorClientException {
        try {
            HttpMethodDescriptor methodPair =
                    getMethodPath("getSupportedActions", IntegratorPacket.class);

            ParameterizedTypeReference<ResponseDTO<List<ActionEndpointDTO<Y>>>>
                    type =
                    new ParameterizedTypeReference<ResponseDTO<List<ActionEndpointDTO<Y>>>>() {
                    };
            return sendRequest(serviceDTO, type, methodPair);
        } catch (Exception e) {
            throw new IntegratorClientException(e);
        }
    }

    /**
     * @see IntegratorHttpClient#addAction(IntegratorPacket)
     * @throws IntegratorClientException
     */
    public <T extends ActionDescriptor> ResponseDTO<Void> addAction(AddActionDTO<T> actionDTO)
            throws IntegratorClientException {
        return addAction(new IntegratorPacket<>(actionDTO));
    }

    /**
     * {@inheritDoc}
     * @throws IntegratorClientException
     */
	@Override
	public <T extends DestinationDescriptor, Y extends ActionDescriptor> ResponseDTO<Void> addAction(
            IntegratorPacket<AddActionDTO<Y>, T> actionDTO) throws IntegratorClientException {
        try {
            HttpMethodDescriptor methodPair = getMethodPath("addAction", IntegratorPacket.class);
            ParameterizedTypeReference<ResponseDTO<Void>>
                    type =
                    new ParameterizedTypeReference<ResponseDTO<Void>>() {
                    };
            return sendRequest(actionDTO, type, methodPair);
        } catch (Exception e) {
            throw new IntegratorClientException(e);
        }
    }

    /**
     * {@inheritDoc}
     * @throws IntegratorClientException
     */
    @Override
    public <ADType extends ActionDescriptor, DDType extends DestinationDescriptor>
    ResponseDTO<FullServiceDTO<ADType>> getServiceInfo(
            IntegratorPacket<ServiceDTO, DDType> serviceDTO) throws IntegratorClientException {
        try {
            HttpMethodDescriptor methodPair = getMethodPath
                    ("getServiceInfo", IntegratorPacket.class);
            ParameterizedTypeReference<ResponseDTO<FullServiceDTO<ADType>>>
                    type =
                    new ParameterizedTypeReference<ResponseDTO<FullServiceDTO<ADType>>>() {
                    };
            return sendRequest(serviceDTO, type, methodPair);
        } catch (Exception e) {
            throw new IntegratorClientException(e);
        }
    }

    /**
     * {@inheritDoc}
     * @throws IntegratorClientException
     */
	@Override
	public <T extends DestinationDescriptor, Y> ResponseDTO<List<ResponseDTO<Void>>>
    registerAutoDetection(IntegratorPacket<AutoDetectionRegistrationDTO<Y>, T> autoDetectionDTO)
            throws IntegratorClientException {
        try {
            HttpMethodDescriptor methodPair =
                    getMethodPath("registerAutoDetection", IntegratorPacket.class);

            ParameterizedTypeReference<ResponseDTO<List<ResponseDTO<Void>>>>
                    type =
                    new ParameterizedTypeReference<ResponseDTO<List<ResponseDTO<Void>>>>() {
                    };
            return sendRequest(autoDetectionDTO, type, methodPair);
        } catch (Exception e) {
            throw new IntegratorClientException(e);
        }
    }

    /**
     * {@inheritDoc}
     * @throws IntegratorClientException
     */
    @Override
    public <T extends DestinationDescriptor> ResponseDTO<List<DeliveryActionsDTO>>
    getActionsForDelivery(IntegratorPacket<Void, T> packet) throws IntegratorClientException {
        try {
            HttpMethodDescriptor methodPair =
                    getMethodPath("getActionsForDelivery", IntegratorPacket.class);
            ParameterizedTypeReference<ResponseDTO<List<DeliveryActionsDTO>>>
                    type =
                    new ParameterizedTypeReference<ResponseDTO<List<DeliveryActionsDTO>>>() {
                    };
            return sendRequest(packet, type, methodPair);
        } catch (Exception e) {
            throw new IntegratorClientException(e);
        }
    }

    /**
     * {@inheritDoc}
     * @throws IntegratorClientException
     */
    @Override
    public <T extends DestinationDescriptor, Y extends ActionDescriptor>
    ResponseDTO<Map<String, ServiceAndActions<Y>>> getServicesSupportingActionType(
            IntegratorPacket<ActionMethod, T> packet) throws IntegratorClientException {
        try {
            HttpMethodDescriptor methodPair = getMethodPath(
                    "getServicesSupportingActionType", IntegratorPacket.class);
            ParameterizedTypeReference<ResponseDTO<Map<String, ServiceAndActions<Y>>>>
                    type =
                    new ParameterizedTypeReference<ResponseDTO<Map<String, ServiceAndActions<Y>>>>() {
                    };
            return sendRequest(packet, type, methodPair);
        } catch (Exception e) {
            throw new IntegratorClientException(e);
        }
    }

    /**
     * @see IntegratorHttpClient#registerAutoDetection(IntegratorPacket)
     * @throws IntegratorClientException
     */
    public <Y> ResponseDTO<List<ResponseDTO<Void>>>
    registerAutoDetection(AutoDetectionRegistrationDTO<Y> autoDetectionDTO)
            throws IntegratorClientException {
        return registerAutoDetection(new IntegratorPacket<>(autoDetectionDTO));
	}

	private HttpMethodDescriptor getMethodPath(String methodName,
                                               Class<?>... parameterTypes) {
        Method m = null;
        try {
            m = getClass().getInterfaces()[0].getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
        }
        RequestMapping annotation = m.getAnnotation(RequestMapping.class);
        return new HttpMethodDescriptor(annotation.value()[0],
                                        annotation.method()[0]);
    }

    private <Response, Request> Response sendRequest(
            Request data, ParameterizedTypeReference<Response> responseType,
            HttpMethodDescriptor methodDescriptor)
            throws RestClientException, MalformedURLException {

        RequestMethod methodType = methodDescriptor.getMethodType();
        URL url = new URL("HTTP", host, port, path + methodDescriptor.getMethodPath());

        String urlString = url.toString();
        HttpEntity<Request> requestEntity = new HttpEntity<>(data);
        if (methodType.equals(RequestMethod.GET)) {
            return restTemplate.
                    exchange(urlString, HttpMethod.GET,
                             requestEntity, responseType).getBody();
        } else if (methodType.equals(RequestMethod.POST)) {
            return restTemplate.
                    exchange(urlString, HttpMethod.POST,
                             requestEntity, responseType).getBody();
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
