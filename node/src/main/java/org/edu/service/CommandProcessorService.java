package org.edu.service;

import org.edu.entity.AppUser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CommandProcessorService {
    public SendMessage processServiceCommand(AppUser appUser, String cmd);

    public SendMessage processCallBackQuery(CallbackQuery query, AppUser appUser);
}
