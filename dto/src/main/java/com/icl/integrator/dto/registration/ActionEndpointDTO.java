package com.icl.integrator.dto.registration;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 18.12.13
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class ActionEndpointDTO<T extends ActionDescriptor> {

    private String actionName;

    private boolean forceRegister;

    private T actionDescriptor;

    public ActionEndpointDTO() {
    }

    public ActionEndpointDTO(String actionName, T actionDescriptor) {
        this.actionName = actionName;
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

        if (forceRegister != that.forceRegister) {
            return false;
        }
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
        result = 31 * result + (forceRegister ? 1 : 0);
        result = 31 * result + actionDescriptor.hashCode();
        return result;
    }

    public boolean isForceRegister() {
        return forceRegister;
    }

    public void setForceRegister(boolean forceRegister) {
        this.forceRegister = forceRegister;
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
}
