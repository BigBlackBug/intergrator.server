package com.icl.integrator.dto.registration;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 18.12.13
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public class HttpActionDTO implements ActionDescriptor {

    private String path;

    public HttpActionDTO() {
    }

    public HttpActionDTO(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "path";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HttpActionDTO actionDTO = (HttpActionDTO) o;

        if (!path.equals(actionDTO.path)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
