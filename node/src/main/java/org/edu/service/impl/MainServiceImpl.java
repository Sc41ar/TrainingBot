package org.edu.service.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.dao.AppUserDao;
import org.edu.dao.RawDataDao;
import org.edu.entity.AppUser;
import org.edu.entity.RawData;
import org.edu.service.MainService;
import org.edu.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashSet;

import static org.edu.entity.enums.UserState.*;
import static org.edu.service.enums.ServiceCommands.*;

@Service
@Log4j2
public class MainServiceImpl implements MainService {

    private final RawDataDao rawDataDao;
    private final ProducerService producerService;
    private final AppUserDao appUserDao;
    private final CommandProcessorServiceImpl commandProcessorService;

    public MainServiceImpl(RawDataDao rawDataDao, ProducerService producerService, AppUserDao appUserDao, CommandProcessorServiceImpl commandProcessorService) {
        this.rawDataDao = rawDataDao;
        this.producerService = producerService;
        this.appUserDao = appUserDao;
        this.commandProcessorService = commandProcessorService;
    }

    @Override
    public void proccessTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppuser(update);
        var text = update.getMessage().getText();
        var output = "Hello from Node";

        if (CANCEL.equals(text)) {
            output = cancelProcess(appUser);
        }/* else if (BASIC_STATE.equals(appUser)) {
            output = commandProccessorService.proccessServiceCommand(appUser, text);
        } */else {
            output = commandProcessorService.proccessServiceCommand(appUser, text);
        }

        log.info("NODE: Text message is Received");
        var message = update.getMessage();
        sendAnswer(output, message.getChatId());
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(ADMIN_STATE);
        appUserDao.save(appUser);
        return "Командa отменена";
    }

    private AppUser findOrSaveAppuser(Update update) {
        Message textMessage = update.getMessage();
        User telegramUser = textMessage.getFrom();
        AppUser persistentAppUser = appUserDao.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .state(BASIC_STATE)
                    .build();
            return appUserDao.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder().event(update).build();
        rawDataDao.save(rawData);
    }
}
