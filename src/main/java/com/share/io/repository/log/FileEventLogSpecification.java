package com.share.io.repository.log;

import static com.share.io.model.eventlog.FileEventLog_.DYNAMIC_CONTENT;
import static com.share.io.model.eventlog.FileEventLog_.EVENT;
import static com.share.io.model.eventlog.FileEventLog_.TIMESTAMP;
import static com.share.io.model.eventlog.FileEventLog_.USER_NAME;
import static com.share.io.model.notification.Notification_.FILE_ID;
import static com.share.io.model.user.User_.ID;
import static com.share.io.util.SqlUtil.getContainsPredicate;
import com.share.io.model.eventlog.Event;
import com.share.io.model.eventlog.FileEventLog;
import com.share.io.model.eventlog.FileEventLog_;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

public class FileEventLogSpecification {

    public static final String MY_SQl_DATE_TIME_PATTERN = "%Y-%m-%d %H:%i";

    public static Specification<FileEventLog> fileIdEquals(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(FileEventLog_.fileId), id);
    }

    public static Specification<FileEventLog> idEquals(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(FileEventLog_.id), id);
    }

    public static Specification<FileEventLog> userNameContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> getContainsPredicate(criteriaBuilder,
                root.get(FileEventLog_.userName), query);
    }

    public static Specification<FileEventLog> dynamicContentContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> getContainsPredicate(criteriaBuilder,
                root.get(FileEventLog_.dynamicContent), query);
    }

    public static Specification<FileEventLog> event(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(FileEventLog_.event), Event.valueOf(query));
    }

    public static Specification<FileEventLog> timestampContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }

        return (root, criteriaQuery, criteriaBuilder) -> {
            Expression<String> formattedDate = criteriaBuilder.function("DATE_FORMAT",
                    String.class, root.get(FileEventLog_.timestamp), criteriaBuilder.literal(MY_SQl_DATE_TIME_PATTERN));
            return getContainsPredicate(criteriaBuilder, formattedDate, query);
        };
    }

    public static Specification<FileEventLog> sort(String sortField, String direction) {
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

    private static Expression getField(String name, Root<FileEventLog> root) {
        if (name == null) {
            return root.get(FileEventLog_.id);
        }

        switch (name) {
            case ID:
                return root.get(FileEventLog_.id);
            case FILE_ID:
                return root.get(FileEventLog_.fileId);
            case TIMESTAMP:
                return root.get(FileEventLog_.timestamp);
            case USER_NAME:
                return root.get(FileEventLog_.userName);
            case EVENT:
                return root.get(FileEventLog_.event);
            case DYNAMIC_CONTENT:
                return root.get(FileEventLog_.dynamicContent);
            default:
                return root.get(FileEventLog_.id);
        }
    }

    public static Order getOrder(CriteriaBuilder criteriaBuilder, Expression field, String direction) {
        if ("desc".equals(direction)) {
            return criteriaBuilder.desc(field);
        }
        return criteriaBuilder.asc(field);
    }
}
