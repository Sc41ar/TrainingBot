package org.edu.service.handlers;

import org.edu.entity.AppUser;
import org.edu.entity.Occupation;

//сервис вноса информации о занятии
public interface OccupationHandler {
    //старт обработки и установка состояния
    public String initOccupation(AppUser appUser);
//обработка
    public String processOccupatin(AppUser appUser, String string);
//парсинг даты и названия из строки
    public Occupation parseOccupation(String string);
}
