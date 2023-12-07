package org.edu.service.handlers;

import org.edu.entity.AppUser;

//Обработчик вывода информационного сообщения
public interface InfoHandler {
    //вывод информацциии
    public String infoOutput(AppUser appUser);
}
