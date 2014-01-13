package com.icl.integrator;

import com.icl.integrator.dto.RequestToTargetDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.ResponseToSourceDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
public class TestController {

    private final Log logger = LogFactory.getLog(TestController.class);

    @RequestMapping(value = "accept_response", method = RequestMethod.POST)
    public void
    acceptResponse(@RequestBody ResponseToSourceDTO responseDTO) {
        logger.info("accepted response from integrator from " + responseDTO
                .getServiceName());
    }

    @RequestMapping(value = "destination", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseFromTargetDTO<String>
    destination(@RequestBody RequestToTargetDTO requestToTargetDTO) {
        logger.info("destination accepted request from " +
                            "integrator " + requestToTargetDTO);
        return new ResponseFromTargetDTO<>("RESPONSE", String.class);
    }

    @RequestMapping(value = "destination2", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseFromTargetDTO<String>
    destination2(@RequestBody RequestToTargetDTO requestToTargetDTO) {
        logger.info("destination2 accepted request from " +
                            "integrator " + requestToTargetDTO);
        return new ResponseFromTargetDTO<>("RESPONSE", String.class);
    }
}
