package com.share.io.repository.file;

import com.share.io.model.file.File;
import com.share.io.model.file.FileType;
import com.share.io.model.file.File_;
import com.share.io.model.user.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.Collection;

import static com.share.io.model.user.User_.ID;
import static com.share.io.model.user.User_.SHARED_FILES;
import static com.share.io.util.SqlUtil.getContainsPredicate;

public class FileSpecification {


    public static final String SQl_DATE_TIME_PATTERN = "MMM dd, yyyy, hh:mm tt";

    public static Specification<File> uploaderIdEquals(Long userId) {
        if (ObjectUtils.isEmpty(userId)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(File_.uploader), userId);
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

    public static Specification<File> uploadDateContains(String query) {
        if (ObjectUtils.isEmpty(query)) {
            return Specification.where(null);
        }
        return (root, criteriaQuery, criteriaBuilder) -> {
            Expression<String> formattedDate = criteriaBuilder.function("FORMAT",
                    String.class, root.get(File_.uploadDate), criteriaBuilder.literal(SQl_DATE_TIME_PATTERN));
            return getContainsPredicate(criteriaBuilder, formattedDate, query);
        };
    }
}
