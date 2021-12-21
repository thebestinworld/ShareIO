package com.share.io.util;

import com.share.io.dto.query.BetweenQuery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlUtil {

    public static final Character ESCAPE_CHARACTER = '\\';

    public static <T, V extends Comparable<? super V>> Specification<T> betweenSpec(SingularAttribute<T, V> attr,
                                                                                    BetweenQuery<V> query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> betweenPredicate(attr, query, criteriaBuilder, root);
    }

    public static <T, V extends Comparable<? super V>> Predicate betweenPredicate(SingularAttribute<T, V> attr,
                                                                                  BetweenQuery<V> query,
                                                                                  CriteriaBuilder cb,
                                                                                  Path<T> root) {
        return cb.and(
                getGreaterPredicate(query.getMin(), cb, root.get(attr), true),
                getLesserPredicate(query.getMax(), cb, root.get(attr), true)
        );

    }

    public static <T extends Comparable<? super T>> Predicate getGreaterPredicate(T valueToCompare,
                                                                                  CriteriaBuilder criteriaBuilder,
                                                                                  Expression<T> field,
                                                                                  boolean inclusive) {
        if (valueToCompare == null) {
            return criteriaBuilder.conjunction();
        }

        if (inclusive) {
            return criteriaBuilder.greaterThanOrEqualTo(field, valueToCompare);
        }

        return criteriaBuilder.greaterThan(field, valueToCompare);
    }

    public static <T extends Comparable<? super T>> Predicate getLesserPredicate(T valueToCompare,
                                                                                 CriteriaBuilder criteriaBuilder,
                                                                                 Expression<T> field,
                                                                                 boolean inclusive) {
        if (valueToCompare == null) {
            return criteriaBuilder.conjunction();
        }

        if (inclusive) {
            return criteriaBuilder.lessThanOrEqualTo(field, valueToCompare);
        }

        return criteriaBuilder.lessThan(field, valueToCompare);
    }

    public static Predicate getContainsPredicate(CriteriaBuilder criteriaBuilder,
                                                 Expression<String> field, String query) {
        if (query == null) {
            return criteriaBuilder.conjunction();
        }

        query = escapeSpecialCharacters(query);

        return criteriaBuilder.like(criteriaBuilder.lower(field), getContainsPattern(query), ESCAPE_CHARACTER);
    }

    public static String escapeSpecialCharacters(String query) {
        return query.replaceAll("[" + Pattern.quote("[]_%\\") + "]",
                Matcher.quoteReplacement(ESCAPE_CHARACTER.toString()) + "$0");
    }

    public static String getContainsPattern(String searchTerm) {
        return (searchTerm.isEmpty()) ? "%" : "%" + searchTerm.toLowerCase() + "%";
    }
}
