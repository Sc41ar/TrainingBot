package org.edu.service.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.controller.UpdateController;
import org.edu.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.edu.model.RabbitQueue.ANSWER_MESSAGE;

@Service
@Log4j2
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage) {
        if (sendMessage != null && sendMessage.getText() != null) {
            updateController.setView(sendMessage);
        }else {
            log.error("Нулевое сообщение");
        }
    }
}
