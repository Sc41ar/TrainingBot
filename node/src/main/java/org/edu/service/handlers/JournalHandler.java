package org.edu.service.handlers;

import org.edu.entity.AppUser;

//обработчик сервиса "журнала"
public interface JournalHandler {
    //установка состояня ввода пользователей
    public void setJournalingState(AppUser appUser);

    //очистка состояния
    public void removeJournalingState(AppUser appUser);

    //состояние добавления занятия на котором отмечали
    public void setAddJournalState(AppUser appUser);
    ///преображение
    public String processJournaling(String string, Long id);
}
