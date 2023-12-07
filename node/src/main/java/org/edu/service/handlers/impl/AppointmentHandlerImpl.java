package org.edu.service.handlers.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.dao.AppUserDao;
import org.edu.dao.OccupationDao;
import org.edu.dao.SubscriptionRepository;
import org.edu.entity.AppUser;
import org.edu.entity.Occupation;
import org.edu.entity.StudentSubscription;
import org.edu.entity.enums.BotState;
import org.edu.entity.enums.UserState;
import org.edu.service.ProducerService;
import org.edu.service.handlers.AppointmentHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.edu.specifications.AppUserSpecifications.hasRole;
import static org.edu.specifications.OccupationSpecifications.isNotExpired;

@Service
@Log4j2
public class AppointmentHandlerImpl implements AppointmentHandler {
    private final OccupationDao occupationDao;
    private final AppUserDao appUserDao;
    private final SubscriptionRepository subscriptionRepository;
    private final ProducerService producerService;

    public AppointmentHandlerImpl(OccupationDao occupationDao, AppUserDao appUserDao, SubscriptionRepository subscriptionRepository, ProducerService producerService) {
        this.occupationDao = occupationDao;
        this.appUserDao = appUserDao;
        this.subscriptionRepository = subscriptionRepository;
        this.producerService = producerService;
    }

    @Override
    public SendMessage processAppointment(AppUser appUser) {
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

        SendMessage sendMessage = new SendMessage(appUser.getTelegramUserId().toString(), answer.toString());

        sendMessage.setReplyMarkup(makeInitialMarkup());

        return sendMessage;
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
            if (isParticipant(appUser, occupation)) {
                returnBasicState(appUser);
                return "Уже записан";
            }
            appUser.getLessons().add(occupation);
            occupation.getParticipants().add(appUser);
            appUser.setBotState(BotState.BASIC);
            appUserDao.save(appUser);
            return "Запись прошла";
        } catch (Exception e) {
            log.error("/||\\" + e.getMessage() + "\n" + e.getCause());
            returnBasicState(appUser);
            return "Ошибка";
        }
    }

    private boolean isParticipant(AppUser appUser, Occupation occupation) {
        for (var user : occupation.getParticipants()) {
            if (user.getId().equals(appUser.getId()))
                return true;
        }
        for (var occ : appUser.getLessons()) {
            if (occ.getId().equals(occupation.getId()))
                return true;
        }
        return false;
    }

    @Override
    public void saveSubsCapacity(AppUser appUser, int count) {
        StudentSubscription persistentStudentSubscription = subscriptionRepository.findByStudentId(appUser.getId());
        StudentSubscription studentSubscription;
        if (persistentStudentSubscription == null) {
            studentSubscription = StudentSubscription.builder().student(appUser).lessonsCount(count).build();
        } else {
            int persistentCount = persistentStudentSubscription.getLessonsCount();
            persistentStudentSubscription.setLessonsCount(persistentCount + count);
            studentSubscription = persistentStudentSubscription;
        }
        try {
            subscriptionRepository.save(studentSubscription);
        } catch (Exception e) {
            log.error(e.getMessage() + " \t " + e.getCause());
        }
    }

    @Override
    public SendMessage chooseSubscriptionPlan(AppUser appUser) {
        SendMessage sendMessage = new SendMessage(appUser.getTelegramUserId().toString(), "Выберите один из планов\n\nА затем введите дату в формате: \n\n23.11.2023:19.00 название");
        sendMessage.setReplyMarkup(makeSubPlanMarkup());
        return sendMessage;
    }

    @Override
    public void notifyAdmin(AppUser appUser, int count) {
        var admins = appUserDao.findAll(hasRole(UserState.ADMIN_STATE));
        for (var user : admins) {
            sendAnswer("Пользователь " + appUser.getFirstName() + " @" + appUser.getUsername() + " активировал абонемент на " + count + " занятий", user.getTelegramUserId());
        }
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), output);
        producerService.produceAnswer(sendMessage);
    }

    private InlineKeyboardMarkup makeInitialMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonSingle = new InlineKeyboardButton();
        InlineKeyboardButton buttonSubscription = new InlineKeyboardButton();

        buttonSingle.setText("Пробное занятие");
        buttonSubscription.setText("Абонемент");

        buttonSingle.setCallbackData("trial");
        buttonSubscription.setCallbackData("subscription");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(buttonSingle);
        row.add(buttonSubscription);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);

        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup makeSubPlanMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        InlineKeyboardButton singlePlan = new InlineKeyboardButton();
        InlineKeyboardButton fourPlan = new InlineKeyboardButton();
        InlineKeyboardButton eightPlan = new InlineKeyboardButton();
        InlineKeyboardButton twelvePlan = new InlineKeyboardButton();
        InlineKeyboardButton twentyPlan = new InlineKeyboardButton();
        InlineKeyboardButton fortyPlan = new InlineKeyboardButton();

        singlePlan.setText("Разовое");
        singlePlan.setCallbackData("single");

        fourPlan.setText("4 занятия");
        fourPlan.setCallbackData("four");

        eightPlan.setText("8 занятий");
        eightPlan.setCallbackData("eight");

        twelvePlan.setText("12 занятий");
        twelvePlan.setCallbackData("twelve");

        twentyPlan.setText("20 занятий");
        twentyPlan.setCallbackData("twenty");

        fortyPlan.setText("40 занятий");
        fortyPlan.setCallbackData("forty");

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        row.add(singlePlan);
        row.add(fourPlan);

        rows.add(row);
        row = new ArrayList<>();
        row.add(eightPlan);
        row.add(twelvePlan);

        rows.add(row);
        row = new ArrayList<>();
        row.add(twentyPlan);
        row.add(fortyPlan);
        rows.add(row);

        markup.setKeyboard(rows);
        return markup;
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