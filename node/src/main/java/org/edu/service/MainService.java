package org.edu.service;

import org.edu.dao.RawDataDao;
import org.telegram.telegrambots.meta.api.objects.Update;
//главный сервис-контроллер
public interface MainService {
    void proccessTextMessage(Update update);
}
