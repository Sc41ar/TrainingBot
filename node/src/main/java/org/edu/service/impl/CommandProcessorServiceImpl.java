package org.edu.service.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.dao.AppUserDao;
import org.edu.dao.OccupationDao;
import org.edu.entity.AppUser;
import org.edu.entity.Occupation;
import org.edu.service.CommandProcessorService;
import org.edu.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.edu.entity.enums.UserState.ADMIN_STATE;
import static org.edu.entity.enums.UserState.TEACHER_STATE;
import static org.edu.service.enums.ServiceCommands.*;
import static org.edu.specifications.OccupationSpecifications.*;

@Service
@Log4j2
public class CommandProcessorServiceImpl implements CommandProcessorService {

    private final ProducerService producerService;
    private final OccupationDao occupationDao;
    private final AppUserDao appUserDao;

    public CommandProcessorServiceImpl(ProducerService producerService, OccupationDao occupationDao, AppUserDao appUserDao) {
        this.producerService = producerService;
        this.occupationDao = occupationDao;
        this.appUserDao = appUserDao;
    }

    @Override
    public String proccessServiceCommand(AppUser appUser, String cmd) {
        //создание записи на занятие
        if (cmd.matches("^(/appointment)(.*)$")) {
            processAppointment(appUser, cmd);
            return "";
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
            return "";
        } else if (INFO.equals(cmd)) {
            infoOutput(appUser);
            return "";
        } else {
            return "Чтобы посмотреть список доступных комманд введите /help";
        }
    }

    private void infoOutput(AppUser appUser) {
        var teacherAt = occupationDao.findAll(hasTeacherId(appUser));
        var studentAt = occupationDao.findAll(hasStudent(appUser));
        HashSet<String> occupationNames = new HashSet<String>();
        for (var item : teacherAt) {
            occupationNames.add(item.getOccupationName());
        }
        StringBuilder answer = new StringBuilder(appUser.getFirstName() + ":\nТренер в: ");
        int i = 1;
        for (var item : occupationNames) {
            answer.append("\n▪\uFE0F" + i + ". " + item);
            i++;
        }
        occupationNames.clear();
        for (var item : studentAt) {
            occupationNames.add(item.getOccupationName());
        }
        answer.append("\n\uD83C\uDF1AУченик в:");
        for (var item : occupationNames) {
            answer.append("\n▪\uFE0F" + i + ". " + item);
            i++;
        }
        sendAnswer(answer.toString(), appUser.getTelegramUserId());
    }

    private void processAppointment(AppUser appUser, String cmd) {
        StringBuilder answer = new StringBuilder("List of occup:");
        if (cmd.matches("^(/appointment) ((0[1-9]|[12][0-9]|3[0-1])\\.(0[1-9]|1[0-2])\\.(202[3-9]):([01][0-9]|2[0-3])"
                + "\\.([0-6][0-9]);\"(([a-zA-Zа-яА-Я _+@]+)*)\")$")) {
            sendAnswer(parseAppointment(appUser, cmd), appUser.getTelegramUserId());
        } else if (cmd.matches("^(/appointment)(.*)$")) {
            var occupList = occupationDao.findAll(isNotExpired());
            HashSet<String> occupationNames = new HashSet<String>();
            for (var item : occupList) {
                occupationNames.add(item.getOccupationName());
            }
            int i = 1;
            for (var item : occupationNames) {
                answer.append("\n▪\uFE0F" + i + ". " + item);
                i++;
            }
            sendAnswer(answer.toString(), appUser.getTelegramUserId());
        }
    }

    private String parseAppointment(AppUser appUser, String string) {
        string = string.replaceAll("\"", "");
        ArrayList<String> subStrings = new ArrayList<String>(List.of(string.split("/appointment |;")));
        subStrings.removeIf(String::isEmpty);

        Date appointmentDate = parseDate(subStrings.get(0));

        if (!isThereOccupationThatMatchesDate(appointmentDate)) return "В это время нет занятия";

        if (occupationDao.findByOccupationNameAndDate(subStrings.get(1), appointmentDate) == null)
            return "Нет такой группы";

        try {
            Occupation occupation = occupationDao.findByOccupationNameAndDate(subStrings.get(1), appointmentDate);
            appUser.getLessons().add(occupation);
            occupation.getParticipants().add(appUser);
            occupationDao.flush();
            appUserDao.saveAndFlush(appUser);
        } catch (Exception e) {
            log.error("/||\\" + e.getMessage() + "\n" + e.getCause());
            return "pizdec";
        }
        return "Vse good";
    }

    private boolean isThereOccupationThatMatchesDate(Date date) {
        ArrayList<Occupation> occupationArrayList = new ArrayList<Occupation>(occupationDao.findAll(isNotExpired()));
        for (var item : occupationArrayList) {
            if (date.equals(item.getDate())) return true;
        }
        return false;
    }

    private Date parseDate(String stDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy:HH.mm");
            return format.parse(stDate);
        } catch (Exception e) {
            log.error(e.getMessage() + "\n\t\t" + e.getCause());
            return new Date();
        }
    }

    private void processAuth(AppUser appUser) {
    }

    private void processOccupation(AppUser appUser, String cmd) {//ono strajnoe 0_0
        boolean rightsCheck = appUser.getState() == TEACHER_STATE || appUser.getState() == ADMIN_STATE;
        if (cmd.matches("^(/occupation) (0[1-9]|[12][0-9]|3[0-1])" + "\\.(0[1-9]|1[0-2])" + "\\.(202[3-9]):([01][0-9]|2[0-3])" + "\\.([0-6][0-9]);\"(([a-zA-Zа-яА-Я _+@]+)*)\"$") && rightsCheck) {
            String output = parseOccupation(appUser, cmd);
            sendAnswer(output, appUser.getTelegramUserId());
        } else if (rightsCheck) {
            sendAnswer("Для записи информации о предстоящих занятиях," + " пожалуйста повторно введите команду в следующем формате: " + "\n\n/occupation dd.mm.yyyy:hours.min;\"название группы\"" + "\n\nВажно! Указывайте имя такое же как и в телеграмме, а то бот вас не поймет", appUser.getTelegramUserId());
        } else {
            sendAnswer("У вас недостаточно полномочий", appUser.getTelegramUserId());
        }
    }

    private String parseOccupation(AppUser appUser, String string) {
        try {
            string = string.replaceAll("\"", "");
            ArrayList<String> subStrings = new ArrayList<String>(List.of(string.split("/occupation |;")));
            subStrings.removeIf(String::isEmpty);

            if (subStrings.size() != 2) {
                log.error(string + "Ошибка прочтения данных");
                return "Ошибка чтения данных";
            }

            Date parsedDate = parseDate(subStrings.get(0));

            if (isThereOccupationThatMatchesDate(parsedDate)) return "В это время уже есть занятие";

            Occupation occupation = Occupation.builder().occupationName(subStrings.get(1)).date(parsedDate).teacher(appUser).build();
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
            return "Список доступных команд:\n" + "/cancel - отмена команды\n" + "/start - начало работы с ботом\n" + "/appointment - запись на занятие\n" + "/info - вывод информации о пользователе, его записях и группах\n" + "/occupation - добавить в бота информацию о предстоящем занятии\n";
        } else {
            return "Список доступных команд:\n" + "/cancel - отмена команды\n" + "/start - начало работы с ботом\n" + "/appointment - запись на занятие\n" + "/info - вывод информации о пользователе, его записях и группах\n";
        }
    }
}