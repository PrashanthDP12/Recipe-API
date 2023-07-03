package com.recipe.management.recipes.service;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.recipe.management.recipes.exception.NotFoundException;
import com.recipe.management.recipes.model.RecipeMDB;
import com.recipe.management.recipes.model.Review;
import com.recipe.management.recipes.repository.RecipeRepositoryMDB;

@Service
public class RecipeServiceMDB {
	
	
    private final RecipeRepositoryMDB recipeRepository;

    @Autowired
    public RecipeServiceMDB(RecipeRepositoryMDB recipeRepository) {
        this.recipeRepository = recipeRepository;
    }
    
    @Autowired
    private MongoTemplate mongoTemplate;

    
    public List<RecipeMDB> getAllRecipes() {
        return recipeRepository.findAll();
    }


    public Optional<RecipeMDB> findRecipeById(ObjectId id) {
        return recipeRepository.findRecipeById(id);
    }
    
    public RecipeMDB createRecipe(RecipeMDB recipe) {
        return recipeRepository.save(recipe);
    }
    
    public RecipeMDB updateRecipe(ObjectId id, RecipeMDB updatedRecipe) {
        Optional<RecipeMDB> existingRecipe = recipeRepository.findRecipeById(id);
        if (existingRecipe.isPresent()) {
            RecipeMDB recipe = existingRecipe.get();
            recipe.setTitle(updatedRecipe.getTitle());
            recipe.setDescription(updatedRecipe.getDescription());
            recipe.setCookingTime(updatedRecipe.getCookingTime());
            recipe.setIngredients(updatedRecipe.getIngredients());
            recipe.setMethod(updatedRecipe.getMethod());
            return recipeRepository.save(recipe);
        }
        return null; // or throw an exception indicating recipe not found
    }
    
    public void deleteRecipe(ObjectId id) {
        recipeRepository.deleteById(id);
    }
    
    public Page<RecipeMDB> searchRecipes(String keyword, Pageable pageable) {
        // Create a Query object
        Query query = new Query();

        // Create a Criteria object to build the search conditions
        Criteria criteria = new Criteria();

        // Add the search conditions for title, ingredients, and description fields
        if (StringUtils.hasText(keyword)) {
            criteria.orOperator(
                Criteria.where("title").regex(keyword, "i"),
                Criteria.where("ingredients").regex(keyword, "i"),
                Criteria.where("description").regex(keyword, "i")
            );
        }

        // Add the criteria to the query
        query.addCriteria(criteria);

        // Set the pagination parameters
        query.with(pageable);

        // Use the MongoTemplate to execute the search query with pagination
        List<RecipeMDB> recipes = mongoTemplate.find(query, RecipeMDB.class);

        // Get the total count of recipes matching the search criteria
        long totalCount = mongoTemplate.count(query, RecipeMDB.class);

        // Create a Page object with the recipes and pagination information
        return new PageImpl<>(recipes, pageable, totalCount);
    }

    public RecipeMDB markRecipeAsFavorite(ObjectId id) {
        RecipeMDB recipe = recipeRepository.findById(id).orElseThrow(() -> new NotFoundException("Recipe not found"));
        recipe.setFavorite(true);
        return recipeRepository.save(recipe);
    }
    
    public List<RecipeMDB> getFavoriteRecipes() {
        return recipeRepository.findByIsFavorite(true);
    }
    
    public RecipeMDB unmarkRecipeAsFavorite(ObjectId id) {
        RecipeMDB recipe = recipeRepository.findById(id).orElseThrow(() -> new NotFoundException("Recipe not found"));
        recipe.setFavorite(false);
        return recipeRepository.save(recipe);
    }

    
    public Review addReviewToRecipe(ObjectId recipeId, Review review) {
        RecipeMDB recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new NotFoundException("Recipe not found"));
        recipe.getReviews().add(review);
        recipe.calculateAverageRating();
        recipeRepository.save(recipe);
        return review;
    }
    
    public List<Review> getReviewsForRecipe(ObjectId recipeId) {
        RecipeMDB recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new NotFoundException("Recipe not found"));
        return recipe.getReviews();
    }
    
    public void deleteReview(ObjectId recipeId, String userId) {
        RecipeMDB recipe = findRecipeById(recipeId).orElseThrow(() -> new NotFoundException("Recipe not found"));

        Optional<Review> reviewToDelete = recipe.getReviews().stream()
                .filter(review -> review.getUserId().equals(userId.trim())) // Compare userId with trimmed userId from the request
                .findFirst();

        if (reviewToDelete.isPresent()) {
            recipe.getReviews().remove(reviewToDelete.get());
            recipeRepository.save(recipe); // Update the recipe in the database
        } else {
            throw new NotFoundException("Review not found");
        }
    }
    
    public Review updateReview(ObjectId recipeId, String reviewId, Review updatedReview) {
        RecipeMDB recipe = findRecipeById(recipeId).orElseThrow(() -> new NotFoundException("Recipe not found"));
        
        Optional<Review> reviewToUpdate = recipe.getReviews().stream()
                .filter(review -> review.getUserId().equals(reviewId))
                .findFirst();

        if (reviewToUpdate.isPresent()) {
            Review review = reviewToUpdate.get();
            review.setRating(updatedReview.getRating());
            review.setComment(updatedReview.getComment());
            recipe.calculateAverageRating();
            recipeRepository.save(recipe);
            return review;
        } else {
            throw new NotFoundException("Review not found");
        }
    }


}

