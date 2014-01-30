package com.icl.integrator.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 17.12.13
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "JMS_ACTION", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ACTION_NAME", "ENDPOINT_ID"})
})
public class JMSAction extends AbstractActionEntity{

    @Column(nullable = false, length = 255, name = "QUEUE_NAME")
    private String queueName;

    @Column(length = 255, name = "QUEUE_USERNAME")
    private String username;

    @Column(length = 255, name = "QUEUE_PASSWORD")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ENDPOINT_ID", nullable = false,
                updatable = false)
    @Cascade(value = org.hibernate.annotations.CascadeType.PERSIST)
    private JMSServiceEndpoint jmsServiceEndpoint;

    public JMSAction() {

    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public JMSServiceEndpoint getJmsServiceEndpoint() {
        return jmsServiceEndpoint;
    }

    public void setJmsServiceEndpoint(JMSServiceEndpoint jmsServiceEndpoint) {
        this.jmsServiceEndpoint = jmsServiceEndpoint;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
