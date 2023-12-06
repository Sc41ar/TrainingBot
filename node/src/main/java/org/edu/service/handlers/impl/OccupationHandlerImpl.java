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
import java.util.List;

import static org.edu.entity.enums.UserState.ADMIN_STATE;
import static org.edu.entity.enums.UserState.TEACHER_STATE;
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
    public String processOccupation(AppUser appUser) {
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
    public String parseOccupatin(AppUser appUser, String string) {
        var singleAppointmentRegexCheck = string.matches("(0?[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.(202[3-9]|203[0-9]):([01][0-9]|2[0-4])\\.([0-5][0-9]) ([a-zA-Zа-яА-Я0-9 ]+)");

        try {
            if (!singleAppointmentRegexCheck) {

                returnBasicState(appUser);
                return "Неверный формат";
            }

            string = string.replaceAll("\"", "");
            ArrayList<String> subStrings = new ArrayList<String>(List.of(string.split(" |;")));
            subStrings.removeIf(String::isEmpty);

            if (subStrings.size() < 2) {
                log.error(string + "Ошибка прочтения данных");
                returnBasicState(appUser);
                return "Ошибка чтения данных";
            }

            Date parsedDate = parseDate(subStrings.get(0));
            StringBuilder name = new StringBuilder();
            for (int i = 1; i < subStrings.size(); i++) {
                name.append(subStrings.get(i));
                if (i + 1 < subStrings.size())
                    name.append(" ");
            }

            if (isThereOccupationThatMatchesDate(parsedDate)) return "В это время уже есть занятие";

            Occupation occupation = Occupation.builder().occupationName(name.toString()).date(parsedDate).teacher(appUser).build();
            occupationDao.save(occupation);
            Occupation occupationWithId = occupationDao.findByOccupationNameAndDate(occupation.getOccupationName(), occupation.getDate());
            producerService.produceMessage(occupationWithId.getId());

        } catch (Exception e) {
            log.error(e.getMessage() + "\n" + e.getCause());
            returnBasicState(appUser);
            return "Ошибка чтения";
        }
        returnBasicState(appUser);
        return "Запись сделана\uD83D\uDC4D";
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
