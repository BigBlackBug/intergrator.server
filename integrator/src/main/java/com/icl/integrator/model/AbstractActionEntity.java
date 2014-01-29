package com.icl.integrator.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 29.01.14
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
@MappedSuperclass
public abstract class AbstractActionEntity extends AbstractEntity {

    @Column(nullable = false, length = 255, name = "ACTION_NAME")
    private String actionName;

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}
