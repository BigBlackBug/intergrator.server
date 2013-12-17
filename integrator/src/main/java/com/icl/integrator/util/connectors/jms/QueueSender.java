package com.icl.integrator.util.connectors.jms;

import org.springframework.stereotype.Component;

@Component
public class QueueSender {

    public void send(final String message) {
//        Properties props = new Properties();
//        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
//                          "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
//        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
//        Context ctx = new InitialContext(props);
//// lookup the connection factory
//        QueueConnectionFactory factory =
//                (QueueConnectionFactory) ctx
//                        .lookup("ConnectionFactory");
//// create a new TopicConnection for pub/sub messaging
//        QueueConnection conn = factory.createQueueConnection();
//// create a new TopicSession for the client
//        QueueSession session =
//                conn.createQueueSession(false, TopicSession.AUTO_ACKNOWLEDGE);
//        Queue queue = session.createQueue("Queue.Name");
//        MessageProducer producer = session.createProducer(queue);
//        TextMessage olo = session.createTextMessage("OLO");
//        producer.send(olo);
    }
}