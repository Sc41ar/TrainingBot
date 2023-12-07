package org.edu.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

//Сервис для работы с очередями RabbitMq
public interface AnswerConsumer {
    //Получение сообщения
    void consume(SendMessage sendMessage);
}
