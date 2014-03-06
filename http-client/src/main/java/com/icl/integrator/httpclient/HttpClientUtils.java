package com.icl.integrator.httpclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.DeliveryType;
import com.icl.integrator.dto.RequestDataDTO;

import java.util.Map;

/**
 * Created by BigBlackBug on 3/6/14.
 */
public class HttpClientUtils {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static <T> RequestDataDTO createRequestData(DeliveryType deliveryType, T data) {
		Map<String, Object> dataMap =
				OBJECT_MAPPER.convertValue(data, new TypeReference<Map<String, Object>>() {
				});
		return new RequestDataDTO(deliveryType, dataMap);
	}

}
