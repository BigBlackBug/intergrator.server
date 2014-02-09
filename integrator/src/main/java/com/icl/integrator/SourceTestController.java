package com.icl.integrator;

import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.springapi.SourceSpringService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 11.12.13
 * Time: 12:43
 * To change this template use File | Settings | File Templates.
 */
@RequestMapping(value = "/source",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
@Controller
public class SourceTestController implements SourceSpringService {

    private final Log logger = LogFactory.getLog(SourceTestController.class);

    @Override //TODO fix conversion
    public void handleResponseFromTarget(@RequestBody(required = false)
                                             ResponseDTO<ResponseFromTargetDTO> responseDTO) {
        logger.info("received target response from integrator from ");
    }

}
