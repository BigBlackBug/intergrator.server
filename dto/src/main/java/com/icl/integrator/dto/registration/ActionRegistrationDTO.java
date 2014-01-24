package com.icl.integrator.dto.registration;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 24.01.14
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
public class ActionRegistrationDTO<T extends ActionDescriptor> {

    private ActionEndpointDTO<T> action;

    private boolean forceRegister;

    public ActionRegistrationDTO() {
    }

    public ActionRegistrationDTO(ActionEndpointDTO<T> action,
                                 boolean forceRegister) {
        this.action = action;
        this.forceRegister = forceRegister;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ActionRegistrationDTO that = (ActionRegistrationDTO) o;

        if (forceRegister != that.forceRegister) {
            return false;
        }
        if (!action.equals(that.action)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = action.hashCode();
        result = 31 * result + (forceRegister ? 1 : 0);
        return result;
    }

    public ActionEndpointDTO<T> getAction() {

        return action;
    }

    public void setAction(ActionEndpointDTO<T> action) {
        this.action = action;
    }

    public boolean isForceRegister() {
        return forceRegister;
    }

    public void setForceRegister(boolean forceRegister) {
        this.forceRegister = forceRegister;
    }
}
