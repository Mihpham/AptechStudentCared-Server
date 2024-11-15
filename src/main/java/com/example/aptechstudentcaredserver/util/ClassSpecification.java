package com.example.aptechstudentcaredserver.util;

import com.example.aptechstudentcaredserver.entity.Class;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ClassSpecification {
    public static Specification<Class> searchClass(String className) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (className != null && !className.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("className"), "%" + className + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
