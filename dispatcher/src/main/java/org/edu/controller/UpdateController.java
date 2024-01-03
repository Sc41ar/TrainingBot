package org.edu.controller;

import lombok.extern.log4j.Log4j2;
import org.edu.service.UpdateProducer;
import org.edu.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.edu.model.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Component
@Log4j2
//Контроллер, который получает обновления от бота
public class UpdateController {
    private final MessageUtils messageUtils;
    private TelegramBot telegramBot;
    @Autowired
    private UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }
    //сеттер
    public void registerBot(TelegramBot bot) {
        telegramBot = bot;
    }

    //обработчик обновлений из бота
    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }//Защита от качингсов
        if (  update.hasCallbackQuery()  ||
                (update.getPoll() == null
                    && !update.getMessage().hasPhoto()
                    && !update.getMessage().hasDocument()
                    && update.getMessage().hasText()
                ) ||
                update.getMessage().hasVideoNote()

        )
            processTextMessage(update);
    }
    //вызов сервиса отправителя
    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }


    //Для сохранения вертикали вызовов. Возможность вызывать отправку сообщений из других модулей.
    //Также обеспечивает "стройность" класса бота
    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }


    public UpdateProducer getUpdateProducer() {
        return updateProducer;
    }

    public void setUpdateProducer(UpdateProducer updateProducer) {
        this.updateProducer = updateProducer;
    }
}
