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
@Table(name = "ADDRESS_MAPPING")
public class AddressMapping {

    @Column(unique = true, nullable = false, length = 255,
            name = "SERVICE_NAME")
    private String serviceName;

    @Column(name = "SERVICE_PORT")
    private Integer servicePort;

    @Column(nullable = false, length = 255,
            name = "SERVICE_URL")
    private String serviceURL;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "addressMapping")
    @Cascade(value = org.hibernate.annotations.CascadeType.ALL)
    private List<ActionMapping> actionMappings = new ArrayList<>();

    @Id
    @Type(type = "com.icl.integrator.model.OracleGuidType")
    @Column(name = "ADDR_MAP_ID")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private UUID id;

    public AddressMapping() {
    }

    public Integer getServicePort() {
        return servicePort;
    }

    public void setServicePort(Integer servicePort) {
        this.servicePort = servicePort;
    }

    public List<ActionMapping> getActionMappings() {
        return actionMappings;
    }

    public void setActionMappings(List<ActionMapping> actionMappings) {
        this.actionMappings = actionMappings;
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

    public void addActionMaping(ActionMapping actionMapping) {
        actionMappings.add(actionMapping);
    }
}
