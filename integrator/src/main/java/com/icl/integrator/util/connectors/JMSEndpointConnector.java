package com.icl.integrator.util.connectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.deserializer.IntegratorObjectMapper;
import com.icl.integrator.dto.registration.QueueDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import com.icl.integrator.model.JMSAction;
import com.icl.integrator.model.JMSServiceEndpoint;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 17.12.13
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
public class JMSEndpointConnector implements EndpointConnector {

    private final JMSEndpointDescriptorDTO connectionData;

    private final QueueDTO queueDescriptor;

    private ObjectMapper serializer = new IntegratorObjectMapper();

    JMSEndpointConnector(JMSEndpointDescriptorDTO connectionData,
                         QueueDTO queueDescriptor) {
        this.connectionData = connectionData;
        this.queueDescriptor = queueDescriptor;
    }

    JMSEndpointConnector(JMSServiceEndpoint endpoint, JMSAction action) {
        String jndiProperties = endpoint.getJndiProperties();

        Map<String, String> properties = null;
        try {
            TypeReference<Map<String, String>> typeRef =
                    new TypeReference<Map<String, String>>() {
                    };
            properties = serializer.readValue(jndiProperties, typeRef);
        } catch (IOException e) {
        }
        this.connectionData =
                new JMSEndpointDescriptorDTO(endpoint.getConnectionFactory(), properties);
        this.queueDescriptor = new QueueDTO(action.getQueueName(), action.getUsername(),
                                            action.getPassword(),action.getActionMethod());
    }

    @Override
    public void testConnection() throws ConnectionException {
        Context ctx;
        try {
            ctx = new InitialContext(
                    new Hashtable<>(connectionData.getJndiProperties()));
        } catch (NamingException e) {
            throw new ConnectionException("Неверно указаны параметры jndi", e);
        }
        ConnectionFactory factory;
        try {
            factory = (ConnectionFactory) ctx
                    .lookup(connectionData.getConnectionFactory());
        } catch (NamingException e) {
            throw new ConnectionException(
                    "Такой ConnectionFactory не существует", e);
        }
        try {
            Connection connection = factory.createConnection(
                    queueDescriptor.getUsername(),
                    queueDescriptor.getPassword());
            Session session = connection.createSession(false,
                                                       Session.AUTO_ACKNOWLEDGE);
            session.createQueue(queueDescriptor.getQueueName());
        } catch (JMSException ex) {
            throw new ConnectionException("Невозможно установить соединение " +
                                                  "с очередью", ex);
        }
    }

    @Override
    public <Request, Response> Response sendRequest(Request data,
                                                    Class<Response> responseClass)
            throws EndpointConnectorExceptions.JMSConnectorException {
        try {
            Context ctx = new InitialContext(
                    new Hashtable<>(connectionData.getJndiProperties()));
            ConnectionFactory factory = (ConnectionFactory) ctx
                    .lookup(connectionData.getConnectionFactory());
            Connection connection = factory.createConnection(
                    queueDescriptor.getUsername(),
                    queueDescriptor.getPassword());
            Session session = connection.createSession(false,
                                                       Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueDescriptor.getQueueName());
            MessageProducer producer = session.createProducer(queue);
            String dataJson = serializer.writeValueAsString(data);
            TextMessage message = session.createTextMessage(dataJson);
            producer.send(message);
            session.close();
            connection.close();
        } catch (Exception ex) {
            throw new EndpointConnectorExceptions.JMSConnectorException(ex);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип соединения: JMS")
                .append("\nПараметры соединения:")
                .append("\n\tConnectionFactory: ")
                .append(connectionData.getConnectionFactory())
                .append("\n\tJNDI Connection Properties: '")
                .append(connectionData.getJndiProperties().toString())
                .append("\n\tQueueName: ")
                .append(queueDescriptor.getQueueName());
        return sb.toString();
    }
}
