package org.edu.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    //отправка в очередь ответа в телеграм
    void produceAnswer(SendMessage sendMessage);

    //отправка в сервис reminder
    void produceMessage(Long occupationId);
}
