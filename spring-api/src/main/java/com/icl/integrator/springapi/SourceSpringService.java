package com.icl.integrator.springapi;

import com.icl.integrator.api.SourceService;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 21.01.14
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
public interface SourceSpringService extends SourceService {

    @Override
    public void handleResponseFromTarget(@RequestBody ResponseFromTargetDTO
                                                 responseDTO);
}
