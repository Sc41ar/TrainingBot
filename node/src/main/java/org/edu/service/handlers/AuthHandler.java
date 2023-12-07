package org.edu.service.handlers;

import org.edu.entity.AppUser;

//Сервис обработки запросов на подтверждение статуса
public interface AuthHandler {
    //запрос аутентифик
    public String processAuth(AppUser appUser);
    //показывает список, отправивших запрос
    public String showUnverifiedUsersList(AppUser appUser);
    //получение имени из обратного запроса
    public String getUsernameFromCallBackQueryMessage(String message);
    //установка статуса учителя, после подтверждения
    public void setUserTeacherState(String username);
    //возврщение статуса пользователю
    public void returnBasicState(AppUser appUser);
}
