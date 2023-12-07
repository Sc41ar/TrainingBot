package org.edu.service.handlers;

import org.edu.entity.AppUser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

//Сервис обрабатывающий сообщения о записи на существующее занятие
public interface AppointmentHandler {
    //начальная обработка
    public SendMessage processAppointment(AppUser appUser);

    //парсинг названия и даты из вводимой строки
    public String parseAppointment(AppUser appUser, String string);

    //сохранение выбранного абонемента
    public void saveSubsCapacity(AppUser appUser, int count);

    //выбор варианта абонемента
    public SendMessage chooseSubscriptionPlan(AppUser appUser);
    ////оповещение админов о том, что пользователь выбрал запись через абонемент, для записи и т.д.
    public void notifyAdmin(AppUser appUser, int count);
}