package com.icl.integrator;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.ResponseToSourceDTO;
import com.icl.integrator.dto.SourceDataDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.services.PacketProcessor;
import com.icl.integrator.services.RegistrationService;
import com.icl.integrator.services.TargetRegistrationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 9:16
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/integrator/",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
public abstract class MainController {

    private static Log logger = LogFactory.getLog(MainController.class);

    @Autowired
    private RegistrationService registrationService;

    @RequestMapping(value = "processData")
    public
    @ResponseBody
    ResponseToSourceDTO
    process(@RequestBody(required = true) SourceDataDTO packet,
            HttpServletRequest request) {
        logger.info(MessageFormat.format("Received a request from source " +
                                                 "({0}:{1,number,#})",
                                         request.getRemoteHost(),
                                         request.getRemotePort()));
        PacketProcessor processor = createProcessor();
        Map<String, String> serviceToReqID = processor.process(packet);
        ResponseFromTargetDTO<Map> fromTargetDTO =
                new ResponseFromTargetDTO<>(serviceToReqID, Map.class);
        ResponseToSourceDTO responseToSourceDTO =
                new ResponseToSourceDTO(fromTargetDTO);
        return responseToSourceDTO;
    }

    /*
    * {service:{name:'',port:'',url:''},actions:[{name:'',url:''}]}
    * */

    @RequestMapping(value = "registerTarget")
    public
    @ResponseBody
    ResponseFromTargetDTO<Map>
    registerTarget(@RequestBody(required = true)
                   TargetRegistrationDTO registrationDTO,
                   HttpServletRequest request) {
        logger.info(MessageFormat.format("Received a registration request " +
                                                 "from source " +
                                                 "({0}:{1,number,#})",
                                         request.getRemoteHost(),
                                         request.getRemotePort()));
        ResponseFromTargetDTO<Map> response;
        try {
            Map result = registrationService.register(registrationDTO);
            response = new ResponseFromTargetDTO<>(result, Map.class);
        } catch (TargetRegistrationException ex) {
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setErrorMessage(ex.getMessage());
            errorDTO.setDeveloperMessage(ex.getCause().getMessage());
            response = new ResponseFromTargetDTO<>(errorDTO);
        }
        return response;
    }

    protected abstract PacketProcessor createProcessor();
}
