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
import org.springframework.core.io.DefaultResourceLoader;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BigBlackBug on 2/12/14.
 */
public class ValidationService {

	private final JsonSchemaFactory jsonSchemaFactory;

	private Map<DeliveryType, Validator> validatorMap = new HashMap<>();

	private JsonValidator integratorValidator;


	@Autowired
	private ObjectMapper mapper;

	public ValidationService() {
		jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(
				ValidationConfiguration.newBuilder().setDefaultVersion(
						SchemaVersion.DRAFTV4).freeze()).freeze();
	}

	@PostConstruct
	private void initValidators() {
		DefaultResourceLoader loader = new DefaultResourceLoader();
		File schemaFile = null;
		try {
			schemaFile = loader.getResource("classpath:/validators/IntegratorPacketSchema.json")
					.getFile();
			JsonSchema jsonSchema =
					jsonSchemaFactory.getJsonSchema(schemaFile.toURI().toString());
			integratorValidator = new JsonValidator(jsonSchema);
			for (DeliveryType deliveryType : DeliveryType.values()) {
				if (deliveryType != DeliveryType.UNDEFINED) {
					String fileName = deliveryType.name().toLowerCase() + ".json";
					schemaFile = loader.getResource("classpath:/validators/" + fileName).getFile();
					jsonSchema = jsonSchemaFactory.getJsonSchema(schemaFile.toURI().toString());
					validatorMap.put(deliveryType, new PacketValidator(jsonSchema));
				}
			}
		} catch (Exception e) {
			throw new ValidatorException(
					"Ошибка инициализации схемы из файла " + String.valueOf(schemaFile), e);
		}
	}

	public void validateIntegratorPacket(String integratorPacket)
			throws PacketValidationException, ValidatorException {
		integratorValidator.validate(integratorPacket);
	}

	@SuppressWarnings("unchecked")
	public void validate(RequestDataDTO requestData)
			throws PacketValidationException, ValidatorException {
		validatorMap.get(requestData.getDeliveryType()).validate(requestData.getData());
	}

	private static interface Validator<T> {

		public void validate(T packet) throws PacketValidationException, ValidatorException;
	}

	private class JsonValidator extends DefaultValidator<String> {

		private JsonValidator(JsonSchema jsonSchema) {
			super(jsonSchema);
		}

		@Override
		protected JsonNode getNode(String packet) throws IOException {
			return mapper.readTree(packet);
		}
	}

	private class PacketValidator<T> extends DefaultValidator<T> {

		private PacketValidator(JsonSchema jsonSchema) {
			super(jsonSchema);
		}

		@Override
		protected JsonNode getNode(T packet) throws IOException {
			try {
				return mapper.valueToTree(packet);
			} catch (IllegalArgumentException ex) {
				throw new ValidatorException("Ошибка парсинга пакета", ex);
			}
		}
	}

	private abstract class DefaultValidator<T> implements Validator<T> {

		private final JsonSchema jsonSchema;


		private DefaultValidator(JsonSchema jsonSchema) {
			this.jsonSchema = jsonSchema;
		}

		@Override
		public void validate(T packet)
				throws PacketValidationException, ValidatorException {
			JsonNode jsonNode;
			ProcessingReport validate;
			try {
				jsonNode = getNode(packet);
				validate = jsonSchema.validate(jsonNode);
			} catch (ProcessingException | IOException e) {
				throw new ValidatorException("Ошибка валидации", e);
			}
			if (!validate.isSuccess()) {
				throw new PacketValidationException(validate);
			}
		}

		protected abstract JsonNode getNode(T packet) throws IOException;
	}

}
