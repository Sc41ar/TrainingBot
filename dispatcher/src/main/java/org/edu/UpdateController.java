package org.edu;

import lombok.extern.log4j.Log4j2;
import org.edu.controller.TelegramBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Log4j2
public class UpdateController {
    private TelegramBot telegramBot;

    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update){
        if(update == null){
            log.error("Recieved update is null");
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
    }

    private void processPhotoMessage(Update update) {
        
    }

    private void processDocMessage(Update update) {
        
    }

    private void setUnsupportedMessageTypeView(Update update) {
        
    }


}
