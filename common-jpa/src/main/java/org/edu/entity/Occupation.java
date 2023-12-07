package org.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "occupation_table")
//сущность занятия
public class Occupation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //наименования группы
    private String occupationName;
    //Даьа самого занятия
    private Date date;
    //ссылка на пользователя - учителя
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private AppUser teacher;
    //участники занятия
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE,
            mappedBy = "lessons")
    private Set<AppUser> participants = new HashSet<>();
}
