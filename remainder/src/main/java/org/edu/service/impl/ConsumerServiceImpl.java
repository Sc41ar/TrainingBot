package org.edu.service.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.service.ConsumerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.edu.model.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Service
@Log4j2
public class ConsumerServiceImpl implements ConsumerService {

    @Override
//    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdate(Update update) {
        log.info(update.getMessage().getText() + "|||||");
    }
}
