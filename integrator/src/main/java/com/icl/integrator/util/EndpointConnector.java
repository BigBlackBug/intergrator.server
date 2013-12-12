package com.icl.integrator.util;

import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.SourceDataDTO;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public interface EndpointConnector {

	public ResponseFromTargetDTO sendRequest(SourceDataDTO packet) throws Exception;

}
