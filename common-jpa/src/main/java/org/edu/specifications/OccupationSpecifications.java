package org.edu.specifications;

import org.edu.entity.AppUser;
import org.edu.entity.Occupation;
import org.edu.metamodel.Occupation_;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;

//запросы-спецификации

public class OccupationSpecifications {
    public static Specification<Occupation> isNotExpired() {
        return ((root, query, criteriaBuilder) -> {
            LocalDateTime localDate = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDate.atZone(ZoneId.systemDefault());
            Date date = Date.from(zonedDateTime.toInstant());
            return criteriaBuilder.greaterThan(root.get(Occupation_.DATE), date);
        });
    }

    public static Specification<Occupation> hasOccupationName(String name) {
        return (root, query, criteriaBuilder) -> (
                criteriaBuilder.equal(root.<String>get("occupationName"), name)
        );
    }

    public static Specification<Occupation> hasOccupationNameAndDate(String name, Date date) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(criteriaBuilder.equal(root.get("date"), date), criteriaBuilder.equal(root.get("occupationName"), name));
        };
    }

    public static Specification<Occupation> hasTeacherId(AppUser teacher) {
        return (root, query, criteriaBuilder) -> (
                criteriaBuilder.equal(root.<AppUser>get("teacher"), teacher)
        );
    }

    public static Specification<Occupation> hasStudent(AppUser student) {
        return ((root, query, criteriaBuilder) -> {
            var set = root.<Set<AppUser>>get("participants");
            return criteriaBuilder.isMember(student, set);
        });
    }
}