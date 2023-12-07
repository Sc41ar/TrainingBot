package org.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import org.edu.entity.enums.BotState;
import org.edu.entity.enums.UserState;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

//добавить поля для сохранения пол hbателя
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //id пользователя в БД телеграма
    private Long telegramUserId;
    @CreationTimestamp
    //дат первого входа
    private LocalDateTime firstLoginDate;
    //заполняется из профиля телегарма
    private String firstName;
    private String lastName;
    private String username;
    @Enumerated(EnumType.STRING)//перечисление возможных состояний/ролей пользователя
    private UserState state;
    @Enumerated(EnumType.STRING)//возможные состояния бота для каждого пользователя
    private BotState botState;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)//Сет с уроками, на которые пользователь записан
    @JoinTable(
            name = "occupation_student",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "occupation_id", referencedColumnName = "id"))
    private Set<Occupation> lessons = new HashSet<>();
}