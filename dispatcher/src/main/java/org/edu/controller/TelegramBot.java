package org.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramBot extends TelegramLongPollingBot {
    @Value("${telegram.name}")
    private String botName;

    @Value("${telegram.token}")
    private String botToken;

    @Autowired
    public TelegramBot() {
        System.out.println(botName + " " + botToken);
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
        var originalMessage = update.getMessage();
        System.out.println(originalMessage.getText());
    }
}
