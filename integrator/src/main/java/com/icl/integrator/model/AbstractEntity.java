package com.icl.integrator.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 29.01.14
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @Type(type = "com.icl.integrator.model.OracleGuidType")
    @Column(name = "ID")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private UUID id;

    public UUID getId() {
        return id;
    }

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AbstractEntity)) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		AbstractEntity ae = (AbstractEntity) obj;
		return ae.getId() != null && ae.getId().equals(getId());
	}

	@Override
	public int hashCode() {
		if (id != null) {
			return id.hashCode();
		}
		return super.hashCode();
	}
}
