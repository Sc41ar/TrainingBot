package org.edu.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.edu.UpdateController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j2
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    private UpdateController updateController;


    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
        init();
    }

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
        log.info(originalMessage.getText());
        var response = new SendMessage();
        response.setChatId(originalMessage.getChatId().toString());
        response.setText("Ya tvou mamu cenil)");
        sendAnswerMessage(response);
    }

    public void sendAnswerMessage(SendMessage message){
        if(message != null){
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage()+ "\n" + e.getCause());
            }
        }
    }
}
