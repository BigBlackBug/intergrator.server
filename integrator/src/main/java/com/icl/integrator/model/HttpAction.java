package com.icl.integrator.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 04.12.13
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "HTTP_ACTION")
public class HttpAction {

    @Column(nullable = false, length = 255, name = "ACTION_NAME")
    private String actionName;

    @Column(nullable = false, length = 255, name = "ACTION_URL")
    private String actionURL;

    @Id
    @Type(type = "com.icl.integrator.model.OracleGuidType")
    @Column(name = "ACTION_MAP_ID")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private UUID id;

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

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionURL() {
        return actionURL;
    }

    public void setActionURL(String actionURL) {
        this.actionURL = actionURL;
    }
}