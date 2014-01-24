package com.icl.integrator.springapi;

import com.icl.integrator.api.ExtendedSourceService;
import com.icl.integrator.dto.FullServiceDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.ServiceDTO;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.source.EndpointDescriptor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 24.01.14
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 */
public interface ExtendedSourceSpringService extends ExtendedSourceService {

    @Override
    @RequestMapping(value = "/handleDeliveryResponse",
                    method = RequestMethod.POST)
    public void handleDeliveryResponse(@RequestBody(required = true)
                                       Map<String, ResponseDTO<UUID>> response);

    @Override
    @RequestMapping(value = "/handleServiceRegistrationResponse",
                    method = RequestMethod.POST)
    public void handleServiceRegistrationResponse(
            @RequestBody(required = true)
            ResponseDTO<Map<String, ResponseDTO<Void>>> response);

    @Override
    @RequestMapping(value = "/handleServiceIsAvailableResponse",
                    method = RequestMethod.POST)
    public void handleServiceIsAvailableResponse(@RequestBody(required = true)
                                                 ResponseDTO<Boolean> response);

    @Override
    @RequestMapping(value = "/handleGetServiceList", method = RequestMethod
            .POST)
    public void handleGetServiceList(@RequestBody(required = true)
                                     ResponseDTO<List<ServiceDTO>> response);

    @Override
    @RequestMapping(value = "/handleGetSupportedActions",
                    method = RequestMethod.POST)
    public void handleGetSupportedActions(@RequestBody(required = true)
                                          ResponseDTO<List<String>> response);

    @Override
    @RequestMapping(value = "/handleAddAction", method = RequestMethod.POST)
    public void handleAddAction(@RequestBody(required = true) ResponseDTO
                                        response);

    @Override
    @RequestMapping(value = "/handleGetServiceInfo",
                    method = RequestMethod.POST)
    public <T extends EndpointDescriptor, Y extends ActionDescriptor>
    void handleGetServiceInfo(@RequestBody(required = true)
                              ResponseDTO<FullServiceDTO<T, Y>> response);

    @Override
    @RequestMapping(value = "/handleResponseFromTarget",
                    method = RequestMethod.POST)
    public void handleResponseFromTarget(@RequestBody(required = true)
                                         ResponseFromTargetDTO responseDTO);
}
