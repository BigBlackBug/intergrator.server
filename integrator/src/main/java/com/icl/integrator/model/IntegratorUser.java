package com.icl.integrator.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;

//TODO add salt
@Entity
@Table(name = "INTEGRATOR_USER")
public class IntegratorUser extends AbstractEntity implements UserDetails {

	@Column(name = "USERNAME", nullable = false, updatable = false)
	private String username;

	@Column(name = "PASSWORD_HASH", nullable = false, updatable = false)
	private String password;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ROLE_ID", nullable = false)
	private Role role;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(new SimpleGrantedAuthority(role.getRole().toString()));
	}

	public Role getRole() {
		return role;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
