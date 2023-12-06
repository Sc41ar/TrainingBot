package org.edu.specifications;

import org.edu.entity.AppUser;
import org.edu.entity.AuthUserQueue;
import org.springframework.data.jpa.domain.Specification;

public class AuthUserQueueSpecifications {
    public static Specification<AuthUserQueue> hasUser(AppUser appUser){
        return (root, query, criteriaBuilder) -> (
                criteriaBuilder.equal(root.get("appUser"), appUser)
        );
    }
}
