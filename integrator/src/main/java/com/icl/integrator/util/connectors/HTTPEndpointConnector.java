package com.icl.integrator.util.connectors;

import com.icl.integrator.dto.RequestDataDTO;
import com.icl.integrator.dto.ResponseDTO;
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

    private final URL url;

    HTTPEndpointConnector(URL url) {
        this.url = url;
    }

    @Override
    public void testConnection() throws ConnectionException {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForObject(url.toURI(), new RequestDataDTO(),
                                       ResponseDTO.class);
            //игнорируем 500 ошибку, так как посылаем заведомо говнозапрос
        } catch (URISyntaxException e) {
            throw new ConnectionException("URL не валиден", e);
        } catch (HttpClientErrorException ex) {
            String message = MessageFormat.format(
                    "Сервер вернул код {0}. Сообщение об ошибке:\'{1}\'",
                    ex.getStatusCode(),
                    ex.getStatusText());
            throw new ConnectionException(message, ex);
        } catch (ResourceAccessException ex) {
            throw new ConnectionException("Ошибка I/O", ex);
        }
    }

    @Override
    public <Request, Response> Response sendRequest(
            Request data, Class<Response> responseClass) throws
            EndpointConnectorExceptions.HttpIntegratorException {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.postForObject(url.toURI(), data,
                                              responseClass);
        } catch (Exception ex) {
            throw new EndpointConnectorExceptions.HttpIntegratorException(ex);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип соединения: HTTP\n").
                append("URL: ").append(url.toString());
        return sb.toString();
    }

}
