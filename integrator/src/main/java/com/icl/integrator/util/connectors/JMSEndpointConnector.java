package com.icl.integrator.util.connectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.registration.QueueDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import com.icl.integrator.model.JMSAction;
import com.icl.integrator.model.JMSServiceEndpoint;
import com.icl.integrator.util.MyObjectMapper;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
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

    private ObjectMapper serializer = new MyObjectMapper();

    JMSEndpointConnector(JMSEndpointDescriptorDTO connectionData,
                         QueueDTO queueDescriptor) {
        this.connectionData = connectionData;
        this.queueDescriptor = queueDescriptor;
    }

    JMSEndpointConnector(JMSServiceEndpoint endpoint, JMSAction action) {
        connectionData = new JMSEndpointDescriptorDTO();
        connectionData.setConnectionFactory(endpoint.getConnectionFactory());
        String jndiProperties = endpoint.getJndiProperties();

        Map<String, String> properties = null;
        try {
            TypeReference<Map<String, String>> typeRef =
                    new TypeReference<Map<String, String>>() {
                    };
            properties = serializer.readValue(jndiProperties, typeRef);
        } catch (IOException e) {
        }
        connectionData.setJndiProperties(properties);
        QueueDTO queueDescriptor = new QueueDTO();
        queueDescriptor.setPassword(action.getPassword());
        queueDescriptor.setQueueName(action.getQueueName());
        queueDescriptor.setUsername(action.getUsername());
        this.queueDescriptor = queueDescriptor;
    }

    @Override
    public void testConnection() throws ConnectionException {
        //TODO
    }

    @Override
    public <Request, Response> Response sendRequest(Request data,
                                                    Class<Response> responseClass)
            throws Exception {
        Context ctx = new InitialContext(
                new Hashtable<>(connectionData.getJndiProperties()));
        QueueConnectionFactory factory = (QueueConnectionFactory) ctx
                .lookup(connectionData.getConnectionFactory());
        QueueConnection connection = factory.createQueueConnection(
                queueDescriptor.getUsername(), queueDescriptor.getPassword());
        QueueSession session =
                connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(queueDescriptor.getQueueName());
        MessageProducer producer = session.createProducer(queue);
        String dataJson = serializer.writeValueAsString(data);
        TextMessage message = session.createTextMessage(dataJson);
        producer.send(message);
        session.close();
        connection.close();
        return null;
    }
}
