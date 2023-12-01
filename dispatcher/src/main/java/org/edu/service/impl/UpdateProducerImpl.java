package org.edu.service.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.service.UpdateProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

//производитель сообщений
@Service
@Log4j2
public class UpdateProducerImpl implements UpdateProducer {
    //логгирование на твой вкус TODO : интеграция с RAbbitMQ
    private final RabbitTemplate rabbitTemplate;

    public UpdateProducerImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produce(String rabbitQueue, Update update) {
        log.info(update.getMessage().getText()+" \\ ");
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }

}
