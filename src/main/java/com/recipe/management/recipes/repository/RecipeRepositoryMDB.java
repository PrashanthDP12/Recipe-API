package com.recipe.management.recipes.repository;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.recipe.management.recipes.model.RecipeMDB;

@Repository
public interface RecipeRepositoryMDB extends MongoRepository<RecipeMDB, ObjectId> {
    Optional<RecipeMDB> findRecipeById(ObjectId id);
    List<RecipeMDB> findByIsFavorite(boolean isFavorite);
}