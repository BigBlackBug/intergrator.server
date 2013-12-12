package com.icl.integrator.async;

import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.ResponseToSourceDTO;
import com.icl.integrator.dto.SourceDataDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public abstract class AsyncConnectorController {

    private static Log logger = LogFactory.getLog(
            AsyncConnectorController.class);

    @RequestMapping(value = "processData")
    public
    @ResponseBody
    ResponseToSourceDTO
    process(@RequestBody(required = true) SourceDataDTO packet,
            HttpServletRequest request) {
        logger.info(MessageFormat.format("Received a request from source " +
                                                 "({0}:{1,number,#})",
                    request.getRemoteHost(),request.getRemotePort()));
        AsyncPacketProcessor processor = createProcessor();
        Map<String, String> serviceToReqID = processor.process(packet);
        ResponseFromTargetDTO<Map> fromTargetDTO =
                new ResponseFromTargetDTO<>(serviceToReqID, Map.class);
        ResponseToSourceDTO responseToSourceDTO =
                new ResponseToSourceDTO(fromTargetDTO);
        return responseToSourceDTO;
    }

    protected abstract AsyncPacketProcessor createProcessor();
}
