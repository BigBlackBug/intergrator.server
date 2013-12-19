package com.icl.integrator.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 17.12.13
 * Time: 10:44
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "IJE")
public class JMSServiceEndpoint {

    @Column(unique = true, nullable = false, length = 255,
            name = "SERVICE_NAME")
    private String serviceName;

    @OneToMany(fetch = FetchType.EAGER,
               mappedBy = "jmsServiceEndpoint")
    @Cascade(value = org.hibernate.annotations.CascadeType.ALL)
    private List<JMSAction> jmsActions = new ArrayList<>();

    @Id
    @Type(type = "com.icl.integrator.model.OracleGuidType")
    @Column(name = "ENDPOINT_ID")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private UUID id;

    @Column(nullable = false, length = 255, name = "JNDI_PROPERTIES")
    private String jndiProperties;

    @Column(nullable = false, length = 255, name = "CONNECTION_FACTORY")
    private String connectionFactory;

    public List<JMSAction> getJmsActions() {
        return jmsActions;
    }

    public void setJmsActions(List<JMSAction> jmsActions) {
        this.jmsActions = jmsActions;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public UUID getId() {
        return id;
    }

    public String getJndiProperties() {
        return jndiProperties;
    }

    public void setJndiProperties(String jndiProperties) {
        this.jndiProperties = jndiProperties;
    }

    public String getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(String connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public JMSAction getActionByName(String actionName) {
        for (JMSAction action : jmsActions) {
            if (action.getActionName().equals(actionName)) {
                return action;
            }
        }
        return null;
    }

    public void addAction(JMSAction action) {
        jmsActions.add(action);
    }
}
