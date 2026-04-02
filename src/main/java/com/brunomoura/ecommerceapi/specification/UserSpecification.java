package com.brunomoura.ecommerceapi.specification;

import com.brunomoura.ecommerceapi.domain.user.User;
import com.brunomoura.ecommerceapi.domain.user.User_;
import com.brunomoura.ecommerceapi.enums.UserRole;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class UserSpecification {

    public static Specification<User> hasId(Long id) {

        if (id == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(User_.id), id);
    }

    public static Specification<User> hasName(String name) {

        if (name == null || name.isBlank()) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(User_.name)),
                "%" + name.trim().toLowerCase() + "%");
    }

    public static Specification<User> hasCpf(String cpf) {

        if (cpf == null || cpf.isBlank()) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(User_.cpf), cpf.trim());
    }

    public static Specification<User> hasEmail(String email) {

        if (email == null || email.isBlank()) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(User_.email), email.trim());
    }

    public static Specification<User> hasRole(UserRole role) {

        if (role == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(User_.role), role);
    }

    public static Specification<User> wasDeletedAt(Instant initialDateOfDelete, Instant finalDateOfDelete) {

        if (initialDateOfDelete == null && finalDateOfDelete == null) {
            return null;
        }

        if (finalDateOfDelete == null) {

            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(User_.deletedAt),
                    initialDateOfDelete);
        }

        if (initialDateOfDelete == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(User_.deletedAt),
                    finalDateOfDelete);
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(User_.deletedAt), initialDateOfDelete,
                finalDateOfDelete);
    }

    public static Specification<User> wasCreatedAt(Instant initialDateOfCreation, Instant finalDateOfCreation) {

        if (initialDateOfCreation == null && finalDateOfCreation == null) {
            return null;
        }

        if (finalDateOfCreation == null) {

            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(User_.createdAt),
                    initialDateOfCreation);
        }

        if (initialDateOfCreation == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(User_.createdAt),
                    finalDateOfCreation);
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(User_.createdAt), initialDateOfCreation,
                finalDateOfCreation);
    }

}
