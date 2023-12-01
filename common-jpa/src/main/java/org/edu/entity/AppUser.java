package org.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import org.edu.entity.enums.UserState;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

//добавить поля для сохранения пол hbателя
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long telegramUserId;
    @CreationTimestamp
    private LocalDateTime firstLoginDate;
    private String firstName;
    private String lastName;
    private String username;
    @Enumerated(EnumType.STRING)
    private UserState state;
}