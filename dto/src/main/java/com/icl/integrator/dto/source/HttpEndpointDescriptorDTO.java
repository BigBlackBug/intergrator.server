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

    public HttpEndpointDescriptorDTO() {
    }

    public HttpEndpointDescriptorDTO(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HttpEndpointDescriptorDTO that = (HttpEndpointDescriptorDTO) o;

        if (port != that.port) {
            return false;
        }
        if (!host.equals(that.host)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        return result;
    }
}
