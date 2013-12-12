package com.icl.integrator.util;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public interface EndpointConnector {

    public <Request, Response> Response sendRequest(
            Request data,Class<Response> responseClass)throws Exception;

}
