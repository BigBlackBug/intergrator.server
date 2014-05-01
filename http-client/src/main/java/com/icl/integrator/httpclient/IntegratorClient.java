package com.icl.integrator.httpclient;

import com.icl.integrator.springapi.IntegratorHttpAPI;

/**
 * Created by BigBlackBug on 24.04.2014.
 */
public interface IntegratorClient extends IntegratorHttpAPI {

	public void login(String username, String password) throws IntegratorClientException;

	public void logout() throws IntegratorClientException;

}
