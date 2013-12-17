package com.icl.integrator.dto.registration;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 16.12.13
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class ActionDTO {

    private String name;

    private String url;

    public ActionDTO(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public ActionDTO() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
