package com.icl.integrator.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 17.12.13
 * Time: 10:44
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "JMS_ENDPOINT")
public class JMSServiceEndpoint extends AbstractEndpointEntity {

    @OneToMany(fetch = FetchType.EAGER,
               mappedBy = "jmsServiceEndpoint")
    @Cascade(value = org.hibernate.annotations.CascadeType.ALL)
    private List<JMSAction> jmsActions = new ArrayList<>();

    @Column(nullable = false, length = 255, name = "CONNECTION_FACTORY")
    private String connectionFactory;

    @Column(nullable = false, length = 255, name = "JNDI_PROPERTIES")
    private String jndiProperties;

    public List<JMSAction> getJmsActions() {
        return jmsActions;
    }

    public void setJmsActions(List<JMSAction> jmsActions) {
        this.jmsActions = jmsActions;
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
