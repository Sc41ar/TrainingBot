package org.edu.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.edu.model.RabbitQueue.*;
//создание бинов с очередями + конвертер сообщений
@Configuration
public class RabbitConfiguration {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue() {
        return new Queue(TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public Queue serviceQueryQueue() {
        return new Queue(SERVICE_QUERY_QUEUE);
    }

    @Bean
    public Queue serviceResponseQueue() {
        return new Queue(SERVICE_RESPONSE_QUEUE);
    }

    @Bean
    public Queue answerMessageQueue() {
        return new Queue(ANSWER_MESSAGE);
    }
}
