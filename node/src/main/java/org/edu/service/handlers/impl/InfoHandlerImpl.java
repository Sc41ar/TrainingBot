package org.edu.service.handlers.impl;

import org.edu.dao.OccupationDao;
import org.edu.entity.AppUser;
import org.edu.service.handlers.InfoHandler;
import org.springframework.stereotype.Service;

import java.util.HashSet;

import static org.edu.specifications.OccupationSpecifications.hasStudent;
import static org.edu.specifications.OccupationSpecifications.hasTeacherId;

@Service
public class InfoHandlerImpl implements InfoHandler {
    private final OccupationDao occupationDao;

    public InfoHandlerImpl(OccupationDao occupationDao) {
        this.occupationDao = occupationDao;
    }

    @Override
    public String  infoOutput(AppUser appUser) {
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
        return answer.toString();
    }
}
