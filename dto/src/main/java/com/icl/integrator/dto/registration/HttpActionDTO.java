package com.icl.integrator.dto.registration;

import com.icl.integrator.util.EndpointType;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 18.12.13
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public class HttpActionDTO extends ActionDescriptor {

    private String path;

    public HttpActionDTO() {
	    super(null, EndpointType.HTTP);
    }

    public HttpActionDTO(String path,ActionMethod actionMethod) {
	    super(actionMethod,EndpointType.HTTP);
        this.path = path;
    }

    @Override
    public String toString() {
        return "path = "+path;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		HttpActionDTO that = (HttpActionDTO) o;

		if (!path.equals(that.path)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + path.hashCode();
		return result;
	}

	public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
