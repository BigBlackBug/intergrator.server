package com.icl.integrator.api;

import com.icl.integrator.dto.ResponseFromTargetDTO;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 21.01.14
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public interface SourceService {

    public void handleResponseFromTarget(ResponseFromTargetDTO responseDTO);

}
