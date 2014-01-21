package com.icl.integrator.api;

import com.icl.integrator.dto.RequestDataDTO;
import com.icl.integrator.dto.ResponseDTO;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 21.01.14
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
public interface TargetService<T> {

    public ResponseDTO<T> acceptRequest(RequestDataDTO requestDataDTO);

}
