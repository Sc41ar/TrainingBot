package org.edu.service;

import org.telegram.telegrambots.meta.api.objects.Update;

//класс необходимый для считываний сообщений с очередей брокера
public interface ConsumerService {
    void consumeTextMessageUpdate(Update update);
    void consumeDocMessageUpdate(Update update);
    void consumePhotoMessageUpdate(Update update);
}
