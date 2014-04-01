package com.icl.integrator.util.connectors;

import com.icl.integrator.dto.registration.ActionMethod;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */

public class HTTPEndpointConnector implements EndpointConnector {

	public static final String CONTENT_TYPE = "Content-Type";

	private final URL url;

	private final ActionMethod actionMethod;

	HTTPEndpointConnector(URL url,ActionMethod actionMethod) {
		this.url = url;
		this.actionMethod = actionMethod;
	}

	@Override
	public void testConnection() throws ConnectionException {
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			HttpEntity<String> entity = new HttpEntity<>(headers);

			restTemplate.exchange(url.toURI().toString()+"?action_method={action_method}", HttpMethod.HEAD,
			                      entity, Void.class, actionMethod);
		} catch (URISyntaxException e) {
			throw new ConnectionException("URL не валиден", e);
		} catch (HttpClientErrorException ex) {
			String message = MessageFormat.format(
					"Пингуемый сервис вернул код {0}. Сообщение об ошибке: {1} ",
					ex.getStatusCode(),
					ex.getStatusText());
			throw new ConnectionException(message, ex);
		} catch (ResourceAccessException ex) {
			throw new ConnectionException("Ошибка I/O", ex);
		} catch (Exception ex) {
			throw new ConnectionException("Неожиданная ошибка", ex);
		}
	}

	@Override
	public <Request, Response> Response sendRequest(
			Request data, Class<Response> responseClass) throws
			EndpointConnectorExceptions.HttpConnectorException {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<Request> entity = new HttpEntity<>(data, headers);
		try {
			ResponseEntity<Response> response = restTemplate
					.postForEntity(url.toURI(), entity, responseClass);
			return response.getBody();
		} catch (Exception ex) {
			throw new EndpointConnectorExceptions.HttpConnectorException(ex);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Тип соединения: HTTP \n").
				append("URL: ").append(url.toString());
		return sb.toString();
	}

}
