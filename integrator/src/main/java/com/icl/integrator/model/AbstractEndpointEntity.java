package com.icl.integrator.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 29.01.14
 * Time: 12:03
 * To change this template use File | Settings | File Templates.
 */
@MappedSuperclass
public abstract class AbstractEndpointEntity extends AbstractEntity {

    @Column(unique = true, nullable = false, length = 255,
            name = "SERVICE_NAME")
    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}

