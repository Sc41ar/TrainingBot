package org.edu.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.edu.service.ProducerService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.edu.model.RabbitQueue.ANSWER_MESSAGE;
import static org.edu.model.RabbitQueue.SERVICE_QUERY_QUEUE;

@Service
@Log4j2
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    Jackson2ObjectMapperBuilder mapperBuilder;

    public ProducerServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produceAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    @Override
    public void produceMessage(Long occupationId) {
        try {
            rabbitTemplate.convertAndSend(SERVICE_QUERY_QUEUE, occupationId);
        } catch (Exception e) {
            log.error(e.getMessage() + e.getCause());
        }

    }

}
