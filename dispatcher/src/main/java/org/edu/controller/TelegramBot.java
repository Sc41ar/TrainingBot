package org.edu.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    public TelegramBot() {
        init();
    }
//    @Autowired
//    public TelegramBot(@Value("${bot.name}")
//                       String botName,
//                       @Value("${bot.token}")
//                       String botToken) {
//        this.botName = botName;
//        this.botToken = botToken;
//        System.out.println(this.botName + " | " + this.botToken);
//    }

    @PostConstruct
    public void init(){
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
