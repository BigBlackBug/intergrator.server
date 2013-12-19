package com.icl.integrator.util.connectors.jms;

import com.icl.integrator.dto.SourceDataDTO;
import com.icl.integrator.services.PacketProcessor;
import com.icl.integrator.util.MyObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.jms.*;
import java.io.IOException;

@Controller
public abstract class QueueListener implements MessageListener {

    @Autowired
    private MyObjectMapper mapper = new MyObjectMapper();

    public void onMessage(final Message message) {
        SourceDataDTO packet = null;
        if (message instanceof TextMessage) {
            String content;
            try {
                content = ((TextMessage) message).getText();
            } catch (JMSException e) {
                //TODO
                return;
            }
            try {
                packet = mapper.readValue(content, SourceDataDTO.class);
            } catch (IOException e) {
                //TODO
                return;
            }
        } else if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                packet = (SourceDataDTO) objectMessage.getObject();
            } catch (JMSException e) {
                //TODO
            }
        } else {
            //TODO unsupported message
        }
        PacketProcessor processor = createProcessor();
//        Map<String, String> serviceToReqID =
        processor.process(packet);
        //TODO ask/handle
//        ResponseFromTargetDTO<Map> fromTargetDTO =
//                new ResponseFromTargetDTO<>(serviceToReqID, Map.class);
//        ResponseToSourceDTO responseToSourceDTO =
//                new ResponseToSourceDTO(fromTargetDTO);
        //            return responseToSourceDTO;
//            final TextMessage textMessage = (TextMessage) message;
//            try {
//                System.out.println("Пришло сообщение на КАГБЭ " +
//                                           "соурс" + textMessage
//                        .getText());
//            } catch (final JMSException e) {
//                e.printStackTrace();
//            }
    }

    protected abstract PacketProcessor createProcessor();
}