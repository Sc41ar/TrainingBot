package org.edu.specifications;

import org.edu.entity.AppUser;
import org.edu.entity.enums.UserState;
import org.springframework.data.jpa.domain.Specification;

//Запросы-Спецификации для пользователя
public class AppUserSpecifications {

    public static Specification<AppUser> hasFirstName(String name) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.<String>get("firstName"), "%" + name + "%"));
    }

    public static Specification<AppUser> hasLastName(String name) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.<String>get("lastName"), name));
    }

    public static Specification<AppUser> hasRole(UserState userState) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("state"), userState));
    }


}