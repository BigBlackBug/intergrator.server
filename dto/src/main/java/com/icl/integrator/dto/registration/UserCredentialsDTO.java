package com.icl.integrator.dto.registration;

/**
 * Created by BigBlackBug on 07.05.2014.
 */
public class UserCredentialsDTO {

	private String username;

	private String password;

	UserCredentialsDTO() {
	}

	public UserCredentialsDTO(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		UserCredentialsDTO that = (UserCredentialsDTO) o;

		if (!password.equals(that.password)) {
			return false;
		}
		if (!username.equals(that.username)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = username.hashCode();
		result = 31 * result + password.hashCode();
		return result;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
