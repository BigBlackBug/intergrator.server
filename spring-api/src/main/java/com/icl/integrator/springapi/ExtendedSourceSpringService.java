package com.icl.integrator.springapi;

import com.icl.integrator.api.ExtendedSourceService;
import com.icl.integrator.dto.FullServiceDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.ServiceDTO;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.RegistrationResultDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	                consumes = MediaType.APPLICATION_JSON_VALUE,
	                method = {RequestMethod.POST, RequestMethod.HEAD})
	public
	@ResponseBody
	void handleDeliveryResponse(@RequestBody
	                            Map<String, ResponseDTO<UUID>> response);

	@Override
	@RequestMapping(value = "/handleServiceRegistrationResponse",
	                consumes = MediaType.APPLICATION_JSON_VALUE,
	                method = {RequestMethod.POST, RequestMethod.HEAD})
	public
	@ResponseBody
	void handleServiceRegistrationResponse(
			@RequestBody
			ResponseDTO<RegistrationResultDTO> response);

	@Override
	@RequestMapping(value = "/handleServiceIsAvailableResponse",
	                consumes = MediaType.APPLICATION_JSON_VALUE,
	                method = {RequestMethod.POST, RequestMethod.HEAD})
	public
	@ResponseBody
	void handleServiceIsAvailableResponse(@RequestBody
	                                      ResponseDTO<Boolean> response);

	@Override
	@RequestMapping(value = "/handleGetServiceList",
	                consumes = MediaType.APPLICATION_JSON_VALUE,
	                method = RequestMethod.POST)
	public
	@ResponseBody
	void handleGetServiceList(@RequestBody
	                          ResponseDTO<List<ServiceDTO>> response);

	@Override
	@RequestMapping(value = "/handleGetSupportedActions",
	                consumes = MediaType.APPLICATION_JSON_VALUE,
	                method = {RequestMethod.POST, RequestMethod.HEAD})
	public
	@ResponseBody
	void handleGetSupportedActions(@RequestBody
	                               ResponseDTO<List<String>> response);

	@Override
	@RequestMapping(value = "/handleAddAction",
	                consumes = MediaType.APPLICATION_JSON_VALUE,
	                method = {RequestMethod.POST, RequestMethod.HEAD})
	public
	@ResponseBody
	void handleAddAction(@RequestBody ResponseDTO
			                     response);

	@Override
	@RequestMapping(value = "/handleGetServiceInfo",
	                consumes = MediaType.APPLICATION_JSON_VALUE,
	                method = {RequestMethod.POST, RequestMethod.HEAD})
	public
	@ResponseBody
	void handleGetServiceInfo(@RequestBody
	                          ResponseDTO<FullServiceDTO<ActionDescriptor>> response);

	@Override
	@RequestMapping(value = "/handleResponseFromTarget",
	                consumes = MediaType.APPLICATION_JSON_VALUE,
	                method = {RequestMethod.POST, RequestMethod.HEAD})
	public
	@ResponseBody
	void handleResponseFromTarget(@RequestBody
	                              ResponseDTO<ResponseFromTargetDTO> responseDTO);
}
