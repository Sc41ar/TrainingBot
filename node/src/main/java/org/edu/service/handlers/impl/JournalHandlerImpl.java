package org.edu.service.handlers.impl;

import lombok.extern.log4j.Log4j2;
import org.edu.dao.AppUserDao;
import org.edu.dao.OccupationDao;
import org.edu.dao.SubscriptionRepository;
import org.edu.entity.AppUser;
import org.edu.entity.enums.BotState;
import org.edu.service.handlers.JournalHandler;
import org.edu.service.handlers.OccupationHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class JournalHandlerImpl implements JournalHandler {
    private final AppUserDao appUserDao;
    private final OccupationDao occupationDao;
    private final OccupationHandler occupationHandler;
    private final SubscriptionRepository SubscriptionDao;

    public JournalHandlerImpl(AppUserDao appUserDao, OccupationDao occupationDao, OccupationHandler occupationHandler, SubscriptionRepository subscriptionDao) {
        this.appUserDao = appUserDao;
        this.occupationDao = occupationDao;
        this.occupationHandler = occupationHandler;
        SubscriptionDao = subscriptionDao;
    }

    @Override
    public void setJournalingState(AppUser appUser) {
        try {
            appUser.setBotState(BotState.JOURNALING);
            appUserDao.save(appUser);
        } catch (Exception e) {
            log.debug("gigig");
            log.info(e.getMessage() + "\t" + e.getCause());
        }
    }

    @Override
    public void removeJournalingState(AppUser appUser) {
        try {
            appUser.setBotState(BotState.BASIC);
            appUserDao.save(appUser);
        } catch (Exception e) {
            log.debug("gigiti");
            log.info(e.getMessage() + "\t" + e.getCause());
        }
    }

    @Override
    public void setAddJournalState(AppUser appUser) {
        try {
            appUser.setBotState(BotState.ADDJOURNAL);
            appUserDao.save(appUser);
        } catch (Exception e) {
            log.debug("gigig");
            log.info(e.getMessage() + "\t" + e.getCause());
        }
    }

    @Override
    public String processJournaling(String string, Long id) {
        try {
            var occupation = occupationDao.findById(id);
            var subStrings = List.of(string.split(" "));
            var user = appUserDao.findByFirstName(subStrings.get(0));
            var studentSub = SubscriptionDao.findByStudentId(user.getId());
            studentSub.setLessonsCount(studentSub.getLessonsCount() - 1);
            if (!user.getLessons().contains(occupation.get())) {
                user.getLessons().add(occupation.get());
            }
            if (!occupation.get().getParticipants().contains(user)) {
                occupation.get().getParticipants().add(user);
            }
            appUserDao.saveAndFlush(user);
            return "Пользователь отмечен";
        } catch (Exception e) {
            log.error(e.getMessage() + "\t" + e.getCause());
            return "75";
        }
    }

//    @Override
//    public String processJournaling(String string) {
//        if (string == null)
//            return "Ошибка";
//        else {
//            switch (string.getOccupationName()) {
//                case "Error format" -> {
//                    return "Неверный формат";
//                }
//                case "В это время уже есть занятие" -> {
//                    return "В это время уже есть занятие";
//                }
//                default -> {
//
//                }
//            }
//        }
//    }
}
