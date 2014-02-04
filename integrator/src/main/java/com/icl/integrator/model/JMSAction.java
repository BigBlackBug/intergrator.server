package com.icl.integrator.model;

import com.icl.integrator.util.EndpointType;
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
@DiscriminatorValue(value = "JMS")
public class JMSAction extends AbstractActionEntity {

	@Column(length = 255, name = "QUEUE_NAME")
	private String queueName;

	@Column(length = 255, name = "QUEUE_USERNAME")
	private String username;

	@Column(length = 255, name = "QUEUE_PASSWORD")
	private String password;

	public JMSAction() {
		super(EndpointType.JMS);
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
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
