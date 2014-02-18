package com.icl.integrator.services.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.icl.integrator.dto.DeliveryType;
import com.icl.integrator.dto.RequestDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BigBlackBug on 2/12/14.
 */
@Service
public class ValidationService {

	private final JsonSchemaFactory jsonSchemaFactory;

	private Map<DeliveryType, Validator<RequestDataDTO>> validatorMap = new HashMap<DeliveryType, Validator<RequestDataDTO>>();

	@Autowired
	private ObjectMapper mapper;

	public ValidationService() {
		jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(
				ValidationConfiguration.newBuilder().setDefaultVersion(
						SchemaVersion.DRAFTV3).freeze()).freeze();
	}

	@PostConstruct
	private void init() {
		for (DeliveryType deliveryType : DeliveryType.values()) {
			String fileName = deliveryType.name().toLowerCase() + ".json";
			File schemaFile = new File("src/main/resources/validators/" + fileName);
			try {
				JsonSchema jsonSchema =
						jsonSchemaFactory.getJsonSchema(schemaFile.toURI().toString());
				validatorMap.put(deliveryType, new DefaultValidator(jsonSchema));
			} catch (ProcessingException e) {
				throw new ValidatorException(
						"Ошибка инициализации схемы для пакета " + deliveryType, e);
			}
		}
	}

	public void validate(RequestDataDTO requestData) throws PacketValidationException, ValidatorException{
		//TODO remake when I create a single validator file
		validatorMap.get(requestData.getDeliveryType()).validate(requestData);
	}

	private static interface Validator<T> {

		public void validate(T packet) throws PacketValidationException, ValidatorException;
	}

	private class DefaultValidator implements Validator<RequestDataDTO> {

		private final JsonSchema jsonSchema;


		private DefaultValidator(JsonSchema jsonSchema) {
			this.jsonSchema = jsonSchema;
		}

		@Override
		public void validate(RequestDataDTO packet)
				throws PacketValidationException, ValidatorException {
			JsonNode jsonNode;
			try {
				jsonNode = mapper.valueToTree(packet.getData());
			} catch (IllegalArgumentException ex) {
				throw new ValidatorException("Ошибка парсинга пакета", ex);
			}
			ProcessingReport validate;
			try {
				validate = jsonSchema.validate(jsonNode);
			} catch (ProcessingException e) {
				throw new ValidatorException("Ошибка валидации", e);
			}
			if (!validate.isSuccess()) {
				throw new PacketValidationException(validate);
			}
		}
	}

}
