package org.edu.service.handlers.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.dao.AppUserDao;
import org.edu.dao.AuthUserQueueDao;
import org.edu.entity.AppUser;
import org.edu.entity.AuthUserQueue;
import org.edu.entity.enums.BotState;
import org.edu.entity.enums.UserState;
import org.edu.service.ProducerService;
import org.edu.service.handlers.AuthHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static org.edu.specifications.AuthUserQueueSpecifications.hasUser;

@Service
@Log4j2
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
    public String showUnverifiedUsersList(AppUser appUser) {
        var rightsCheck = appUser.getState() == UserState.ADMIN_STATE;
        if (!rightsCheck) return "Недостаточно прав";

        StringBuilder answer = new StringBuilder("");
        var list = authUserQueueDao.findAll();
        int i = 1;
        for (var item : list) {
            answer.append("\n" + i + ". " + item.getAppUser().getFirstName() + " @" + item.getAppUser().getUsername());
            i++;
        }
        return answer.toString();
    }

    @Override
    public String getUsernameFromCallBackQueryMessage(String message) {
        String answer = "";
        var indexOfSobaka = message.indexOf('@');
        var indexOfSpace = message.indexOf(' ', indexOfSobaka);

        if (indexOfSobaka < 0)
            return "Неверный ввод пользователя\nФормат ввода: @username";

        if (indexOfSpace < 0) {
            indexOfSpace = message.length();
        }

        answer = message.substring(indexOfSobaka + 1, indexOfSpace);
        //answer += "\n\nВведите имя профиля тг";
        return answer;
    }

    @Override
    public void setUserTeacherState(String username) {
        var optionalUser = appUserDao.findByUsername(username);
        try {
            var appUser = optionalUser.get();
            appUser.setState(UserState.TEACHER_STATE);
            appUserDao.saveAndFlush(appUser);
            var listAuthUser = authUserQueueDao.findAll(hasUser(appUser));
            authUserQueueDao.deleteAll(listAuthUser);
        } catch (Exception e) {
            log.error("Ошибка" + e.getMessage() + " -> " + e.getCause());
        }
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), output);
        InlineKeyboardMarkup markup = makeMarkup();
        sendMessage.setReplyMarkup(markup);
        producerService.produceAnswer(sendMessage);
    }

    public void returnBasicState(AppUser appUser) {
        appUser.setBotState(BotState.BASIC);
        appUserDao.saveAndFlush(appUser);
    }

    private InlineKeyboardMarkup makeMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonYes = new InlineKeyboardButton();
        InlineKeyboardButton buttonNo = new InlineKeyboardButton();

        buttonYes.setText("Подтвердить");
        buttonNo.setText("Отклонить");
        buttonYes.setCallbackData("Подтвердить");
        buttonNo.setCallbackData("Отклонить");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(buttonYes);
        row.add(buttonNo);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row);

        markup.setKeyboard(rowList);
        return markup;
    }

}
