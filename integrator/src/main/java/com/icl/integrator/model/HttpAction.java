package com.icl.integrator.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 04.12.13
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "HTTP_ACTION", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ACTION_NAME", "ENDPOINT_ID"})
})
public class HttpAction extends AbstractActionEntity{

    @Column(nullable = false, length = 255, name = "ACTION_URL")
    private String actionURL;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ENDPOINT_ID", nullable = false,
                updatable = false)
    @Cascade(value = org.hibernate.annotations.CascadeType.PERSIST)
    private HttpServiceEndpoint httpServiceEndpoint;

    public HttpAction() {
    }

    public HttpServiceEndpoint getHttpServiceEndpoint() {
        return httpServiceEndpoint;
    }

    public void setHttpServiceEndpoint(
            HttpServiceEndpoint httpServiceEndpoint) {
        this.httpServiceEndpoint = httpServiceEndpoint;
    }

    public String getActionURL() {
        return actionURL;
    }

    public void setActionURL(String actionURL) {
        this.actionURL = actionURL;
    }
}
