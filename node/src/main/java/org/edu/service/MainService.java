package org.edu.service;

import org.edu.dao.RawDataDao;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void proccessTextMessage(Update update);
}
