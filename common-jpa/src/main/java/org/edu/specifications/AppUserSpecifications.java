package org.edu.specifications;

import org.edu.entity.AppUser;
import org.springframework.data.jpa.domain.Specification;

public class AppUserSpecifications {

    public static Specification<AppUser> hasFirstNameLike(String name){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.<String>get("firstName"), "%" + name + "%"));
    }

    public static Specification<AppUser> hasLastNameLike(String name){
        return((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.<String>get("lastName"), name));
    }


}