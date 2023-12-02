package org.edu.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "occupation_table")
public class Occupation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String occupation_id;
    private java.sql.Date date;
    private Long teacher_id;
}
