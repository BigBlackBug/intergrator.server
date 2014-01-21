package com.icl.integrator.api;

import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ResponseToSourceDTO;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 21.01.14
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public interface SourceService {

    //TODO probably refactor to something more meaningful
    public void acceptSourceResponse(Map<String, ResponseDTO<String>>
                                             responseDTO);

    public void acceptTargetResponse(ResponseToSourceDTO responseDTO);

}
