package org.edu;

import org.edu.controller.TelegramBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@PropertySource("classpath:application.properties")
public class DispatcherApp {
    public static void main(String[] args) throws TelegramApiException {
        SpringApplication springApplication = new SpringApplication(DispatcherApp.class);
        springApplication.run();
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DispatcherApp.class);
        TelegramBot bot = context.getBean("telegramBot", TelegramBot.class);
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(bot);

        } catch (TelegramApiException e) {
            System.out.println("Runtime EX");
            System.out.println(e.getMessage());
        }
    }
}
