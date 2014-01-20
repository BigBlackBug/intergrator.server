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
public interface CleanAPI {

    public void process(SourceDataDTO packet);

    public Map<String, String> ping();

    public ResponseFromTargetDTO<Map> registerTarget(
            TargetRegistrationDTO registrationDTO);

    public ResponseFromTargetDTO<Boolean> isAvailable(PingDTO pingDTO);
}
