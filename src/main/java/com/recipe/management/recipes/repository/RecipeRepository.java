package com.recipe.management.recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recipe.management.recipes.model.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}