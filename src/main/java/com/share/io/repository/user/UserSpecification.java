package com.share.io.repository.user;

import com.share.io.model.user.User;
import com.share.io.model.user.User_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import static com.share.io.util.SqlUtil.getContainsPredicate;

public class UserSpecification {


    public static Specification<User> usernameContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> getContainsPredicate(criteriaBuilder,
                root.get(User_.username), query);
    }

    public static Specification<User> emailContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> getContainsPredicate(criteriaBuilder,
                root.get(User_.email), query);
    }
}
