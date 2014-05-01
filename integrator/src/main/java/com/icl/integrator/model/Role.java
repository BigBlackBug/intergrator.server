package com.icl.integrator.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "ROLE")
public class Role extends AbstractEntity {

	@Enumerated(EnumType.STRING)
	@Column(name = "ROLE", nullable = false, updatable = false)
	private RoleEnum role;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "role", fetch = FetchType.LAZY)
	private Set<IntegratorUser> userRoles;

	public RoleEnum getRole() {
		return role;
	}

	public void setRole(RoleEnum role) {
		this.role = role;
	}

	public Set<IntegratorUser> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Set<IntegratorUser> userRoles) {
		this.userRoles = userRoles;
	}

}