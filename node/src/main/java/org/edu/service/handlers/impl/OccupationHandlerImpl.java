package org.edu.service.handlers.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.dao.AppUserDao;
import org.edu.dao.OccupationDao;
import org.edu.entity.AppUser;
import org.edu.entity.Occupation;
import org.edu.entity.enums.BotState;
import org.edu.service.ProducerService;
import org.edu.service.handlers.OccupationHandler;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.edu.entity.enums.UserState.ADMIN_STATE;
import static org.edu.entity.enums.UserState.TEACHER_STATE;
import static org.edu.service.cons.ParseUtils.FORMAT_REGEX;
import static org.edu.specifications.OccupationSpecifications.isNotExpired;

@Log4j2
@Service
public class OccupationHandlerImpl implements OccupationHandler {
    private final AppUserDao appUserDao;
    private final OccupationDao occupationDao;
    private final ProducerService producerService;

    public OccupationHandlerImpl(AppUserDao appUserDao, OccupationDao occupationDao, ProducerService producerService) {
        this.appUserDao = appUserDao;
        this.occupationDao = occupationDao;
        this.producerService = producerService;
    }


    @Override
    public String initOccupation(AppUser appUser) {
        boolean rightsCheck = appUser.getState() == TEACHER_STATE || appUser.getState() == ADMIN_STATE;
        StringBuilder answer = new StringBuilder("");
        if (rightsCheck) {
            answer.append("Для записи информации о предстоящих занятиях,");
            answer.append(" пожалуйста повторно введите команду в следующем формате: ");
            answer.append("\n\n23.11.2023:19.00 название\n\n");
        } else {
            answer.append("У вас недостаточно полномочий");
        }
        appUser.setBotState(BotState.OCCUPATION);
        appUserDao.saveAndFlush(appUser);
        return answer.toString();
    }

    @Override
    public String processOccupatin(AppUser appUser, String string) {
        var singleAppointmentRegexCheck = string.matches(FORMAT_REGEX);

        if (!singleAppointmentRegexCheck) {//questionable
            return "Error format";
        }
        var parsedOccupation = parseOccupation(string);
        if (parsedOccupation != null) {
            if (isThereOccupationThatMatchesDate(parsedOccupation.getDate())) {
                returnBasicState(appUser);
                return "В это время уже есть занятие";
            }
            saveOccupationToDb(parsedOccupation);
            returnBasicState(appUser);
            return "Успешно ";
        } else {
            returnBasicState(appUser);
            return "ошибка записи";
        }
    }

    @Override
    public Occupation parseOccupation(String string) {
        try {
            string = string.replaceAll("\"", "");
            var firstSpaceIndex = string.indexOf(' ');
            var stringDate = string.substring(0, firstSpaceIndex);
            var name = string.substring(firstSpaceIndex);

            Date parsedDate = parseDate(stringDate);

            return Occupation.builder().occupationName(name).date(parsedDate).build();

        } catch (Exception e) {
            log.error(e.getMessage() + "\n" + e.getCause());
            return null;
        }
    }

    private void saveOccupationToDb(Occupation occupation) {
        try {
            occupationDao.save(occupation);
            Occupation occupationWithId = occupationDao.findByOccupationNameAndDate(occupation.getOccupationName(), occupation.getDate());
            producerService.produceMessage(occupationWithId.getId());
        } catch (Exception e) {
            log.info(e.getMessage() + "\t" + e.getCause());
        }
    }

    private void returnBasicState(AppUser appUser) {
        appUser.setBotState(BotState.BASIC);
        appUserDao.saveAndFlush(appUser);
    }

    private boolean isThereOccupationThatMatchesDate(Date date) {
        ArrayList<Occupation> occupationArrayList = new ArrayList<Occupation>(occupationDao.findAll(isNotExpired()));
        for (Occupation item : occupationArrayList) {
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
