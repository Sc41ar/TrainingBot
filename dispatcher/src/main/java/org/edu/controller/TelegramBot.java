package org.edu.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;

@Component
@Log4j2
public class TelegramBot extends TelegramLongPollingBot {

    private final UpdateController updateController;
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;


    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
        init();
    }

    @PostConstruct
    public void init() {
        updateController.registerBot(this);
        ArrayList<BotCommand> commandArrayList = new ArrayList<>();
        commandArrayList.add(new BotCommand("/help", "Получить справку по командам бота"));
        commandArrayList.add(new BotCommand("/start", "Начать общение с ботом"));
        commandArrayList.add(new BotCommand("/cancel", "Отмена текущей операции"));
        commandArrayList.add(new BotCommand("/occupation", "Запись информации о занятии"));
        commandArrayList.add(new BotCommand("/appointment", "Запись на занятие"));
        commandArrayList.add(new BotCommand("/info", "Вывод информации о пользователе, его группах и записях"));
        commandArrayList.add(new BotCommand("/auth", "Запрос на подтверждение личности"));
        commandArrayList.add(new BotCommand("/unverified_list", "Список пользователей, ожидающих подтверждений"));
        commandArrayList.add(new BotCommand("/get_state", "Выводит состояние бота"));
        try {
            this.execute(new SetMyCommands(commandArrayList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateController.processUpdate(update);
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage() + "\n" + e.getCause());
            }
        }
    }
}
