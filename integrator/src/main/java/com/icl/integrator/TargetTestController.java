package com.icl.integrator;

import com.icl.integrator.dto.RequestDataDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.springapi.TargetSpringService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 24.01.14
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
@RequestMapping(value = "/destination",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
@Controller
public class TargetTestController implements TargetSpringService {

    private final Log logger = LogFactory.getLog(SourceTestController.class);

    @Override
    public ResponseDTO handleRequest(@RequestBody(required = true)
                                     RequestDataDTO requestDataDTO) {
        logger.info("destination received a request from " +
                            "integrator " + requestDataDTO);
        if(requestDataDTO.empty()){
            return new ResponseDTO<>();
        }
        return new ResponseDTO<>("RESPONSE", String.class);
    }

}
