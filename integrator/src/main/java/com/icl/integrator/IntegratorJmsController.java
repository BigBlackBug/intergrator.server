package com.icl.integrator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.DeliveryDTO;
import com.icl.integrator.dto.IntegratorPacket;
import com.icl.integrator.dto.PingDTO;
import com.icl.integrator.dto.ServiceDTO;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.AddActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.services.IntegratorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IntegratorService integratorService;

    @Override
    public void onMessage(final Message message) {
        IntegratorPacket<?> integratorPacket;
        if (message instanceof TextMessage) {
            String content;
            try {
                content = ((TextMessage) message).getText();
            } catch (JMSException e) {
                logger.error("Ошибка получения текста сообщения", e);
                return;
            }
            try {
                integratorPacket =
                        mapper.readValue(content, IntegratorPacket.class);
            } catch (IOException e) {
                logger.error("Не могу десериализовать сообщение в объект", e);
                return;
            }
            switch (integratorPacket.getMethod()) {
                case ADD_ACTION: {
                    IntegratorPacket<AddActionDTO> packet = mapper.convertValue(
                            integratorPacket,
                            new TypeReference<IntegratorPacket<AddActionDTO>>() {
                            });
                    integratorService.addAction(packet);
                    break;
                }
                case DELIVER: {
                    IntegratorPacket<DeliveryDTO> packet = mapper.convertValue(
                            integratorPacket,
                            new TypeReference<IntegratorPacket<DeliveryDTO>>() {
                            });
                    integratorService.deliver(packet);
                    break;
                }
                case GET_SERVICE_INFO: {
                    IntegratorPacket<ServiceDTO> packet = mapper.convertValue(
                            integratorPacket,
                            new TypeReference<IntegratorPacket<ServiceDTO>>() {
                            });
                    integratorService.getServiceInfo(packet);
                    break;
                }
                case GET_SERVICE_LIST: {
                    IntegratorPacket<Void> packet = mapper.convertValue(
                            integratorPacket,
                            new TypeReference<IntegratorPacket<Void>>() {
                            });
                    integratorService.getServiceList(packet);
                    break;
                }
                case GET_SUPPORTED_ACTIONS: {
                    IntegratorPacket<ServiceDTO> packet = mapper.convertValue(
                            integratorPacket,
                            new TypeReference<IntegratorPacket<ServiceDTO>>() {
                            });
                    integratorService.getSupportedActions(packet);
                    break;
                }
                case IS_AVAILABLE: {
                    IntegratorPacket<PingDTO> packet = mapper.convertValue(
                            integratorPacket,
                            new TypeReference<IntegratorPacket<PingDTO>>() {
                            });
                    integratorService.isAvailable(packet);
                    break;
                }
                case PING: {
                    IntegratorPacket<Void> packet = mapper.convertValue(
                            integratorPacket,
                            new TypeReference<IntegratorPacket<Void>>() {
                            });
                    integratorService.ping(packet);
                    break;
                }
                case REGISTER_SERVICE: {
                    IntegratorPacket<TargetRegistrationDTO<ActionDescriptor>>
                            packet
                            = mapper.convertValue(
                            integratorPacket,
                            new TypeReference<IntegratorPacket<TargetRegistrationDTO<ActionDescriptor>>>() {
                            });
                    integratorService.registerService(packet);
                    break;
                }
            }
        } else {
            logger.error(MessageFormat.format(
                    "Присланный тип сообщения {0} не поддерживается",
                    message.getClass()));
            return;
        }
    }

}