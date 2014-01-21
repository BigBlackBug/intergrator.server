package com.icl.integrator.api;

import com.icl.integrator.dto.PingDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.SourceDataDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 20.01.14
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
@RequestMapping(value = "/integrator/",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
public interface IntegratorHttpAPI extends IntegratorAPI {

    @Override
    @RequestMapping(value = "deliver", method = RequestMethod.POST)
    public void deliver(@RequestBody(required = true) SourceDataDTO packet);

    @Override
    @RequestMapping(value = "ping", method = RequestMethod.GET)
    public
    @ResponseBody
    Boolean ping();

    @Override
    @RequestMapping(value = "registerService", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseFromTargetDTO<Map> registerService(@RequestBody(required = true)
                                               TargetRegistrationDTO registrationDTO);

    @Override
    @RequestMapping(value = "checkAvailability", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseFromTargetDTO<Boolean> isAvailable(@RequestBody(required = true)
                                               PingDTO pingDTO);
}
