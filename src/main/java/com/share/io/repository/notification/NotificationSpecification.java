package com.share.io.repository.notification;

import static com.share.io.model.notification.Notification_.IS_READ;
import static com.share.io.model.notification.Notification_.RECEIVED_DATE;
import static com.share.io.model.reminder.Reminder_.MESSAGE;
import static com.share.io.model.user.User_.ID;
import static com.share.io.util.SqlUtil.getContainsPredicate;
import com.share.io.model.notification.Notification;
import com.share.io.model.notification.Notification_;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

public class NotificationSpecification {


    public static final String MY_SQl_DATE_TIME_PATTERN = "%Y-%m-%d %H:%i";

    public static Specification<Notification> userIdEquals(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Notification_.userId), id);
    }

    public static Specification<Notification> idEquals(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Notification_.id), id);
    }

    public static Specification<Notification> messageContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> getContainsPredicate(criteriaBuilder,
                root.get(Notification_.message), query);
    }

    public static Specification<Notification> isRead(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> {
            if ("YES".equals(query)) {
                return criteriaBuilder.isTrue(root.get(Notification_.isRead));
            } else {
                return criteriaBuilder.isFalse(root.get(Notification_.isRead));
            }
        };
    }

    public static Specification<Notification> receivedDateContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }

        return (root, criteriaQuery, criteriaBuilder) -> {
            Expression<String> formattedDate = criteriaBuilder.function("DATE_FORMAT",
                    String.class, root.get(Notification_.receivedDate), criteriaBuilder.literal(MY_SQl_DATE_TIME_PATTERN));
            return getContainsPredicate(criteriaBuilder, formattedDate, query);
        };
    }

    public static Specification<Notification> sort(String sortField, String direction) {
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

    private static Expression getField(String name, Root<Notification> root) {

        if (name == null) {
            return root.get(Notification_.id);
        }

        switch (name) {
            case ID:
                return root.get(Notification_.id);
            case MESSAGE:
                return root.get(Notification_.message);
            case RECEIVED_DATE:
                return root.get(Notification_.receivedDate);
            case IS_READ:
                return root.get(Notification_.isRead);
            default:
                return root.get(Notification_.id);
        }
    }

    public static Order getOrder(CriteriaBuilder criteriaBuilder, Expression field, String direction) {
        if ("desc".equals(direction)) {
            return criteriaBuilder.desc(field);
        }
        return criteriaBuilder.asc(field);
    }
}
