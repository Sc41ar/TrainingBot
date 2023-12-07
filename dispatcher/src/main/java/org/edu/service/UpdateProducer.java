package org.edu.service;

import org.telegram.telegrambots.meta.api.objects.Update;

//Сервис создания сообщений
public interface UpdateProducer  {
    //Отправка сообщений
    void produce(String rabbitQueue, Update update);
}
