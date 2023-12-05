package org.edu.service.handlers.impl;

import org.edu.dao.AppUserDao;
import org.edu.dao.AuthUserQueueDao;
import org.edu.entity.AppUser;
import org.edu.entity.AuthUserQueue;
import org.edu.entity.enums.BotState;
import org.edu.service.ProducerService;
import org.edu.service.handlers.AuthHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashSet;

@Service
public class AuthHandlerImpl implements AuthHandler {
    private final AppUserDao appUserDao;
    private final ProducerService producerService;
    private final AuthUserQueueDao authUserQueueDao;

    public AuthHandlerImpl(AppUserDao appUserDao, ProducerService producerService, AuthUserQueueDao authUserQueueDao) {
        this.appUserDao = appUserDao;
        this.producerService = producerService;
        this.authUserQueueDao = authUserQueueDao;
    }

    @Override
    public String processAuth(AppUser appUser) {
        long adminId = 1;
        long managerId = 5;
        var admin = appUserDao.findById(adminId);
        var manager = appUserDao.findById(managerId);
        sendAnswer("Пользователь " + appUser.getFirstName() + " @" + appUser.getUsername() + " ожидает потверждения", admin.get().getTelegramUserId());
        sendAnswer("Пользователь " + appUser.getFirstName() + " @" + appUser.getUsername() + " ожидает потверждения", manager.get().getTelegramUserId());
        admin.get().setBotState(BotState.AUTHENTICATION);
        manager.get().setBotState(BotState.AUTHENTICATION);
        appUserDao.saveAndFlush(admin.get());
        appUserDao.saveAndFlush(manager.get());
        AuthUserQueue authUserQueue = AuthUserQueue.builder().appUser(appUser).build();
        authUserQueueDao.save(authUserQueue);
        return "Запрос на подтверждение личности отправлен";
    }

    @Override
    public String showUnverifiedUsersList() {
        StringBuilder answer = new StringBuilder("");
        var list = authUserQueueDao.findAll();
        int i  = 1;
        for (var item : list) {
            answer.append("\n" + i + ". " + item.getAppUser().getFirstName()+ " @"+item.getAppUser().getUsername());
            i++;
        }
        return answer.toString();
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }
    private void returnBasicState(AppUser appUser){
        appUser.setBotState(BotState.BASIC);
        appUserDao.saveAndFlush(appUser);
    }

}
