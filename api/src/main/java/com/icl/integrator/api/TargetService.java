package com.icl.integrator.api;

import com.icl.integrator.dto.RequestDataDTO;
import com.icl.integrator.dto.ResponseDTO;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 21.01.14
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
public interface TargetService<T> {

    /**
     * Метод обрабатывает входящие данные от интегратора.
     * ВНИМАНИЕ, временная багофича! Для поддержки фукнциональности пинга,
     * если requestDataDTO.empty(), то следует вернуться из метода.
     * @param requestDataDTO - данные, пришедщие из интегратора
     * @return
     */
    public ResponseDTO<T> handleRequest(RequestDataDTO requestDataDTO);

}
