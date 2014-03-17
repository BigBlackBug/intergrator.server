package com.icl.integrator.model;

import com.icl.integrator.dto.util.EndpointType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 17.12.13
 * Time: 10:44
 * To change this template use File | Settings | File Templates.
 */
@Entity
@DiscriminatorValue(value = "JMS")
public class JMSServiceEndpoint extends AbstractEndpointEntity<JMSAction> {

	@Column(length = 255, name = "CONNECTION_FACTORY")
	private String connectionFactory;

	@Column(length = 255, name = "JNDI_PROPERTIES")
	private String jndiProperties;

	public JMSServiceEndpoint() {
		super(EndpointType.JMS);
	}

	@Override
	public void setActions(Set<JMSAction> actions) {
		this.actions.clear();
		this.actions.addAll(actions);
	}

	@Override
	public JMSAction getActionByName(String actionName) {
		for (AbstractActionEntity entity : this.actions) {
			if (entity.getActionName().equals(actionName)) {
				return (JMSAction) entity;
			}
		}
		return null;
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
}
