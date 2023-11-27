package org.edu.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@PropertySource("classpath:application.properties")
public class TelegramBot extends TelegramLongPollingBot {
   @Value("${bot.Name}")
   private String botName;
   @Value("${bot.Token}")
   private String botToken;

    @Override
    public  String getBotUsername(){
        return botName;
    }

    @Override
    public String getBotToken(){
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var originalMessage = update.getMessage();
        System.out.println(originalMessage.getText());
    }
}
