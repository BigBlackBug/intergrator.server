package com.icl.integrator.model;

import com.icl.integrator.util.EndpointType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 04.12.13
 * Time: 12:51
 * To change this template use File | Settings | File Templates.
 */
@Entity
@DiscriminatorValue(value = "HTTP")
public class HttpServiceEndpoint extends AbstractEndpointEntity<HttpAction> {

	@Column(name = "SERVICE_PORT")
	private Integer servicePort;

	@Column(length = 255, name = "SERVICE_URL")
	private String serviceURL;

	public HttpServiceEndpoint() {
		super(EndpointType.HTTP);
	}

	@Override
	public void setActions(Set<HttpAction> actions) {
		this.actions.clear();
		this.actions.addAll(actions);
	}

	@Override
	public HttpAction getActionByName(String actionName) {
		for (AbstractActionEntity entity : this.actions) {
			if (entity.getActionName().equals(actionName)) {
				return (HttpAction) entity;
			}
		}
		return null;
	}

	public Integer getServicePort() {
		return servicePort;
	}

	public void setServicePort(Integer servicePort) {
		this.servicePort = servicePort;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}
}
