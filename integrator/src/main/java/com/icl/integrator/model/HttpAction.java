package com.icl.integrator.model;

import com.icl.integrator.util.EndpointType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 04.12.13
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
@Entity
@DiscriminatorValue(value = "HTTP")
public class HttpAction extends AbstractActionEntity {

	@Column(length = 255, name = "ACTION_URL")
	private String actionURL;

	public HttpAction() {
		super(EndpointType.HTTP);
	}

	public String getActionURL() {
		return actionURL;
	}

	public void setActionURL(String actionURL) {
		this.actionURL = actionURL;
	}
}
