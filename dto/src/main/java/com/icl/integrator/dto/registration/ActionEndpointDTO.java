package com.icl.integrator.dto.registration;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 18.12.13
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class ActionEndpointDTO<T extends ActionDescriptor> implements Serializable {

    private String actionName;

    private T actionDescriptor;

    ActionEndpointDTO() {
    }

    public ActionEndpointDTO(String actionName, T actionDescriptor) {
        this.actionName = actionName;
        this.actionDescriptor = actionDescriptor;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public T getActionDescriptor() {
        return actionDescriptor;
    }

    public void setActionDescriptor(T actionDescriptor) {
        this.actionDescriptor = actionDescriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ActionEndpointDTO that = (ActionEndpointDTO) o;

        if (!actionDescriptor.equals(that.actionDescriptor)) {
            return false;
        }
        if (!actionName.equals(that.actionName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = actionName.hashCode();
        result = 31 * result + actionDescriptor.hashCode();
        return result;
    }
}


