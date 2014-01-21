package com.icl.integrator.api;

import com.icl.integrator.dto.PingDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.SourceDataDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 20.01.14
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */
public interface IntegratorAPI {

    public void deliver(SourceDataDTO packet);

    public Boolean ping();

    public ResponseFromTargetDTO<Map> registerService(
            TargetRegistrationDTO registrationDTO);

    public ResponseFromTargetDTO<Boolean> isAvailable(PingDTO pingDTO);

//    public List<String> getServiceList();
//
//    public void getSupportedActions()  ;
}
