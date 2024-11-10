package com.example.aptechstudentcaredserver.util;

import com.example.aptechstudentcaredserver.entity.User;
import com.example.aptechstudentcaredserver.enums.Status;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> searchStudents(String rollNumber, String fullName, String email) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (rollNumber != null && !rollNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("userDetail").get("rollNumber"), "%" + rollNumber + "%"));
            }

            if (fullName != null && !fullName.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("userDetail").get("fullName"), "%" + fullName + "%"));
            }

            if (email != null && !email.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + email + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
