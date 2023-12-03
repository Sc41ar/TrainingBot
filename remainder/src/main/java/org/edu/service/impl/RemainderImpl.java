package org.edu.service.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.dao.AppUserDao;
import org.edu.dao.OccupationDao;
import org.edu.service.ProducerService;
import org.edu.service.ReminderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.ZonedDateTime;
import java.util.Date;

@Service
@Log4j2
public class RemainderImpl implements ReminderService {
    private final ProducerService producerService;
    private final AppUserDao appUserDao;
    private final OccupationDao occupationDao;

    public RemainderImpl(ProducerService producerService, AppUserDao appUserDao, OccupationDao occupationDao) {
        this.producerService = producerService;
        this.appUserDao = appUserDao;
        this.occupationDao = occupationDao;
    }

    @Override
    @Scheduled(fixedRate = 3600000, initialDelay = 3600000)
    public void Remind() {
        var userList = appUserDao.findAll();
        for (var user : userList) {
            var usersLessons = user.getLessons();
            if (usersLessons.isEmpty())
                continue;
            else {
                for (var lesson : usersLessons) {
                    ZonedDateTime zonedDateTime = ZonedDateTime.now().plusHours(2);
                    Date date = Date.from(zonedDateTime.toInstant());
                    if (lesson.getDate().before(date))
                        sendAnswer("REMIND", user.getTelegramUserId());
                }
            }
        }
        log.error("ded\n\n");
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

//    @Override
//    @Scheduled(fixedRate = 86400000, initialDelay = 87600)
//    public void DeleteExpired() {
//        var occupationCollection = occupationDao.findAll();
//        for (var occupation : occupationCollection) {
//            ZonedDateTime zonedDateTime = ZonedDateTime.now();
//            Date date = Date.from(zonedDateTime.toInstant());
//            if (occupation.getDate().before(date)) {
//                occupationDao.delete(occupation);
//                sendAnswer("DELETED", (long) 679925854);
//            }
//
//        }
//    }
}
