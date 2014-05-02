package com.icl.integrator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.DeliveryDTO;
import com.icl.integrator.dto.IntegratorMethod;
import com.icl.integrator.dto.IntegratorPacket;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.AddActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.services.validation.PacketValidationException;
import com.icl.integrator.services.validation.ValidationService;
import com.icl.integrator.services.validation.ValidatorException;
import com.icl.integrator.util.IntegratorException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.IOException;
import java.text.MessageFormat;

@Component
public class IntegratorJmsController implements MessageListener {

	private final static Log logger = LogFactory.getLog(
			IntegratorJmsController.class);

	@Autowired
	private ObjectMapper mapper;

	@Qualifier("integratorService")
	@Autowired
	private IntegratorAPI integratorService;

	@Autowired
	private ValidationService validationService;

	@Override
	public void onMessage(final Message message) {
		IntegratorPacket<?, ?> integratorPacket;
		try {
			if (message instanceof TextMessage) {
				String content;
				try {
					content = ((TextMessage) message).getText();
				} catch (JMSException e) {
					throw new IntegratorException("Ошибка получения текста сообщения", e);
				}
				try {
					integratorPacket =
							mapper.readValue(content, IntegratorPacket.class);
				} catch (IOException e) {
					throw new IntegratorException("Не могу десериализовать сообщение в объект", e);
				}
				try {
					validationService.validateIntegratorPacket(content);
				} catch (PacketValidationException | ValidatorException ex) {
					throw new IntegratorException("Ошибка валидации", ex);
				}
				IntegratorMethod method = integratorPacket.getMethod();
				if (method == null) {
					//TODO костыыыль
					throw new IntegratorException("Поле method у IntegratorPacket не заполнено",
					                              new NullPointerException());
				}
				switch (method) {
					case ADD_ACTION: {
						IntegratorPacket<AddActionDTO<ActionDescriptor>, DestinationDescriptor
								> packet = mapper.convertValue(
								integratorPacket,
								new TypeReference<IntegratorPacket<AddActionDTO<ActionDescriptor>, DestinationDescriptor>>() {
								});
						integratorService.addAction(packet);
						break;
					}
					case DELIVER: {
						IntegratorPacket<DeliveryDTO, DestinationDescriptor>
								packet = mapper.convertValue(
								integratorPacket,
								new TypeReference<IntegratorPacket<DeliveryDTO, DestinationDescriptor>>() {
								});
						integratorService.deliver(packet);
						break;
					}
					case GET_SERVICE_INFO: {
						IntegratorPacket<String, DestinationDescriptor> packet =
								mapper.convertValue(
										integratorPacket,
										new TypeReference<IntegratorPacket<String, DestinationDescriptor>>() {
										});
						integratorService.getServiceInfo(packet);
						break;
					}
					case GET_SERVICE_LIST: {
						IntegratorPacket<Void, DestinationDescriptor> packet =
								mapper.convertValue(
										integratorPacket,
										new TypeReference<IntegratorPacket<Void, DestinationDescriptor>>() {
										});
						integratorService.getServiceList(packet);
						break;
					}
					case GET_SUPPORTED_ACTIONS: {
						IntegratorPacket<String, DestinationDescriptor> packet =
								mapper.convertValue(
										integratorPacket,
										new TypeReference<IntegratorPacket<String, DestinationDescriptor>>() {
										});
						integratorService.getSupportedActions(packet);
						break;
					}
					case IS_AVAILABLE: {
						IntegratorPacket<ServiceDestinationDescriptor, DestinationDescriptor>
								packet =
								mapper.convertValue(
										integratorPacket,
										new TypeReference<IntegratorPacket<ServiceDestinationDescriptor, DestinationDescriptor>>() {
										});
						integratorService.isAvailable(packet);
						break;
					}
					case PING: {
						IntegratorPacket<Void, DestinationDescriptor> packet =
								mapper.convertValue(
										integratorPacket,
										new TypeReference<IntegratorPacket<Void, DestinationDescriptor>>() {
										});
						integratorService.ping(packet);
						break;
					}
					case REGISTER_SERVICE: {
						IntegratorPacket<TargetRegistrationDTO<ActionDescriptor>, DestinationDescriptor>
								packet
								= mapper.convertValue(
								integratorPacket,
								new TypeReference<IntegratorPacket<TargetRegistrationDTO<ActionDescriptor>, DestinationDescriptor>>() {
								});
						integratorService.registerService(packet);
						break;
					}
				}
			} else {
				throw new IntegratorException(MessageFormat.format(
						"Присланный тип сообщения {0} не поддерживается",
						message.getClass()));
			}
		} catch (IntegratorException ex) {
			String errorMessage = ex.getMessage();
			logger.error(errorMessage, ex.getCause());
		}
	}

}