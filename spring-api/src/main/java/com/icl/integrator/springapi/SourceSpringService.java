package com.icl.integrator.springapi;

import com.icl.integrator.api.SourceService;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 24.01.14
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */

public interface SourceSpringService extends SourceService {

    @Override
    @RequestMapping(value = "/handleResponseFromTarget",
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    method = {RequestMethod.POST, RequestMethod.HEAD})
    public @ResponseBody void handleResponseFromTarget(@RequestBody(required = false)
                                             ResponseDTO<ResponseFromTargetDTO> responseDTO);
}
