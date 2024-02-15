package org.edu.service;

import org.edu.entity.AppUser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

//сервис для выбора нужного обработчика
public interface CommandProcessorService {
    //обработка команды из строки
    public SendMessage processServiceCommand(AppUser appUser, String cmd);
    //обработа обратного вызова
    public SendMessage processCallBackQuery(CallbackQuery query, AppUser appUser);

}
