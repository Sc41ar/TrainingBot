package org.edu.service.enums;


public enum ServiceCommands {
    HELP("/help"), //запись на занятие
    APPOINTMENT("/appointment"), CANCEL("/cancel"), //инфа по группам, записям и т.д.
    INFO("/info"), //создание активности админом или тренером
    OCCUPATION("/occupation"), AUTH("/auth"), UNVERIFIEDLIST("/unverified_list"), START("/start");

    private final String cmd;


    ServiceCommands(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }

    public boolean equals(String cmd) {
        return this.toString().equals(cmd);
    }
}