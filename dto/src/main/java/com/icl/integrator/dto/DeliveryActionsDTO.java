package com.icl.integrator.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by e.shahmaev on 21.03.14.
 */
public class DeliveryActionsDTO implements Serializable{

    private String actionName;

    private List<ServiceDTO> services;

    DeliveryActionsDTO() {
    }

    public DeliveryActionsDTO(String actionName,
                              List<ServiceDTO> services) {
        this.actionName = actionName;
        this.services = services;
    }

    public DeliveryActionsDTO(String actionName,
                              ServiceDTO service) {
        this.actionName = actionName;
        this.services = new ArrayList<ServiceDTO>();
        services.add(service);
    }

    public DeliveryActionsDTO(String actionName,
                              ServiceDTO... services) {
        this.actionName = actionName;
        this.services = Arrays.asList(services);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DeliveryActionsDTO that = (DeliveryActionsDTO) o;

        if (!actionName.equals(that.actionName)) {
            return false;
        }
        if (!services.equals(that.services)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = actionName.hashCode();
        result = 31 * result + services.hashCode();
        return result;
    }

    public String getActionName() {
        return actionName;
    }

    public List<ServiceDTO> getServices() {
        return services;
    }
}
