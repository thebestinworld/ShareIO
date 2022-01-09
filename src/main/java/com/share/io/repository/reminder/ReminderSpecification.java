package com.share.io.repository.reminder;

import com.share.io.model.reminder.Reminder;
import com.share.io.model.reminder.Reminder_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import static com.share.io.model.reminder.Reminder_.MESSAGE;
import static com.share.io.model.reminder.Reminder_.PAST_DUE;
import static com.share.io.model.reminder.Reminder_.TIME;
import static com.share.io.model.user.User_.ID;
import static com.share.io.util.SqlUtil.getContainsPredicate;

public class ReminderSpecification {


    public static final String MY_SQl_DATE_TIME_PATTERN = "%Y-%m-%d %H:%i";

    public static Specification<Reminder> userIdEquals(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Reminder_.userId), id);
    }

    public static Specification<Reminder> idEquals(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Reminder_.id), id);
    }

    public static Specification<Reminder> messageContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> getContainsPredicate(criteriaBuilder,
                root.get(Reminder_.message), query);
    }

    public static Specification<Reminder> pastDueIs(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> {
            if ("YES".equals(query)) {
                return criteriaBuilder.isTrue(root.get(Reminder_.pastDue));
            } else {
                return criteriaBuilder.isFalse(root.get(Reminder_.pastDue));
            }
        };
    }

    public static Specification<Reminder> timeContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }

        return (root, criteriaQuery, criteriaBuilder) -> {
            Expression<String> formattedDate = criteriaBuilder.function("DATE_FORMAT",
                    String.class, root.get(Reminder_.time), criteriaBuilder.literal(MY_SQl_DATE_TIME_PATTERN));
            return getContainsPredicate(criteriaBuilder, formattedDate, query);
        };
    }

    public static Specification<Reminder> sort(String sortField, String direction) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Expression field = getField(sortField, root);

            criteriaQuery.orderBy(
                    criteriaBuilder.asc(criteriaBuilder.selectCase().when(criteriaBuilder.isNull(field),
                            1).otherwise(0)),
                    getOrder(criteriaBuilder, field, direction)
            );

            return criteriaBuilder.conjunction();
        };
    }

    private static Expression getField(String name, Root<Reminder> root) {

        if (name == null) {
            return root.get(Reminder_.id);
        }

        switch (name) {
            case ID:
                return root.get(Reminder_.id);
            case MESSAGE:
                return root.get(Reminder_.message);
            case TIME:
                return root.get(Reminder_.time);
            case PAST_DUE:
                return root.get(Reminder_.pastDue);
            default:
                return root.get(Reminder_.id);
        }
    }

    public static Order getOrder(CriteriaBuilder criteriaBuilder, Expression field, String direction) {
        if ("desc".equals(direction)) {
            return criteriaBuilder.desc(field);
        }
        return criteriaBuilder.asc(field);
    }
}
