package com.icl.integrator.dto;

import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 22.01.14
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
public class ResponseFromIntegratorDTO {

    private Map<String, ResponseDTO<UUID>> serviceToUUIDMap;

    public ResponseFromIntegratorDTO() {

    }

    public ResponseFromIntegratorDTO(
            Map<String, ResponseDTO<UUID>> serviceToUUIDMap) {
        this.serviceToUUIDMap = serviceToUUIDMap;
    }

    public Map<String, ResponseDTO<UUID>> getServiceToUUIDMap() {
        return serviceToUUIDMap;
    }

    public void setServiceToUUIDMap(
            Map<String, ResponseDTO<UUID>> serviceToUUIDMap) {
        this.serviceToUUIDMap = serviceToUUIDMap;
    }
}
