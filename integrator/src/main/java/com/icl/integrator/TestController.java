package com.icl.integrator;

import com.icl.integrator.dto.ResponseFromIntegratorDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.RequestDataDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.springapi.SourceSpringService;
import com.icl.integrator.springapi.TargetSpringService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 11.12.13
 * Time: 12:43
 * To change this template use File | Settings | File Templates.
 */
@RequestMapping(value = "/api/", consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
@Controller
public class TestController
        implements SourceSpringService, TargetSpringService {

    private final Log logger = LogFactory.getLog(TestController.class);

    @Override
    @RequestMapping(value = "accept_source_response",
                    method = RequestMethod.POST)
    public void handleResponseFromIntegrator(
            @RequestBody ResponseFromIntegratorDTO responseDTO) {
        logger.info("received source response from integrator from " +
                            responseDTO);
    }

    @RequestMapping(value = "accept_target_response",
                    method = RequestMethod.POST)
    @Override
    public void handleResponseFromTarget(
            @RequestBody ResponseFromTargetDTO responseDTO) {
        logger.info("received target response from integrator from " +
                            responseDTO.getServiceName());
    }

    //TODO maybe needs response body here
    @RequestMapping(value = "destination", method = RequestMethod.POST)
    @Override
    public ResponseDTO handleRequest(
            @RequestBody RequestDataDTO requestDataDTO) {
        logger.info("destination received a request from " +
                            "integrator " + requestDataDTO);
        return new ResponseDTO<>("RESPONSE", String.class);
    }
//
//    @RequestMapping(value = "destination2", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    ResponseDTO<String>
//    destination2(@RequestBody RequestDataDTO requestDataDTO) {
//        logger.info("destination2 accepted request from " +
//                            "integrator " + requestDataDTO);
//        return new ResponseDTO<>("RESPONSE", String.class);
//    }
}
