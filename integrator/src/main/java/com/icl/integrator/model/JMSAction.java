package com.icl.integrator.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 17.12.13
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "IJA")
public class JMSAction {

    @Column(nullable = false, length = 255, name = "ACTION_NAME")
    private String actionName;

    @Column(nullable = false, length = 255, name = "QUEUE_NAME")
    private String queueName;

    @Column(length = 255, name = "QUEUE_USERNAME")
    private String username;

    @Column(length = 255, name = "QUEUE_PASSWORD")
    private String password;

    @Id
    @Type(type = "com.icl.integrator.model.OracleGuidType")
    @Column(name = "ACTION_ID")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ENDPOINT_ID", nullable = false,
                updatable = false)
    private JMSServiceEndpoint jmsServiceEndpoint;

    public JMSAction() {

    }

    public UUID getId() {
        return id;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
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
