package org.edu.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ProducerService {
    void produceAnswer(SendMessage sendMessage);
}
