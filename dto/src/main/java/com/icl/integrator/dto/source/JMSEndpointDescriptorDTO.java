package com.icl.integrator.dto.source;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 17.12.13
 * Time: 10:39
 * To change this template use File | Settings | File Templates.
 */
public class JMSEndpointDescriptorDTO {

    private Map<String, String> jndiProperties;

    private String connectionFactory;

    private String username;

    private String password;

    private String queueName;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
