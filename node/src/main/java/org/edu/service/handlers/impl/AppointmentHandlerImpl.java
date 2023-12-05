package org.edu.service.handlers.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.dao.AppUserDao;
import org.edu.dao.OccupationDao;
import org.edu.entity.AppUser;
import org.edu.entity.Occupation;
import org.edu.entity.enums.BotState;
import org.edu.service.ProducerService;
import org.edu.service.handlers.AppointmentHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.edu.specifications.OccupationSpecifications.isNotExpired;

@Service
@Log4j2
public class AppointmentHandlerImpl implements AppointmentHandler {
    private final ProducerService producerService;
    private final OccupationDao occupationDao;
    private final AppUserDao appUserDao;

    public AppointmentHandlerImpl(ProducerService producerService, OccupationDao occupationDao, AppUserDao appUserDao) {
        this.producerService = producerService;
        this.occupationDao = occupationDao;
        this.appUserDao = appUserDao;
    }

    @Override
    public String processAppointment(AppUser appUser) {
        StringBuilder answer = new StringBuilder("Список ближайших групп:");
        var occupList = occupationDao.findAll(isNotExpired());
        HashSet<String> occupationNames = new HashSet<String>();
        for (var item : occupList) {
            occupationNames.add(item.getOccupationName());
        }
        int i = 1;
        for (var item : occupationNames) {
            var date = occupationDao.findByOccupationNameOrderByDate(item).get(0).getDate();
            answer.append("\n▪\uFE0F" + i + ". " + item + " | " + date.toString());
            i++;
        }
        answer.append("\nВыбери одну из групп\nДля записи введите следующую строку: 23.11.2023:19.00 название");

        appUser.setBotState(BotState.APPOINTMENT);
        appUserDao.saveAndFlush(appUser);
        return answer.toString();
    }

    @Override
    public String parseAppointment(AppUser appUser, String string) {
        string = string.replaceAll("\"", "");
        ArrayList<String> subStrings = new ArrayList<String>(List.of(string.split(" |;")));
        subStrings.removeIf(String::isEmpty);

        Date appointmentDate = parseDate(subStrings.get(0));

        if (!isThereOccupationThatMatchesDate(appointmentDate)) {
            returnBasicState(appUser);
            return "В это время нет занятия";
        }

        if (occupationDao.findByOccupationNameAndDate(subStrings.get(1), appointmentDate) == null) {
            returnBasicState(appUser);
            return "Нет такой группы";
        }

        try {
            Occupation occupation = occupationDao.findByOccupationNameAndDate(subStrings.get(1), appointmentDate);
            appUser.getLessons().add(occupation);
            occupation.getParticipants().add(appUser);
            occupationDao.flush();
            appUserDao.saveAndFlush(appUser);
        } catch (Exception e) {
            log.error("/||\\" + e.getMessage() + "\n" + e.getCause());
            returnBasicState(appUser);
            return "Ошибка";
        }
        returnBasicState(appUser);
        return "Запись прошла";
    }

    private void returnBasicState(AppUser appUser) {
        appUser.setBotState(BotState.BASIC);
        appUserDao.saveAndFlush(appUser);
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
}