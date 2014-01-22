package com.icl.integrator.api;

import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ResponseFromIntegratorDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;

import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 21.01.14
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public interface SourceService {

    //TODO тут будет поддержка всех методов.
    public void handleResponseFromIntegrator(
            ResponseFromIntegratorDTO<Map<String, ResponseDTO<UUID>>> responseDTO);

    public void handleResponseFromTarget(ResponseFromTargetDTO responseDTO);

}
