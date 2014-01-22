package com.icl.integrator;

import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.services.PacketProcessor;
import com.icl.integrator.services.PacketProcessorFactory;
import com.icl.integrator.task.retryhandler.DatabaseRetryLimitHandler;
import com.icl.integrator.util.MyObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class IntegratorJmsController implements MessageListener, IntegratorAPI {

    private final static Log logger = LogFactory.getLog(
            DatabaseRetryLimitHandler.class);

    @Autowired
    private MyObjectMapper mapper = new MyObjectMapper();

    @Autowired
    private PacketProcessorFactory processorFactory;

    public void onMessage(final Message message) {
        DeliveryDTO packet;
        if (message instanceof TextMessage) {
            String content;
            try {
                content = ((TextMessage) message).getText();
            } catch (JMSException e) {
                logger.error("Ошибка получения текста сообщения", e);
                return;
            }
            try {
                packet = mapper.readValue(content, DeliveryDTO.class);
            } catch (IOException e) {
                logger.error("Не могу десериализовать сообщение в объект", e);
                return;
            }
        } else if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                packet = (DeliveryDTO) objectMessage.getObject();
            } catch (JMSException e) {
                logger.error("Ошибка получения объекта из сообщения", e);
                return;
            }
        } else {
            logger.error(MessageFormat.format(
                    "Присланный тип сообщения {0} не поддерживается",
                    message.getClass()));
            return;
        }
        deliver(packet);
    }

    @Override
    public Map<String, ResponseDTO<UUID>> deliver(
            DeliveryDTO packet) {
        try {
            PacketProcessor processor = processorFactory.createProcessor();
            return processor.process(packet);
        } catch (Exception ex) {
            logger.error("Ошибка отправки", ex);
            return null;
        }
    }

    @Override
    public Boolean ping(RawDestinationDescriptorDTO responseHandler) {
        //TODO implement
        return null;
    }

    @Override
    public ResponseDTO<Map<String, ResponseDTO<Void>>> registerService(
            TargetRegistrationDTO<?> registrationDTO) {
        //TODO implement
        return null;
    }

    @Override
    public ResponseDTO<Boolean> isAvailable(PingDTO pingDTO) {
        //TODO implement
        return null;
    }

    @Override
    public ResponseDTO<List<ServiceDTO>> getServiceList(
            RawDestinationDescriptorDTO responseHandler) {
        //TODO implement
        return null;
    }

    @Override
    public ResponseDTO<List<String>> getSupportedActions(
            ServiceDTOWithResponseHandler serviceDTO) {
        //TODO implement
        return null;
    }
}