package org.edu;

import org.edu.controller.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class DispatcherApp {
    public static void main(String[] args) throws TelegramApiException {
        SpringApplication.run(DispatcherApp.class);
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(new TelegramBot());
        } catch (TelegramApiException e) {
            System.out.println("Runtime EX");
            System.out.println(e.getMessage());
        }
    }
}
