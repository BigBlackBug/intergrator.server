package com.icl.integrator.model;

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
@Table(name = "ACTION_MAPPING")
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
    @JoinColumn(name = "ADDRESS_MAPPING_ID", nullable = false,
                updatable = false)
    private HttpServiceEndpoint httpServiceEndpoint;

    public HttpAction() {
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public void setActionURL(String actionURL) {
        this.actionURL = actionURL;
    }

    public HttpServiceEndpoint getHttpServiceEndpoint() {
        return httpServiceEndpoint;
    }

    public void setHttpServiceEndpoint(HttpServiceEndpoint httpServiceEndpoint) {
        this.httpServiceEndpoint = httpServiceEndpoint;
    }
}
