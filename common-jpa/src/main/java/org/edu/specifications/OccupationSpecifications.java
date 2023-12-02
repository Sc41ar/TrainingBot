package org.edu.specifications;

import org.edu.entity.Occupation;
import org.edu.metamodel.Occupation_;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class OccupationSpecifications{
    public static Specification<Occupation> isNotExpired(){
        return ((root, query, criteriaBuilder) -> {
            LocalDateTime localDate = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDate.atZone(ZoneId.systemDefault());
            Date date = Date.from(zonedDateTime.toInstant());
            return criteriaBuilder.greaterThan(root.get(Occupation_.DATE), date);
        });
    }
}