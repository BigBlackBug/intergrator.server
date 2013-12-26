package com.icl.integrator.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 04.12.13
 * Time: 12:51
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "HTTP_ENDPOINT")
public class HttpServiceEndpoint {

    @Column(unique = true, nullable = false, length = 255,
            name = "SERVICE_NAME")
    private String serviceName;

    @Column(name = "SERVICE_PORT")
    private Integer servicePort;

    @Column(nullable = false, length = 255, name = "SERVICE_URL")
    private String serviceURL;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "httpServiceEndpoint")
    @Cascade(value = org.hibernate.annotations.CascadeType.ALL)
    private List<HttpAction> httpActions = new ArrayList<>();

    @Id
    @Type(type = "com.icl.integrator.model.OracleGuidType")
    @Column(name = "ADDR_MAP_ID")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private UUID id;

    public HttpServiceEndpoint() {
    }

    public Integer getServicePort() {
        return servicePort;
    }

    public void setServicePort(Integer servicePort) {
        this.servicePort = servicePort;
    }

    public List<HttpAction> getHttpActions() {
        return httpActions;
    }

    public void setHttpActions(
            List<HttpAction> httpActions) {
        this.httpActions = httpActions;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void addActionMaping(HttpAction httpAction) {
        httpActions.add(httpAction);
    }

    public void addAction(HttpAction action) {
        httpActions.add(action);
    }
}
