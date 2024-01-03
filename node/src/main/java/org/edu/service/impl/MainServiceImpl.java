package org.edu.service.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.dao.AppUserDao;
import org.edu.dao.RawDataDao;
import org.edu.entity.AppUser;
import org.edu.entity.RawData;
import org.edu.entity.enums.BotState;
import org.edu.service.MainService;
import org.edu.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.edu.entity.enums.UserState.ADMIN_STATE;
import static org.edu.entity.enums.UserState.BASIC_STATE;
import static org.edu.service.enums.ServiceCommands.CANCEL;

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
        AppUser appUser;
        if (update.hasCallbackQuery()) {
            appUser = appUserDao.findAppUserByTelegramUserId(update.getCallbackQuery().getFrom().getId());
            sendAnswer(commandProcessorService.processCallBackQuery(update.getCallbackQuery(), appUser));
        } else if (update.getMessage().hasVideoNote()) {
            appUser = findOrSaveAppuser(update.getMessage());
            sendAnswer(new SendMessage( appUser.getTelegramUserId().toString(), commandProcessorService.processVideoNote()));

        } else {
            appUser = findOrSaveAppuser(update.getMessage());
            var text = update.getMessage().getText();

            if (CANCEL.equals(text)) {
                //TODO
            } else {
                sendAnswer(commandProcessorService.processServiceCommand(appUser, text));
            }
        }
    }

    private void sendAnswer(SendMessage sendMessage) {
        producerService.produceAnswer(sendMessage);
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

    private AppUser findOrSaveAppuser(Message message) {
        User telegramUser = message.getFrom();
        AppUser persistentAppUser = appUserDao.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser
                    .builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .state(BASIC_STATE)
                    .botState(BotState.BASIC)
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
