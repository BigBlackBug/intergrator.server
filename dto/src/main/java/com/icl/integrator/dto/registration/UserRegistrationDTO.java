package com.icl.integrator.dto.registration;

/**
 * Created by BigBlackBug on 07.05.2014.
 */
public class UserRegistrationDTO {

	private String username;

	private String password;

	UserRegistrationDTO() {
	}

	public UserRegistrationDTO(String username, String password) {

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

		UserRegistrationDTO that = (UserRegistrationDTO) o;

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
