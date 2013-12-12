package com.icl.integrator.dto.source;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 11.12.13
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class HttpEndpointDescriptorDTO implements EndpointDescriptor {

    private String host;

    private int port;

    private String path;

    public HttpEndpointDescriptorDTO() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
