package org.edu.service.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.dao.AppUserDao;
import org.edu.dao.OccupationDao;
import org.edu.dao.SubscriptionRepository;
import org.edu.entity.AppUser;
import org.edu.service.ProducerService;
import org.edu.service.ReminderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@Log4j2
public class RemainderImpl implements ReminderService {
    private final ProducerService producerService;
    private final AppUserDao appUserDao;
    private final OccupationDao occupationDao;
    private final SubscriptionRepository studentSubscriptionDao;

    public RemainderImpl(ProducerService producerService, AppUserDao appUserDao, OccupationDao occupationDao, SubscriptionRepository studentSubscriptionDao) {
        this.producerService = producerService;
        this.appUserDao = appUserDao;
        this.occupationDao = occupationDao;
        this.studentSubscriptionDao = studentSubscriptionDao;
    }

    @Override
    @Scheduled(fixedRate = 3600000, initialDelay = 3600000)
    public void Remind() {
        var userList = appUserDao.findAll();
        for (var user : userList) {
            var usersLessons = user.getLessons();
            if (!usersLessons.isEmpty()||usersLessons != null) {
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

    @Override
    public void doAutoAppointmentString(Long occupationId) {
        var occupationsOptional = occupationDao.findById(occupationId);
        var occupation = occupationsOptional.get();
        var occupations = occupationDao.findByOccupationNameOrderByDate(occupation.getOccupationName());
        if (occupations == null || occupations.isEmpty()) {
            return;
        } else {
            Set<AppUser> participants = new HashSet<>();
            for (int i = 0; i < occupations.size(); i++) {
                if (occupations.get(i).getParticipants() != null) {
                    participants = occupations.get(i).getParticipants();
                    break;
                }
            }
            for (var user : participants) {
                if (studentSubscriptionDao.findByStudentId(user.getId()) != null) {
                    try {
                        occupation.getParticipants().add(user);
                        user.getLessons().add(occupation);
                        appUserDao.saveAndFlush(user);
                    } catch (Exception e) {
                        log.error(e.getMessage() + "\t" + e.getCause());
                    }
                }
            }
        }

    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }
}
