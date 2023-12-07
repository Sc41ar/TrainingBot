package org.edu.service;

public interface ReminderService {
    //сервис-напоминалка о занятии
    public void Remind();

    //    public void DeleteExpired();
    //авто запись на новое занятие
    public void doAutoAppointmentString(Long occupationId);
}
