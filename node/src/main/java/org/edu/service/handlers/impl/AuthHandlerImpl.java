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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

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
        SendMessage message = new SendMessage();
        message.setText("Пользователь " + appUser.getFirstName() + " @" + appUser.getUsername() + " ожидает потверждения");

        InlineKeyboardMarkup markup = makeMarkup();
        message.setReplyMarkup(markup);
        message.setChatId(admin.get().getTelegramUserId());

        sendAnswer(message);

        admin.get().setBotState(BotState.AUTHENTICATION);
        manager.get().setBotState(BotState.AUTHENTICATION);
        appUserDao.saveAndFlush(admin.get());
        appUserDao.saveAndFlush(manager.get());
        AuthUserQueue authUserQueue = AuthUserQueue.builder().appUser(appUser).build();
        authUserQueueDao.save(authUserQueue);
        return "Запрос на подтверждение личности отправлен";
    }

    private InlineKeyboardMarkup makeMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonYes = new InlineKeyboardButton();
        InlineKeyboardButton buttonNo = new InlineKeyboardButton();

        buttonYes.setText("Подтвердить");
        buttonNo.setText("Отклонить");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(buttonYes);
        row.add(buttonNo);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row);

        markup.setKeyboard(rowList);
        return markup;
    }

    @Override
    public String showUnverifiedUsersList() {
        StringBuilder answer = new StringBuilder("");
        var list = authUserQueueDao.findAll();
        int i = 1;
        for (var item : list) {
            answer.append("\n" + i + ". " + item.getAppUser().getFirstName() + " @" + item.getAppUser().getUsername());
            i++;
        }
        return answer.toString();
    }

    private void sendAnswer(SendMessage message) {

        producerService.produceAnswer(message);
    }

    private void returnBasicState(AppUser appUser) {
        appUser.setBotState(BotState.BASIC);
        appUserDao.saveAndFlush(appUser);
    }

}
