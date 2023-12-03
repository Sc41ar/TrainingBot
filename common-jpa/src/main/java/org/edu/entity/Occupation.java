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
public class Occupation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String occupationName;

    private Date date;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private AppUser teacher;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE,
            mappedBy = "lessons")
    private Set<AppUser> participants = new HashSet<>();
}
