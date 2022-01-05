package com.share.io.repository.file;

import com.share.io.model.file.File;
import com.share.io.model.file.FileType;
import com.share.io.model.file.File_;
import com.share.io.model.user.User;
import com.share.io.model.user.User_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import static com.share.io.model.file.File_.CONTENT_TYPE;
import static com.share.io.model.file.File_.DESCRIPTION;
import static com.share.io.model.file.File_.EXTENSION;
import static com.share.io.model.file.File_.FILE_TYPE;
import static com.share.io.model.file.File_.NAME;
import static com.share.io.model.file.File_.ORIGINAL_NAME;
import static com.share.io.model.file.File_.UPDATE_DATE;
import static com.share.io.model.file.File_.UPLOADER;
import static com.share.io.model.file.File_.UPLOAD_DATE;
import static com.share.io.model.file.File_.VERSION;
import static com.share.io.model.user.User_.ID;
import static com.share.io.model.user.User_.SHARED_FILES;
import static com.share.io.util.SqlUtil.getContainsPredicate;

public class FileSpecification {


    public static final String MY_SQl_DATE_TIME_PATTERN = "%Y-%m-%d %T.%f";

    public static Specification<File> uploaderIdEquals(Long userId) {
        if (ObjectUtils.isEmpty(userId)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(File_.uploader), userId);
    }

    public static Specification<File> uploaderNameContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) ->
                getContainsPredicate(criteriaBuilder,
                        root.get(File_.uploader).get(User_.username), query);
    }

    public static Specification<File> sharedUsersContain(Long userId) {
        if (ObjectUtils.isEmpty(userId)) {
            return Specification.where(null);
        }
        return (root, query, cb) -> {
            query.distinct(true);
            Subquery<User> ownerSubQuery = query.subquery(User.class);
            Root<User> owner = ownerSubQuery.from(User.class);
            Expression<Collection<File>> userSharedFiles = owner.get(SHARED_FILES);
            ownerSubQuery.select(owner);
            ownerSubQuery.where(cb.equal(owner.get(ID), userId), cb.isMember(root, userSharedFiles));
            return cb.exists(ownerSubQuery);
        };
    }

    public static Specification<File> originalNameContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> getContainsPredicate(criteriaBuilder,
                root.get(File_.originalName), query);
    }

    public static Specification<File> nameContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> getContainsPredicate(criteriaBuilder,
                root.get(File_.name), query);
    }

    public static Specification<File> idEquals(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(File_.id), id);
    }

    public static Specification<File> descriptionContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> getContainsPredicate(criteriaBuilder,
                root.get(File_.description), query);
    }

    public static Specification<File> fileType(FileType type) {
        if (ObjectUtils.isEmpty(type)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(File_.fileType), type);
    }


    public static Specification<File> contentTypeContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> getContainsPredicate(criteriaBuilder,
                root.get(File_.contentType), query);
    }

    public static Specification<File> extensionContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> getContainsPredicate(criteriaBuilder,
                root.get(File_.extension), query);
    }

    public static Specification<File> version(Long version) {
        if (ObjectUtils.isEmpty(version)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(File_.version), version);
    }

    public static Specification<File> uploadDateContains(LocalDate uploadDate) {
        if (ObjectUtils.isEmpty(uploadDate)) {
            return Specification.where(null);
        }
        String query = uploadDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return (root, criteriaQuery, criteriaBuilder) -> {
            Expression<String> formattedDate = criteriaBuilder.function("DATE_FORMAT",
                    String.class, root.get(File_.uploadDate), criteriaBuilder.literal(MY_SQl_DATE_TIME_PATTERN));
            return getContainsPredicate(criteriaBuilder, formattedDate, query);
        };
    }

    public static Specification<File> updateDateContains(LocalDate updateDate) {
        if (ObjectUtils.isEmpty(updateDate)) {
            return Specification.where(null);
        }
        String query = updateDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return (root, criteriaQuery, criteriaBuilder) -> {
            Expression<String> formattedDate = criteriaBuilder.function("DATE_FORMAT",
                    String.class, root.get(File_.updateDate), criteriaBuilder.literal(MY_SQl_DATE_TIME_PATTERN));
            return getContainsPredicate(criteriaBuilder, formattedDate, query);
        };
    }

    public static Specification<File> sort(String sortField, String direction) {
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

    private static Expression getField(String name, Root<File> root) {

        if (name == null) {
            return root.get(File_.id);
        }

        switch (name) {
            case ID:
                return root.get(File_.id);
            case NAME:
                return root.get(File_.name);
            case ORIGINAL_NAME:
                return root.get(File_.originalName);
            case DESCRIPTION:
                return root.get(File_.description);
            case FILE_TYPE:
                return root.get(File_.fileType);
            case EXTENSION:
                return root.get(File_.extension);
            case CONTENT_TYPE:
                return root.get(File_.contentType);
            case VERSION:
                return root.get(File_.version);
            case UPDATE_DATE:
                return root.get(File_.updateDate);
            case UPLOAD_DATE:
                return root.get(File_.uploadDate);
            case UPLOADER:
                return root.join(File_.uploader, JoinType.LEFT).get(User_.username);
            default:
                return root.get(File_.id);
        }
    }

    public static Order getOrder(CriteriaBuilder criteriaBuilder, Expression field, String direction) {
        switch (direction) {
            case "asc":
                return criteriaBuilder.asc(field);
            case "desc":
                return criteriaBuilder.desc(field);
            default:
                return criteriaBuilder.asc(field);
        }
    }
}
