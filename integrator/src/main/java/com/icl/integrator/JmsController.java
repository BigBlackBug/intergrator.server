package com.icl.integrator;

import com.icl.integrator.dto.SourceDataDTO;
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

@Component
public class JmsController implements MessageListener {

    private final static Log logger = LogFactory.getLog(
            DatabaseRetryLimitHandler.class);

    @Autowired
    private MyObjectMapper mapper = new MyObjectMapper();

    @Autowired
    private PacketProcessorFactory processorFactory;

    public void onMessage(final Message message) {
        SourceDataDTO packet;
        if (message instanceof TextMessage) {
            String content;
            try {
                content = ((TextMessage) message).getText();
            } catch (JMSException e) {
                logger.error("Ошибка получения текста сообщения", e);
                return;
            }
            try {
                packet = mapper.readValue(content, SourceDataDTO.class);
            } catch (IOException e) {
                logger.error("Не могу десериализовать сообщение в объект", e);
                return;
            }
        } else if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                packet = (SourceDataDTO) objectMessage.getObject();
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
        try {
            PacketProcessor processor = processorFactory.createProcessor();
            processor.process(packet);
            //TODO если есть сорс конечно надо отправлять обратно ему
            // результаты process
        } catch (Exception ex) {
            logger.error("Ошибка отправки", ex);
        }
    }

}