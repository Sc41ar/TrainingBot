package org.edu.service.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.dao.AppUserDao;
import org.edu.dao.OccupationDao;
import org.edu.entity.AppUser;
import org.edu.entity.Occupation;
import org.edu.entity.enums.BotState;
import org.edu.service.CommandProcessorService;
import org.edu.service.ProducerService;
import org.edu.service.handlers.AppointmentHandler;
import org.edu.service.handlers.AuthHandler;
import org.edu.service.handlers.InfoHandler;
import org.edu.service.handlers.OccupationHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.edu.entity.enums.UserState.ADMIN_STATE;
import static org.edu.entity.enums.UserState.TEACHER_STATE;
import static org.edu.specifications.OccupationSpecifications.isNotExpired;

@Service
@Log4j2
public class CommandProcessorServiceImpl implements CommandProcessorService {

    private final ProducerService producerService;
    private final OccupationDao occupationDao;
    private final AppUserDao appUserDao;
    private final InfoHandler infoHandler;
    private final AppointmentHandler appointmentHandler;
    private final OccupationHandler occupationHandler;
    private final AuthHandler authHandler;

    public CommandProcessorServiceImpl(ProducerService producerService, OccupationDao occupationDao, AppUserDao appUserDao, InfoHandler infoHandler, AppointmentHandler appointmentHandler, OccupationHandler occupationHandler, AuthHandler authHandler) {
        this.producerService = producerService;
        this.occupationDao = occupationDao;
        this.appUserDao = appUserDao;
        this.infoHandler = infoHandler;
        this.appointmentHandler = appointmentHandler;
        this.occupationHandler = occupationHandler;
        this.authHandler = authHandler;
    }

    @Override
    public SendMessage processServiceCommand(AppUser appUser, String cmd) {
        SendMessage answer = new SendMessage();
        answer.setChatId(appUser.getTelegramUserId());
        if (cmd == null) {
            answer.setText("");
        }
        if (cmd.equals("/get_state")) {
            answer.setText(appUser.getBotState().name());
        }
        switch (appUser.getBotState()) {
            case AUTHENTICATION -> {
                if (cmd != null && !cmd.isEmpty()) {
                    var username = authHandler.getUsernameFromCallBackQueryMessage(cmd);
                    authHandler.setUserTeacherState(username);
                    authHandler.returnBasicState(appUser);
                    answer.setText("");
                }
                String answerString = authHandler.showUnverifiedUsersList(appUser);
                answer.setText(answerString);
            }
            case BASIC -> {
                switch (cmd) {
                    case "/start" -> {
                        answer.setText("Чтобы посмотреть список доступных комманд введите /help");

                    }
                    case "/help" -> {
                        answer.setText(help(appUser));
                    }
                    case "/info" -> {
                        answer.setText(infoHandler.infoOutput(appUser));
                    }
                    case "/appointment" -> {
                        answer.setText(appointmentHandler.processAppointment(appUser));
                    }
                    case "/occupation" -> {
                        answer.setText(occupationHandler.processOccupation(appUser));
                    }
                    case "/auth" -> {
                        answer.setText(authHandler.processAuth(appUser));
                    }
                    case "/unverified_list" -> {
                        appUser.setBotState(BotState.AUTHENTICATION);
                        appUserDao.saveAndFlush(appUser);
                        answer.setText(authHandler.showUnverifiedUsersList(appUser));

                    }


                    default -> {
                        answer.setText("nuulll");
                    }
                }
            }
            case APPOINTMENT -> {
                answer.setText(appointmentHandler.parseAppointment(appUser, cmd));
            }
            case OCCUPATION -> {
                answer.setText(occupationHandler.parseOccupatin(appUser, cmd));
            }
            default -> {
                answer.setText("Ошибка");
            }
        }
        return answer;
    }

    @Override
    public String processCallBackQuery(CallbackQuery query, AppUser appUser) {
        switch (query.getData()) {
            case "Подтвердить" -> {
                var username = authHandler.getUsernameFromCallBackQueryMessage(query.getMessage().getText());
                authHandler.setUserTeacherState(username);
                authHandler.returnBasicState(appUser);
                return "Потвержден";
            }
            case "Отклонить" -> {
                authHandler.returnBasicState(appUser);
                return "Отклонен";
            }
            default -> {
                return "Ошибка";
            }
        }
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