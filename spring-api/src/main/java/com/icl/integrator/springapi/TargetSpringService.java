package com.icl.integrator.springapi;

import com.icl.integrator.api.TargetService;
import com.icl.integrator.dto.RequestDataDTO;
import com.icl.integrator.dto.ResponseDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 21.01.14
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
public interface TargetSpringService<T> extends TargetService<T> {

	@Override
	@RequestMapping(value = "/handleRequest",
	                method = {RequestMethod.POST, RequestMethod.HEAD})
	public
	@ResponseBody
	ResponseDTO<T>
	handleRequest(@RequestBody(required = false) RequestDataDTO requestDataDTO);
}
