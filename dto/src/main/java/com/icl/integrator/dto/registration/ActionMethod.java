package com.icl.integrator.dto.registration;

public enum ActionMethod {
	HANDLE_DELIVERY,
	HANDLE_RESPONSE_FROM_TARGET,
	HANDLE_DELIVERY_RESPONSE,
	HANDLE_SERVER_REGISTRATION_RESPONSE,
	HANDLE_SERVICE_IS_AVAILABLE,
	HANDLE_GET_SERVER_LIST,
    HANDLE_GET_SUPPORTED_ACTIONS,
	HANDLE_ADD_ACTION,
	HANDLE_GET_SERVICE_INFO,
	HANDLE_PING,
	HANDLE_AUTO_DETECTION_REGISTRATION_RESPONSE,
	HANDLE_GET_ACTIONS_FOR_DELIVERY,
	HANDLE_GET_SERVICES_SUPPORTING_ACTION_TYPE,
	HANDLE_FETCH_UPDATES,
	HANDLE_REMOVE_SERVICE
}