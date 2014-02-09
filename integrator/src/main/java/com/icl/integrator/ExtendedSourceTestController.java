package com.icl.integrator;

import com.icl.integrator.dto.FullServiceDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.ServiceDTO;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.springapi.ExtendedSourceSpringService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 24.01.14
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
@RequestMapping(value = "/ext_source",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
@Controller
public class ExtendedSourceTestController implements
        ExtendedSourceSpringService {

    private final Log logger =
            LogFactory.getLog(ExtendedSourceTestController.class);

    @Override
    public void handleDeliveryResponse(@RequestBody(
            required = true) Map<String, ResponseDTO<UUID>> response) {
        logger.info("called handleDeliveryResponse");
    }

    @Override
    public void handleServiceRegistrationResponse(@RequestBody(
            required = true) ResponseDTO<Map<String, ResponseDTO<Void>>> response) {
        logger.info("called handleServiceRegistrationResponse");
    }

    @Override
    public void handleServiceIsAvailableResponse(@RequestBody(
            required = true) ResponseDTO<Boolean> response) {
        logger.info("handleServiceRegistrationResponse");
    }

    @Override
    public void handleGetServiceList(@RequestBody(
            required = true) ResponseDTO<List<ServiceDTO>> response) {
        logger.info("called handleGetServiceList");
    }

    @Override
    public void handleGetSupportedActions(@RequestBody(
            required = true) ResponseDTO<List<String>> response) {
        logger.info("handleServiceRegistrationResponse");
    }

    @Override
    public void handleAddAction(@RequestBody(required = true)
                                ResponseDTO response) {
        logger.info("called handleAddAction");
    }

    @Override
    public <T extends EndpointDescriptor, Y extends ActionDescriptor>
    void handleGetServiceInfo(@RequestBody(required = true)
                              ResponseDTO<FullServiceDTO<T, Y>> response) {
        logger.info("called handleGetServiceInfo");
    }

    @Override
    public void handleResponseFromTarget(@RequestBody(
            required = true) ResponseDTO<ResponseFromTargetDTO> responseDTO) {
        logger.info("called handleResponseFromTarget");
    }
}
