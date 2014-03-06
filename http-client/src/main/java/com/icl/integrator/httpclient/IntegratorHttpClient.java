package com.icl.integrator.httpclient;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.springapi.IntegratorHttpAPI;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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
import java.util.UUID;

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
    public <T extends DestinationDescriptor> ResponseDTO<Boolean> ping(
            IntegratorPacket<Void, T> responseHandler) {
        HttpMethodDescriptor methodPair = getMethodPath(
                "ping", IntegratorPacket.class);
        ParameterizedTypeReference<ResponseDTO<Boolean>>
                type = new ParameterizedTypeReference<ResponseDTO<Boolean>>() {
        };
        try {
            return sendRequest(responseHandler, type, methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

    public ResponseDTO<Boolean> ping() {
        return ping(new IntegratorPacket<Void, DestinationDescriptor>
                            (new RawDestinationDescriptor()));
    }

    public ResponseDTO<List<ServiceDTO>> getServiceList() {
        return getServiceList(new IntegratorPacket<Void, DestinationDescriptor>
                                      (new RawDestinationDescriptor()));
    }

    public ResponseDTO<List<String>> getSupportedActions(
            ServiceDTO serviceDTO) {
        return getSupportedActions(new IntegratorPacket<>(serviceDTO));
    }

    public <T extends EndpointDescriptor, Y extends ActionDescriptor>
    ResponseDTO<FullServiceDTO<T, Y>>
    getServiceInfo(ServiceDTO serviceDTO) {
        return getServiceInfo(new IntegratorPacket<>(serviceDTO));
    }

    public ResponseDTO<Map<String, ResponseDTO<UUID>>> deliver(
            DeliveryDTO delivery) {
        return deliver(new IntegratorPacket<>(delivery));
    }

    //TODO format
    @Override
    public <T extends DestinationDescriptor> ResponseDTO<Map<String, ResponseDTO<UUID>>> deliver(
            IntegratorPacket<DeliveryDTO, T> delivery) {
        HttpMethodDescriptor methodPair = getMethodPath(
                "deliver", IntegratorPacket.class);
        ParameterizedTypeReference<ResponseDTO<Map<String, ResponseDTO<UUID>>>>
                type =
                new ParameterizedTypeReference<ResponseDTO<Map<String,
                        ResponseDTO<UUID>>>>() {
                };
        try {
            return sendRequest(delivery, type, methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

    public <T extends ActionDescriptor>
    ResponseDTO<RegistrationResultDTO> registerService(
            TargetRegistrationDTO<T> registrationDTO) {
        return registerService(new IntegratorPacket<>(registrationDTO));
    }

    @Override
    public <T extends ActionDescriptor, Y extends DestinationDescriptor> ResponseDTO<RegistrationResultDTO> registerService(
		    IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO) {
        HttpMethodDescriptor methodPair = getMethodPath(
                "registerService", IntegratorPacket.class);
        ParameterizedTypeReference<ResponseDTO<RegistrationResultDTO>>
                type =
                new ParameterizedTypeReference<ResponseDTO<RegistrationResultDTO>>() {
                };
        try {
            return sendRequest(registrationDTO, type, methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

    public ResponseDTO<Boolean> isAvailable(ServiceDestinationDescriptor serviceDescriptor) {
	    IntegratorPacket<ServiceDestinationDescriptor, DestinationDescriptor>
			    integratorPacket =
			    new IntegratorPacket<>();
	    integratorPacket.setPacket(serviceDescriptor);
	    return isAvailable(integratorPacket);
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<Boolean> isAvailable(
            IntegratorPacket<ServiceDestinationDescriptor, T> pingDTO) {
        HttpMethodDescriptor methodPair = getMethodPath(
                "isAvailable", IntegratorPacket.class);
        ParameterizedTypeReference<ResponseDTO<Boolean>>
                type = new ParameterizedTypeReference<ResponseDTO<Boolean>>() {
        };
        try {
            return sendRequest(pingDTO, type, methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<List<ServiceDTO>> getServiceList(
            IntegratorPacket<Void, T> responseHandler) {
        HttpMethodDescriptor methodPair = getMethodPath(
                "getServiceList", IntegratorPacket.class);
        try {
            ParameterizedTypeReference<ResponseDTO<List<ServiceDTO>>>
                    type =
                    new ParameterizedTypeReference<ResponseDTO<List<ServiceDTO>>>() {
                    };
            return sendRequest(responseHandler, type, methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<List<String>> getSupportedActions(
            IntegratorPacket<ServiceDTO, T> serviceDTO) {
        HttpMethodDescriptor methodPair = getMethodPath
                ("getSupportedActions", IntegratorPacket.class);
        try {
            ParameterizedTypeReference<ResponseDTO<List<String>>>
                    type =
                    new ParameterizedTypeReference<ResponseDTO<List<String>>>() {
                    };
            return sendRequest(serviceDTO, type, methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

    public ResponseDTO addAction(AddActionDTO actionDTO) {
        return addAction(new IntegratorPacket<>(actionDTO));
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<Void> addAction(
            IntegratorPacket<AddActionDTO, T> actionDTO) {
        HttpMethodDescriptor methodPair = getMethodPath(
                "addAction", IntegratorPacket.class);
        try {
            ParameterizedTypeReference<ResponseDTO<Void>>
                    type =
                    new ParameterizedTypeReference<ResponseDTO<Void>>() {
                    };
            return sendRequest(actionDTO, type, methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

    @Override
    public <EDType extends EndpointDescriptor, ADType extends ActionDescriptor, DDType extends DestinationDescriptor> ResponseDTO<FullServiceDTO<EDType, ADType>> getServiceInfo(
            IntegratorPacket<ServiceDTO, DDType> serviceDTO) {
        HttpMethodDescriptor methodPair = getMethodPath
                ("getServiceInfo", IntegratorPacket.class);
        try {
            ParameterizedTypeReference<ResponseDTO<FullServiceDTO<EDType, ADType>>>
                    type =
                    new ParameterizedTypeReference<ResponseDTO<FullServiceDTO<EDType, ADType>>>() {
                    };
            return sendRequest(serviceDTO, type, methodPair);
        } catch (MalformedURLException e) {
            throw new IntegratorClientException(e);
        }
    }

	@Override
	public <T extends DestinationDescriptor, Y> ResponseDTO<List<ResponseDTO<Void>>>
	registerAutoDetection(IntegratorPacket<AutoDetectionRegistrationDTO<Y>, T> autoDetectionDTO) {
		HttpMethodDescriptor methodPair = getMethodPath(
				"registerAutoDetection", IntegratorPacket.class);
		try {
			ParameterizedTypeReference<ResponseDTO<List<ResponseDTO<Void>>>>
					type =
					new ParameterizedTypeReference<ResponseDTO<List<ResponseDTO<Void>>>>() {
					};
			return sendRequest(autoDetectionDTO, type, methodPair);
		} catch (MalformedURLException e) {
			throw new IntegratorClientException(e);
		}
	}

	public <Y> ResponseDTO<List<ResponseDTO<Void>>>
	registerAutoDetection(AutoDetectionRegistrationDTO<Y> autoDetectionDTO) {
		return registerAutoDetection(new IntegratorPacket<>(autoDetectionDTO));
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
            Request data, ParameterizedTypeReference<Response> responseType,
            HttpMethodDescriptor methodDescriptor)
            throws RestClientException, MalformedURLException {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(
                new MappingJackson2HttpMessageConverter());
        RequestMethod methodType = methodDescriptor.getMethodType();
        URL url = new URL("HTTP", host, port,
                          path + methodDescriptor.getMethodPath());

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
