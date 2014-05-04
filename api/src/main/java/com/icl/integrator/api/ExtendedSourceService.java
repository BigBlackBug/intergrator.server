package com.icl.integrator.api;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.ActionRegistrationResultDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 24.01.14
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
public interface ExtendedSourceService extends SourceService {

	public void handleDeliveryResponse(Map<String, ResponseDTO<UUID>> response);

	public void handleServiceRegistrationResponse(
			ResponseDTO<List<ActionRegistrationResultDTO>> response);

	public void handleServiceIsAvailableResponse(ResponseDTO<Boolean> response);

	public void handleGetServiceList(ResponseDTO<List<ServiceDTO>> response);

	public void handleGetSupportedActions(ResponseDTO<List<String>> response);

	public void handleAddAction(ResponseDTO<Void> response);

	public void handleGetServiceInfo(ResponseDTO<FullServiceDTO<ActionDescriptor>> response);

	public void handleAutoDetectionRegistration(ResponseDTO<List<ResponseDTO<Void>>> response);

	public void handleGetActionsForDelivery(ResponseDTO<List<DeliveryActionsDTO>> response);

	public void handleGetServicesSupportingActionType(ResponseDTO<List<ServiceAndActions<ActionDescriptor>>> response);

	public void handleFetchUpdates(ResponseDTO<List<Modification>> response);

}
