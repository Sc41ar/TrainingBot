package org.edu.service.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.service.ConsumerService;
import org.edu.service.ReminderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static org.edu.model.RabbitQueue.SERVICE_QUERY_QUEUE;

@Service
@Log4j2
public class ConsumerServiceImpl implements ConsumerService {

    private final ReminderService reminderService;

    public ConsumerServiceImpl(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @Override
    @RabbitListener(queues = SERVICE_QUERY_QUEUE)
    public void consumeMessage(Long occupationId) {
        log.info(occupationId + "|||||");
        reminderService.doAutoAppointmentString(occupationId);

    }
}
