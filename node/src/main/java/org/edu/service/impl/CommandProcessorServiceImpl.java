package org.edu.service.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.dao.AppUserDao;
import org.edu.dao.OccupationDao;
import org.edu.entity.AppUser;
import org.edu.entity.enums.BotState;
import org.edu.service.CommandProcessorService;
import org.edu.service.ProducerService;
import org.edu.service.handlers.*;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static org.edu.entity.enums.UserState.ADMIN_STATE;
import static org.edu.entity.enums.UserState.TEACHER_STATE;
import static org.edu.service.cons.ParseUtils.FORMAT_REGEX;
import static org.edu.specifications.OccupationSpecifications.hasOccupationNameAndDate;

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
    private final JournalHandler journalHandler;

    private Long cachedId;

    public CommandProcessorServiceImpl(ProducerService producerService, OccupationDao occupationDao, AppUserDao appUserDao, InfoHandler infoHandler, AppointmentHandler appointmentHandler, OccupationHandler occupationHandler, AuthHandler authHandler, JournalHandler journalHandler) {
        this.producerService = producerService;
        this.occupationDao = occupationDao;
        this.appUserDao = appUserDao;
        this.infoHandler = infoHandler;
        this.appointmentHandler = appointmentHandler;
        this.occupationHandler = occupationHandler;
        this.authHandler = authHandler;
        this.journalHandler = journalHandler;
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
            return answer;
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
                        return (appointmentHandler.processAppointment(appUser));
                    }
                    case "/occupation" -> {
                        answer.setText(occupationHandler.initOccupation(appUser));
                    }
                    case "/auth" -> {
                        answer.setText(authHandler.processAuth(appUser));
                    }
                    case "/unverified_list" -> {
                        appUser.setBotState(BotState.AUTHENTICATION);
                        appUserDao.saveAndFlush(appUser);
                        answer.setText(authHandler.showUnverifiedUsersList(appUser));

                    }
                    case "/journal" -> {
                        journalHandler.setAddJournalState(appUser);
                        answer.setText("Введите список посещений: ");
                    }
                    default -> {
                        answer.setText("nuulll");
                    }
                }
            }
            case JOURNALING -> {
                switch (cmd) {
                    case "exit", "выход", "quit", "q", "e" -> {
                        answer.setText("выход из сервиса журнала");
                        journalHandler.removeJournalingState(appUser);
//TODO
                    }
                    default -> {
                        answer.setText(journalHandler.processJournaling(cmd, cachedId));
//TODO
                    }
                }
            }
            case ADDJOURNAL -> {
                var check = cmd.matches(FORMAT_REGEX);
                if (check) {
                    var parsedOcc = occupationHandler.parseOccupation(cmd);
                    var name = parsedOcc.getOccupationName();
                    var lissOcc = occupationDao.findAll(hasOccupationNameAndDate(name, parsedOcc.getDate()));
//                    var check_ = a.get(9).equals(parsedOcc.getOccupationName());
                    var occ = lissOcc.get(0);
                    cachedId = occ.getId();
                    journalHandler.setJournalingState(appUser);
                }
                if (cachedId == null) {
                    answer.setText("Ошибка ввода");
                }
            }
            case APPOINTMENT -> {
                answer.setText(appointmentHandler.parseAppointment(appUser, cmd));
            }
            case OCCUPATION -> {
                answer.setText(occupationHandler.processOccupatin(appUser, cmd));
            }
            default -> {
                answer.setText("Ошибка");
            }
        }
        return answer;
    }

    @Override
    public SendMessage processCallBackQuery(CallbackQuery query, AppUser appUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(appUser.getTelegramUserId());
        switch (query.getData()) {
            case "Подтвердить" -> {
                var username = authHandler.getUsernameFromCallBackQueryMessage(query.getMessage().getText());
                authHandler.setUserTeacherState(username);
                authHandler.returnBasicState(appUser);
                sendMessage.setText("Потвержден");
            }
            case "Отклонить" -> {
                authHandler.returnBasicState(appUser);
                sendMessage.setText("Отклонен");
            }
            case "single", "trial" -> {
                appointmentHandler.saveSubsCapacity(appUser, 1);
                appointmentHandler.notifyAdmin(appUser, 1);
                sendMessage.setText("Выбрано пробное занятие\nВведите дату и нзвание группы");
            }
            case "subscription" -> {
                sendMessage = appointmentHandler.chooseSubscriptionPlan(appUser);

            }
            case "four" -> {
                var count = 4;
                appointmentHandler.saveSubsCapacity(appUser, count);
                appointmentHandler.notifyAdmin(appUser, count);
                sendMessage.setText("Выбрано " + count + " занятия");
            }
            case "eight" -> {
                var count = 8;
                appointmentHandler.saveSubsCapacity(appUser, count);
                appointmentHandler.notifyAdmin(appUser, count);
                sendMessage.setText("Выбрано " + count + " занятия");
            }
            case "twelve" -> {
                var count = 12;
                appointmentHandler.saveSubsCapacity(appUser, count);
                appointmentHandler.notifyAdmin(appUser, count);
                sendMessage.setText("Выбрано " + count + " занятия");
            }
            case "twenty" -> {
                var count = 20;
                appointmentHandler.saveSubsCapacity(appUser, count);
                appointmentHandler.notifyAdmin(appUser, count);
                sendMessage.setText("Выбрано " + count + " занятия");
            }
            case "forty" -> {
                var count = 40;
                appointmentHandler.saveSubsCapacity(appUser, count);
                appointmentHandler.notifyAdmin(appUser, count);
                sendMessage.setText("Выбрано " + count + " занятия");
            }
            default -> {
                sendMessage.setText("Ошибка");
            }
        }
        return sendMessage;
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