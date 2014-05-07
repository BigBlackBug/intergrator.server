package com.icl.integrator.httpclient;

import com.icl.integrator.springapi.IntegratorHttpAPI;
import com.icl.integrator.springapi.IntegratorManagementHttpAPI;

/**
 * Created by BigBlackBug on 24.04.2014.
 */
public interface IntegratorClient extends IntegratorHttpAPI, IntegratorManagementHttpAPI {

	public void login(String username, String password) throws IntegratorClientException;

	public void logout() throws IntegratorClientException;

}
