package com.icl.integrator.dto;

import com.icl.integrator.dto.registration.ActionDescriptor;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 18.12.13
 * Time: 11:31
 * To change this template use File | Settings | File Templates.
 */
public class ServiceDTO {

    private EndpointDTO endpoint;

    private ActionDescriptor sourceResponseAction;

    private ActionDescriptor targetResponseAction;

    public ServiceDTO() {

    }

    public ServiceDTO(EndpointDTO endpoint,
                      ActionDescriptor sourceResponseAction,
                      ActionDescriptor targetResponseAction) {
        this.endpoint = endpoint;
        this.sourceResponseAction = sourceResponseAction;
        this.targetResponseAction = targetResponseAction;
    }

    public ActionDescriptor getSourceResponseAction() {
        return sourceResponseAction;
    }

    public void setSourceResponseAction(ActionDescriptor sourceResponseAction) {
        this.sourceResponseAction = sourceResponseAction;
    }

    public ActionDescriptor getTargetResponseAction() {
        return targetResponseAction;
    }

    public void setTargetResponseAction(ActionDescriptor targetResponseAction) {
        this.targetResponseAction = targetResponseAction;
    }

    public EndpointDTO getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointDTO endpoint) {
        this.endpoint = endpoint;
    }
//
//    public ActionDescriptor getActionDescriptor() {
//        return actionDescriptor;
//    }
//
//    public void setActionDescriptor(ActionDescriptor actionDescriptor) {
//        this.actionDescriptor = actionDescriptor;
//    }

}
