package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.util.EndpointType;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 17.12.13
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
public class QueueDTO extends ActionDescriptor implements Serializable {

	private String username;

	private String password;

	private String queueName;

	public QueueDTO() {
		super(null, EndpointType.JMS);
	}

	public QueueDTO(String queueName, String username, String password, ActionMethod actionMethod) {
		super(actionMethod, EndpointType.JMS);
		this.queueName = queueName;
		this.username = username;
		this.password = password;
	}

	public QueueDTO(String queueName, ActionMethod actionMethod) {
		super(actionMethod, EndpointType.JMS);
		this.queueName = queueName;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		QueueDTO queueDTO = (QueueDTO) o;

		if (password != null ? !password.equals(queueDTO.password) : queueDTO.password != null) {
			return false;
		}
		if (queueName != null ? !queueName.equals(queueDTO.queueName) :
				queueDTO.queueName != null) {
			return false;
		}
		if (username != null ? !username.equals(queueDTO.username) : queueDTO.username != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (username != null ? username.hashCode() : 0);
		result = 31 * result + (password != null ? password.hashCode() : 0);
		result = 31 * result + (queueName != null ? queueName.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "username: " + username + " queue: " + queueName;
	}
}
