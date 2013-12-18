package com.icl.integrator.dto.source;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 17.12.13
 * Time: 10:39
 * To change this template use File | Settings | File Templates.
 */
public class JMSEndpointDescriptorDTO implements EndpointDescriptor {

    private Map<String, String> jndiProperties;

    private String connectionFactory;

//    private QueueDTO queueDTO;   //action

    public JMSEndpointDescriptorDTO() {
    }

    public String getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(String connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Map<String, String> getJndiProperties() {
        return jndiProperties;
    }

    public void setJndiProperties(Map<String, String> jndiProperties) {
        this.jndiProperties = jndiProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JMSEndpointDescriptorDTO that = (JMSEndpointDescriptorDTO) o;

        if (!connectionFactory.equals(that.connectionFactory)) {
            return false;
        }
        if (!jndiProperties.equals(that.jndiProperties)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = jndiProperties.hashCode();
        result = 31 * result + connectionFactory.hashCode();
        return result;
    }
//
//    public QueueDTO getQueueDTO() {
//        return queueDTO;
//    }
//
//    public void setQueueDTO(QueueDTO queueDTO) {
//        this.queueDTO = queueDTO;
//    }
}
