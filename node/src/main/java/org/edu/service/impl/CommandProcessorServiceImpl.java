package org.edu.service.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.dao.OccupationDao;
import org.edu.entity.AppUser;
import org.edu.entity.Occupation;
import org.edu.service.CommandProcessorService;
import org.edu.service.ProducerService;
import org.edu.service.enums.ServiceCommands;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.edu.entity.enums.UserState.ADMIN_STATE;
import static org.edu.entity.enums.UserState.TEACHER_STATE;
import static org.edu.service.enums.ServiceCommands.*;
import static org.edu.specifications.OccupationSpecifications.isNotExpired;

@Service
@Log4j2
public class CommandProcessorServiceImpl implements CommandProcessorService {

    private final ProducerService producerService;
    private final OccupationDao occupationDao;

    public CommandProcessorServiceImpl(ProducerService producerService, OccupationDao occupationDao) {
        this.producerService = producerService;
        this.occupationDao = occupationDao;
    }

    @Override
    public String proccessServiceCommand(AppUser appUser, String cmd) {
        //создание записи на занятие
        if (cmd.matches("^(/appointment)(.*)$")) {
            processAppointment(appUser, cmd);
            return "Запись совершена";
        } else if (HELP.equals(cmd)) {
            return help(appUser);
        } else if (START.equals(cmd)) {
            return "Чтобы посмотреть список доступных комманд введите /help";
        } //Создание занятия
        else if (cmd.matches("^(/occupation)(.*)$")) {
            processOccupation(appUser, cmd);
            return "";
        } else if (AUTH.equals(cmd)) {
            processAuth(appUser);
            return "OK";
        } else {
            return "Чтобы посмотреть список доступных комманд введите /help";
        }
    }

    private void processAppointment(AppUser appUser, String cmd) {
        if (cmd.matches("^(/appointment) ((0[1-9]|[12][0-9]|3[0-1])\\.(0[1-9]|1[0-2])\\.(202[3-9]):([01][0-9]|2[0-3])\\.([0-6][0-9]);\"(([a-zA-Zа-яА-Я _+@]+)*)\")$")) {
        } else if (cmd.matches("^(/appointment)(.*)$")) {

            var occupList = occupationDao.findAll(isNotExpired());

            sendAnswer("List of occup:", appUser.getTelegramUserId());
        }
    }

    private void processAuth(AppUser appUser) {
        return;
    }

    private void processOccupation(AppUser appUser, String cmd) {//ono strajnoe 0_0
        if (cmd.matches("^(/occupation) (0[1-9]|[12][0-9]|3[0-1])\\.(0[1-9]|1[0-2])\\.(202[3-9]):([01][0-9]|2[0-3])\\.([0-6][0-9]);\"(([a-zA-Zа-яА-Я _+@]+)*)\"$")) {
            String output = parseOccupation(cmd, appUser);

            sendAnswer(output, appUser.getTelegramUserId());
        } else if (appUser.getState() == TEACHER_STATE || appUser.getState() == ADMIN_STATE) {
            sendAnswer("Для записи информации о предстоящих занятиях," +
                            " пожалуйста повторно введите команду в следующем формате: " +
                            "\n\n/occupation dd.mm.yyyy:hours.min;\"название группы\"" +
                            "\n\nВажно! Указывайте имя такое же как и в телеграмме, а то бот вас не поймет"
                    , appUser.getTelegramUserId());
        } else {
            sendAnswer("У вас недостаточно полномочий", appUser.getTelegramUserId());
        }
    }

    private String parseOccupation(String string, AppUser appUser) {
        try {
            string = string.replaceAll("\"", "");
            ArrayList<String> subStrings = new java.util.ArrayList<>(Arrays.stream(((string.split("/occupation|;")))).toList());
            subStrings.removeIf(String::isEmpty);
            if (subStrings.size() != 2) {
                log.error(string + "Ошибка прочтения данных");
                return "Ошибка чтения данных";
            }
            SimpleDateFormat format = new SimpleDateFormat(" dd.MM.yyyy:HH.mm");
            Date parsedDate = format.parse(subStrings.get(0));
            Occupation occupation = Occupation.builder()
                    .occupationName(subStrings.get(1))
                    .date(parsedDate)
                    .teacher(appUser)
                    .build();
            occupationDao.save(occupation);
        } catch (Exception e) {
            log.error(e.getMessage() + "\n" + e.getCause());
            return "Ошибка чтения";
        }
        return "Vse ok";
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
