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
