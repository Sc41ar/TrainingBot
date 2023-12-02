package org.edu.service.impl;

import org.edu.entity.AppUser;
import org.edu.service.CommandProcessorService;
import org.edu.service.ProducerService;
import org.edu.service.enums.ServiceCommands;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.edu.entity.enums.UserState.ADMIN_STATE;
import static org.edu.entity.enums.UserState.TEACHER_STATE;
import static org.edu.service.enums.ServiceCommands.*;

@Service
public class CommandProcessorServiceImpl implements CommandProcessorService {

    private final ProducerService producerService;

    public CommandProcessorServiceImpl(ProducerService producerService) {
        this.producerService = producerService;
    }

    @Override
    public String proccessServiceCommand(AppUser appUser, String cmd) {
        if (ServiceCommands.APPOINTMENT.equals(cmd)) {
            //TODO
            return "Временно недоступно";
        } else if (HELP.equals(cmd)) {
            return help(appUser);
        } else if (START.equals(cmd)) {
            return "Чтобы посмотреть список доступных комманд введите /help";
        } else if (cmd.matches("^(/occupation)(.*)$")) {
            processOccupation(appUser, cmd);
            return "";
        } else if (AUTH.equals(cmd)) {
            processAuth(appUser);
            return "OK";
        } else {
            return "Чтобы посмотреть список доступных комманд введите /help";
        }
    }

    private void processAuth(AppUser appUser) {
        return;
    }

    private void processOccupation(AppUser appUser, String cmd) {
        if (false){

        }
        else if (appUser.getState() == TEACHER_STATE || appUser.getState() == ADMIN_STATE) {
            sendAnswer("Для записи информации о предстоящих занятиях, пожалуйста повторно введите команду в следующем формате: " +
                    "\n\n/occupation dd.mm.yyyy;\"название группы\";\"ваше имя\"" +
                    "\n\nВажно! Указывайте имя такое же как и в телеграмме, а то бот вас не поймет"
                    , appUser.getTelegramUserId());
        } else {
            sendAnswer( "У вас недостаточно полномочий", appUser.getTelegramUserId());
        }
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private String help(AppUser appUser) {
        if (appUser.getState().equals(TEACHER_STATE) || appUser.getState().equals(ADMIN_STATE)) {
            return "Список доступных команд:\n"
                    + "/cancel - отмена команды\n"
                    + "/start - начало работы с ботом\n"
                    + "/appointment - запись на занятие\n"
                    + "/info - вывод информации о пользователе, его записях и группах\n"
                    + "/occupation - добавить в бота информацию о предстоящем занятии\n";
        } else {
            return "Список доступных команд:\n"
                    + "/cancel - отмена команды\n"
                    + "/start - начало работы с ботом\n"
                    + "/appointment - запись на занятие\n"
                    + "/info - вывод информации о пользователе, его записях и группах\n";
        }
    }
}
