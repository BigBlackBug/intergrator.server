package com.icl.integrator.httpclient;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.editor.EditActionDTO;
import com.icl.integrator.dto.editor.EditServiceDTO;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.httpclient.exceptions.AuthException;
import com.icl.integrator.httpclient.exceptions.IntegratorClientException;
import com.icl.integrator.httpclient.exceptions.MethodNotSupportedException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
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
public class IntegratorHttpClient implements IntegratorClient {

	private final String host;

	private final String mainControllerPath;

	private final int port;

	private final RestTemplate restTemplate;

	private final Class<?> clientInterface;

	private final String managementControllerPath;

	public IntegratorHttpClient(String host, String deployPath, int port,
	                            IntegratorClientSettings clientSettings) {
		this.clientInterface = getClass().getInterfaces()[0];
		this.mainControllerPath = createControllerPath(deployPath);
		this.managementControllerPath = createManagementControllerPath(deployPath);
		this.host = host;
		this.port = port;
		this.restTemplate = new IntegratorRestTemplate(clientSettings);
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
		return createControllerPath(clientInterface.getInterfaces()[0]
				                            .getAnnotation(RequestMapping.class), deployPath);
	}

	private String createManagementControllerPath(String deployPath) {
		return createControllerPath(clientInterface.getInterfaces()[1]
				                            .getAnnotation(RequestMapping.class), deployPath);
	}

	private String createControllerPath(RequestMapping requestMapping, String deployPath) {
		String controllerPath = requestMapping.value()[0];
		StringBuilder sb = new StringBuilder();

		if (!deployPath.isEmpty() && !deployPath.startsWith("/")) {
			sb.append("/");
		}
		sb.append(deployPath).append(controllerPath);
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws com.icl.integrator.httpclient.exceptions.IntegratorClientException
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
	 * @throws IntegratorClientException
	 * @see IntegratorHttpClient#ping(IntegratorPacket)
	 */
	public ResponseDTO<Boolean> ping() throws IntegratorClientException {
		return ping(new IntegratorPacket<Void, DestinationDescriptor>());
	}

	/**
	 * @throws IntegratorClientException
	 * @see IntegratorHttpClient#getServiceList(IntegratorPacket)
	 */
	public ResponseDTO<List<ServiceDTO>> getServiceList() throws IntegratorClientException {
		return getServiceList(new IntegratorPacket<Void, DestinationDescriptor>());
	}

	/**
	 * @throws IntegratorClientException
	 * @see com.icl.integrator.api.IntegratorAPI#getSupportedActions(com.icl.integrator.dto.IntegratorPacket)
	 */
	public <T extends ActionDescriptor>
	ResponseDTO<List<ActionEndpointDTO<T>>>
	getSupportedActions(String serviceDTO) throws IntegratorClientException {
		return getSupportedActions(new IntegratorPacket<>(serviceDTO));
	}

	/**
	 * @throws IntegratorClientException
	 * @see com.icl.integrator.api.IntegratorAPI#getServiceInfo(com.icl.integrator.dto.IntegratorPacket)
	 */
	public <Y extends ActionDescriptor>
	ResponseDTO<FullServiceDTO<Y>>
	getServiceInfo(String serviceName) throws IntegratorClientException {
		return getServiceInfo(new IntegratorPacket<>(serviceName));
	}

	/**
	 * @throws IntegratorClientException
	 * @see IntegratorHttpClient#deliver(IntegratorPacket)
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
	 * @throws IntegratorClientException
	 * @see IntegratorHttpClient#registerService(IntegratorPacket)
	 */
	public <T extends ActionDescriptor>
	ResponseDTO<List<ActionRegistrationResultDTO>> registerService(
			TargetRegistrationDTO<T> registrationDTO) throws IntegratorClientException {
		return registerService(new IntegratorPacket<>(registrationDTO));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws IntegratorClientException
	 */
	@Override
	public <T extends ActionDescriptor, Y extends DestinationDescriptor>
	ResponseDTO<List<ActionRegistrationResultDTO>>
	registerService(IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO)
			throws IntegratorClientException {
		try {
			HttpMethodDescriptor methodPair =
					getMethodPath("registerService", IntegratorPacket.class);
			ParameterizedTypeReference<ResponseDTO<List<ActionRegistrationResultDTO>>>
					type =
					new ParameterizedTypeReference<ResponseDTO<List<ActionRegistrationResultDTO>>>() {
					};
			return sendRequest(registrationDTO, type, methodPair);
		} catch (Exception e) {
			throw new IntegratorClientException(e);
		}
	}

	/**
	 * @throws IntegratorClientException
	 * @see IntegratorHttpClient#isAvailable(IntegratorPacket)
	 */
	public ResponseDTO<Boolean> isAvailable(ServiceDestinationDescriptor serviceDescriptor)
			throws IntegratorClientException {
		IntegratorPacket<ServiceDestinationDescriptor, DestinationDescriptor>
				integratorPacket =
				new IntegratorPacket<>();
		integratorPacket.setData(serviceDescriptor);
		return isAvailable(integratorPacket);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param packet
	 * @throws IntegratorClientException
	 */
	@Override
	public <T extends DestinationDescriptor> ResponseDTO<Boolean> isAvailable(
			IntegratorPacket<ServiceDestinationDescriptor, T> packet)
			throws IntegratorClientException {
		try {
			HttpMethodDescriptor methodPair = getMethodPath("isAvailable", IntegratorPacket.class);
			ParameterizedTypeReference<ResponseDTO<Boolean>>
					type = new ParameterizedTypeReference<ResponseDTO<Boolean>>() {
			};
			return sendRequest(packet, type, methodPair);
		} catch (Exception e) {
			throw new IntegratorClientException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 *
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
	 *
	 * @param serviceName
	 * @throws IntegratorClientException
	 */
	@Override
	public <T extends DestinationDescriptor, Y extends ActionDescriptor>
	ResponseDTO<List<ActionEndpointDTO<Y>>> getSupportedActions(
			IntegratorPacket<String, T> serviceName) throws IntegratorClientException {
		try {
			HttpMethodDescriptor methodPair =
					getMethodPath("getSupportedActions", IntegratorPacket.class);

			ParameterizedTypeReference<ResponseDTO<List<ActionEndpointDTO<Y>>>>
					type =
					new ParameterizedTypeReference<ResponseDTO<List<ActionEndpointDTO<Y>>>>() {
					};
			return sendRequest(serviceName, type, methodPair);
		} catch (Exception e) {
			throw new IntegratorClientException(e);
		}
	}

	/**
	 * @throws IntegratorClientException
	 * @see IntegratorHttpClient#addAction(IntegratorPacket)
	 */
	public <T extends ActionDescriptor> ResponseDTO<Void> addAction(AddActionDTO<T> actionDTO)
			throws IntegratorClientException {
		return addAction(new IntegratorPacket<>(actionDTO));
	}

	/**
	 * {@inheritDoc}
	 *
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
	 *
	 * @param serviceName
	 * @throws IntegratorClientException
	 */
	@Override
	public <ADType extends ActionDescriptor, DDType extends DestinationDescriptor>
	ResponseDTO<FullServiceDTO<ADType>> getServiceInfo(
			IntegratorPacket<String, DDType> serviceName) throws IntegratorClientException {
		try {
			HttpMethodDescriptor methodPair = getMethodPath(
					"getServiceInfo", IntegratorPacket.class);
			ParameterizedTypeReference<ResponseDTO<FullServiceDTO<ADType>>>
					type =
					new ParameterizedTypeReference<ResponseDTO<FullServiceDTO<ADType>>>() {
					};
			return sendRequest(serviceName, type, methodPair);
		} catch (Exception e) {
			throw new IntegratorClientException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 *
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
	 *
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
	 *
	 * @throws IntegratorClientException
	 */
	@Override
	public <T extends DestinationDescriptor, Y extends ActionDescriptor>
	ResponseDTO<List<ServiceAndActions<Y>>> getServicesSupportingActionType(
			IntegratorPacket<ActionMethod, T> packet) throws IntegratorClientException {
		try {
			HttpMethodDescriptor methodPair = getMethodPath(
					"getServicesSupportingActionType", IntegratorPacket.class);
			ParameterizedTypeReference<ResponseDTO<List<ServiceAndActions<Y>>>>
					type =
					new ParameterizedTypeReference<ResponseDTO<List<ServiceAndActions<Y>>>>() {
					};
			return sendRequest(packet, type, methodPair);
		} catch (Exception e) {
			throw new IntegratorClientException(e);
		}
	}

	public <Y extends ActionDescriptor>
	ResponseDTO<List<ServiceAndActions<Y>>> getServicesSupportingActionType(
			ActionMethod actionMethod) {
		return getServicesSupportingActionType(new IntegratorPacket<>(actionMethod));
	}

	@Override
	public <T extends DestinationDescriptor>
	ResponseDTO<List<Modification>>
	fetchUpdates(IntegratorPacket<Void, T> packet) {
		try {
			HttpMethodDescriptor methodPair = getMethodPath("fetchUpdates", IntegratorPacket.class);
			ParameterizedTypeReference<ResponseDTO<List<Modification>>>
					type =
					new ParameterizedTypeReference<ResponseDTO<List<Modification>>>() {
					};
			return sendRequest(packet, type, methodPair);
		} catch (Exception e) {
			throw new IntegratorClientException(e);
		}
	}

	public ResponseDTO<List<Modification>> fetchUpdates() {
		return fetchUpdates(new IntegratorPacket<Void, DestinationDescriptor>());
	}

	@Override
	public <T extends DestinationDescriptor> ResponseDTO<Void> removeService(
			IntegratorPacket<String, T> serviceName) {
		try {
			HttpMethodDescriptor methodPair =
					getMethodPath("removeService", IntegratorPacket.class);
			ParameterizedTypeReference<ResponseDTO<Void>>
					type =
					new ParameterizedTypeReference<ResponseDTO<Void>>() {
					};
			return sendRequest(serviceName, type, methodPair);
		} catch (Exception e) {
			throw new IntegratorClientException(e);
		}
	}

	public ResponseDTO<Void> removeService(String serviceName) {
		return removeService(new IntegratorPacket<>(serviceName));
	}

	@Override
	public <T extends DestinationDescriptor> ResponseDTO<Void> editService(
			 IntegratorPacket<EditServiceDTO, T> editServiceDTO) {
		try {
			HttpMethodDescriptor methodPair = getMethodPath("editService", IntegratorPacket.class);
			ParameterizedTypeReference<ResponseDTO<Void>>
					type =
					new ParameterizedTypeReference<ResponseDTO<Void>>() {
					};
			return sendRequest(editServiceDTO, type, methodPair);
		} catch (Exception e) {
			throw new IntegratorClientException(e);
		}
	}

	public ResponseDTO<Void> editService(EditServiceDTO editServiceDTO) {
		return editService(new IntegratorPacket<>(editServiceDTO));
	}

	@Override
	public <T extends DestinationDescriptor> ResponseDTO<Void> editAction(
			IntegratorPacket<EditActionDTO, T> editActionDTO) {
		try {
			HttpMethodDescriptor methodPair = getMethodPath("editAction", IntegratorPacket.class);
			ParameterizedTypeReference<ResponseDTO<Void>>
					type =
					new ParameterizedTypeReference<ResponseDTO<Void>>() {
					};
			return sendRequest(editActionDTO, type, methodPair);
		} catch (Exception e) {
			throw new IntegratorClientException(e);
		}
	}

	public ResponseDTO<Void> editAction(EditActionDTO editActionDTO) {
		return editAction(new IntegratorPacket<>(editActionDTO));
	}

	/**
	 * @throws IntegratorClientException
	 * @see IntegratorHttpClient#registerAutoDetection(IntegratorPacket)
	 */
	public <Y> ResponseDTO<List<ResponseDTO<Void>>>
	registerAutoDetection(AutoDetectionRegistrationDTO<Y> autoDetectionDTO)
			throws IntegratorClientException {
		return registerAutoDetection(new IntegratorPacket<>(autoDetectionDTO));
	}

	//TODO добавить шифрование
	@Override
	public void login(String username, String password) throws IntegratorClientException {
//		TODO если аутентификация успешна, то прилетает нулл. спасибо спрингсекьюрити за это
		ResponseDTO responseDTO;
		try {
			IntegratorPacket<UserCredentialsDTO, DestinationDescriptor> packet =
					new IntegratorPacket<>(new UserCredentialsDTO(username, password));
			ParameterizedTypeReference<ResponseDTO> type =
					new ParameterizedTypeReference<ResponseDTO>() {
					};
			HttpMethodDescriptor methodDescriptor =
					new HttpMethodDescriptor("/login", RequestMethod.POST);
			responseDTO = sendRequest(mainControllerPath, packet, type, methodDescriptor);
		} catch (Exception e) {
			throw new IntegratorClientException(e);
		}
		if (responseDTO != null) {
			throw new AuthException(responseDTO.getError().getErrorMessage());
		}
	}

	//TODO 2 логаута подряд крешатся
	@Override
	public void logout() throws IntegratorClientException {
		HttpStatus statusCode;
		try {
			URL url = new URL("HTTP", host, port,
			                  mainControllerPath + IntegratorClientConstants.LOGOUT_URL);
			ResponseEntity<Void> response = restTemplate.getForEntity(url.toURI(), Void.class);
			statusCode = response.getStatusCode();
		} catch (Exception ex) {
			throw new IntegratorClientException(ex);
		}
		if (statusCode.series().value() != 2) {
			throw new IntegratorClientException("Не фортануло " + statusCode.getReasonPhrase());
		}
	}

	@Override
	public ResponseDTO<Void> registerUser(
			@RequestBody IntegratorPacket<UserCredentialsDTO, DestinationDescriptor> packet) {
		try {
			HttpMethodDescriptor methodPair =
					getMethodPath("registerUser", IntegratorPacket.class);
			ParameterizedTypeReference<ResponseDTO<Void>>
					type =
					new ParameterizedTypeReference<ResponseDTO<Void>>() {
					};
			return sendRequest(managementControllerPath, packet, type, methodPair);
		} catch (Exception ex) {
			throw new IntegratorClientException(ex);
		}
	}

	private HttpMethodDescriptor getMethodPath(String methodName,
	                                           Class<?>... parameterTypes) {
		Method m = null;
		try {
			m = clientInterface.getMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
		}
		RequestMapping annotation = m.getAnnotation(RequestMapping.class);
		return new HttpMethodDescriptor(annotation.value()[0], annotation.method()[0]);
	}

	private <Response, Request> Response sendRequest(
			String controllerPath,
			Request data, ParameterizedTypeReference<Response> responseType,
			HttpMethodDescriptor methodDescriptor)
			throws RestClientException, MalformedURLException {
		return sendRequest(controllerPath, new HttpEntity<>(data), responseType, methodDescriptor);
	}

	private <Response, Request> Response sendRequest(
			Request data, ParameterizedTypeReference<Response> responseType,
			HttpMethodDescriptor methodDescriptor)
			throws RestClientException, MalformedURLException {
		return sendRequest(new HttpEntity<>(data), responseType, methodDescriptor);
	}

	private <Response, Request> Response sendRequest(
			String controllerPath,
			HttpEntity<Request> requestEntity, ParameterizedTypeReference<Response> responseType,
			HttpMethodDescriptor methodDescriptor)
			throws RestClientException, MalformedURLException {

		RequestMethod methodType = methodDescriptor.getMethodType();
		URL url = new URL("HTTP", host, port, controllerPath + methodDescriptor.getMethodPath());

		String urlString = url.toString();
		if (methodType.equals(RequestMethod.GET)) {
			return restTemplate
					.exchange(urlString, HttpMethod.GET, requestEntity, responseType)
					.getBody();
		} else if (methodType.equals(RequestMethod.POST)) {
			return restTemplate
					.exchange(urlString, HttpMethod.POST, requestEntity, responseType)
					.getBody();
		} else {
			throw new MethodNotSupportedException();
		}
	}

	private <Response, Request> Response sendRequest(
			HttpEntity<Request> requestEntity, ParameterizedTypeReference<Response> responseType,
			HttpMethodDescriptor methodDescriptor)
			throws RestClientException, MalformedURLException {
		return sendRequest(mainControllerPath, requestEntity, responseType, methodDescriptor);
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
