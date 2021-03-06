package com.icl.integrator.dto.source;

import com.icl.integrator.dto.util.EndpointType;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 17.12.13
 * Time: 10:39
 * To change this template use File | Settings | File Templates.
 */
public class JMSEndpointDescriptorDTO extends EndpointDescriptor {

    private Map<String, String> jndiProperties;

    private String connectionFactory;

    JMSEndpointDescriptorDTO() {
        super(EndpointType.JMS);
    }

    public JMSEndpointDescriptorDTO(String connectionFactory,
                                    Map<String, String> jndiProperties) {
        this();
        this.connectionFactory = connectionFactory;
        this.jndiProperties = jndiProperties;
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
}
