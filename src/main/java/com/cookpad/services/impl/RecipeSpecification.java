package com.cookpad.services.impl;

import com.cookpad.entities.Recipe;
import com.cookpad.entities.enums.RecipeType;
import com.cookpad.exceptions.RecipeAPIException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class RecipeSpecification {

    public static Specification<Recipe> search(
            String query,
            Long recipeId,
            String recipeName,
            String recipeType,
            Integer prepTime,
            Integer cookingTime,
            Integer serves
    ) {
        return (Root<Recipe> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Main query-based search (multi-field)
            if (query != null && !query.isEmpty()) {
                Predicate orPredicate = cb.disjunction();

                orPredicate = cb.or(orPredicate, cb.like(cb.lower(root.get("recipeName")), "%" + query.toLowerCase() + "%"));

                try {
                    int num = Integer.parseInt(query);
                    orPredicate = cb.or(orPredicate, cb.equal(root.get("prepTime"), num));
                    orPredicate = cb.or(orPredicate, cb.equal(root.get("cookingTime"), num));
                    orPredicate = cb.or(orPredicate, cb.equal(root.get("serves"), num));
                } catch (NumberFormatException ignored) {}

                predicates.add(orPredicate);
            }

            // Specific field filters
            if (recipeId != null) {
                predicates.add(cb.equal(root.get("recipeId"), recipeId));
            }

            if (recipeName != null && !recipeName.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("recipeName")), "%" + recipeName.toLowerCase() + "%"));
            }

            if (recipeType != null && !recipeType.isEmpty()) {
                try {
                    RecipeType typeEnum = RecipeType.valueOf(recipeType.toUpperCase());
                    predicates.add(cb.equal(root.get("recipeType"), typeEnum));
                } catch (IllegalArgumentException e) {
                    throw new RecipeAPIException(HttpStatus.NOT_FOUND, "Invalid recipeType value: " + recipeType);
                }
            }


            if (prepTime != null) {
                predicates.add(cb.equal(root.get("prepTime"), prepTime));
            }

            if (cookingTime != null) {
                predicates.add(cb.equal(root.get("cookingTime"), cookingTime));
            }

            if (serves != null) {
                predicates.add(cb.equal(root.get("serves"), serves));
            }

            // Combine all predicates
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}