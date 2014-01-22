package com.icl.integrator.springapi;

import com.icl.integrator.api.SourceService;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ResponseFromIntegratorDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 21.01.14
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
public interface SourceSpringService extends SourceService {

    @Override
    public void
    handleResponseFromIntegrator(
            @RequestBody ResponseFromIntegratorDTO<Map<String,
                    ResponseDTO<UUID>>> responseDTO);

    @Override
    public void handleResponseFromTarget(@RequestBody ResponseFromTargetDTO
                                                 responseDTO);
}
