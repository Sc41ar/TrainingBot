package org.edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

//микросервис, занимающийся обработкой сообщения, большинство работы с БД
@SpringBootApplication
@PropertySource("classpath:application.properties")
public class NodeApp {
    public static void main(String[] args) throws TelegramApiException {
        SpringApplication springApplication = new SpringApplication(NodeApp.class);
        springApplication.run();
    }
}
