package org.edu;

import lombok.extern.log4j.Log4j2;
import org.edu.controller.TelegramBot;
import org.edu.service.UpdateProducer;
import org.edu.service.impl.UpdateProducerImpl;
import org.edu.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.model.RabbitQueue.*;

@Component
@Log4j2
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    @Autowired
    private UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    public void registerBot(TelegramBot bot){
        telegramBot = bot;
    }


    public void processUpdate(Update update){
        if(update == null){
            log.error("Received update is null");
            return;
        }

        if(update.getMessage() != null) {
            distributeMessagesByType(update);
        }else {
            log.error("Received unsupported message type" + update);
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if(message.getText()!=null){
            processTextMessage(update);
        }else if (message.getDocument() != null){
            processDocMessage(update);
        }else if (message.getPhoto() != null){
            processPhotoMessage(update);
        }else{
            setUnsupportedMessageTypeView(update);
        }
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    private void setFileIsReceivedView(Update update) {
         var sendMessge = messageUtils.generateSendMEssageWithText(update,
                 "File is received. Processing");
         setView(sendMessge);
    }

    //Пока не реагируем на ненужные нам сообщения отредактированные и т.д.
    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMEssageWithText(update,
                "Unsupported message type");
        setView(sendMessage);
    }

    //Для сохранения вертикали вызовов. Возможность вызывать отправку сообщений из других модулей.
    //Также обеспечивает "стройность" класса бота
    private void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }


    public UpdateProducer getUpdateProducer() {
        return updateProducer;
    }

    public void setUpdateProducer(UpdateProducer updateProducer) {
        this.updateProducer = updateProducer;
    }
}
