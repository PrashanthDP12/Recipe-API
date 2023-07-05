package com.recipe.management.recipes.controller;

import com.recipe.management.recipes.exception.NotFoundException;
import com.recipe.management.recipes.model.RecipeMDB;
import com.recipe.management.recipes.model.Review;
import com.recipe.management.recipes.service.RecipeServiceMDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recipes/v2")
public class RecipeControllerV2 {
    
    private final RecipeServiceMDB recipeService;
    
    @Autowired
    public RecipeControllerV2(RecipeServiceMDB recipeService) {
        this.recipeService = recipeService;
    }
    
    @GetMapping
    public ResponseEntity<List<RecipeMDB>> getAllRecipes() {
        List<RecipeMDB> recipes = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }
    
    @GetMapping("/{recipeKey}")
    public ResponseEntity<RecipeMDB> getRecipeByRecipeKey(@PathVariable("recipeKey") String recipeKey) {
        Optional<RecipeMDB> recipe = recipeService.findRecipeByRecipeKey(recipeKey);
        return recipe.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<RecipeMDB> createRecipe(@RequestBody RecipeMDB recipe) {
        RecipeMDB createdRecipe = recipeService.createRecipe(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
    }
    
    @PutMapping("/{recipeKey}")
    public ResponseEntity<RecipeMDB> updateRecipe(
            @PathVariable("recipeKey") String recipeKey,
            @RequestBody RecipeMDB updatedRecipe) {
        RecipeMDB updated = recipeService.updateRecipeByRecipeKey(recipeKey, updatedRecipe);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{recipeKey}")
    public ResponseEntity<String> deleteRecipe(@PathVariable("recipeKey") String recipeKey) {
        try {
            recipeService.deleteRecipeByRecipeKey(recipeKey);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Recipe deleted successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<RecipeMDB>> searchRecipes(
            @RequestParam(value = "keyword", required = false) String keyword,
            Pageable pageable) {
        Page<RecipeMDB> recipes = recipeService.searchRecipesByRecipeKey(StringUtils.trimWhitespace(keyword), pageable);
        return ResponseEntity.ok(recipes);
    }
    
    @PostMapping("/{recipeKey}/favorite")
    public ResponseEntity<RecipeMDB> markRecipeAsFavorite(@PathVariable("recipeKey") String recipeKey) {
        RecipeMDB markedRecipe = recipeService.markRecipeAsFavoriteByRecipeKey(recipeKey);
        return ResponseEntity.ok(markedRecipe);
    }
    
    @GetMapping("/favorite")
    public ResponseEntity<List<RecipeMDB>> getFavoriteRecipes() {
        List<RecipeMDB> favoriteRecipes = recipeService.getFavoriteRecipesByRecipeKey();
        return ResponseEntity.ok(favoriteRecipes);
    }
    
    @DeleteMapping("/{recipeKey}/favorite")
    public ResponseEntity<RecipeMDB> unmarkRecipeAsFavorite(@PathVariable("recipeKey") String recipeKey) {
        RecipeMDB unmarkedRecipe = recipeService.unmarkRecipeAsFavoriteByRecipeKey(recipeKey);
        return ResponseEntity.ok(unmarkedRecipe);
    }
    
    @PostMapping("/{recipeKey}/reviews")
    public ResponseEntity<Review> addReviewToRecipe(
            @PathVariable("recipeKey") String recipeKey,
            @RequestBody Review review) {
        try {
            Review addedReview = recipeService.addReviewToRecipeByRecipeKey(recipeKey, review);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedReview);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    @GetMapping("/{recipeKey}/reviews")
    public ResponseEntity<List<Review>> getReviewsForRecipe(@PathVariable("recipeKey") String recipeKey) {
        try {
            List<Review> reviews = recipeService.getReviewsForRecipeByRecipeKey(recipeKey);
            return ResponseEntity.ok(reviews);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    @DeleteMapping("/{recipeKey}/reviews/{userId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable("recipeKey") String recipeKey,
            @PathVariable("userId") String userId) {
        try {
            recipeService.deleteReviewByRecipeKeyAndUserId(recipeKey, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Review deleted successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PutMapping("/{recipeKey}/reviews/{reviewId}")
    public ResponseEntity<Review> updateReview(
            @PathVariable("recipeKey") String recipeKey,
            @PathVariable("reviewId") String reviewId,
            @RequestBody Review updatedReview) {
        try {
            Review updated = recipeService.updateReviewByRecipeKeyAndReviewId(recipeKey, reviewId, updatedReview);
            return ResponseEntity.ok(updated);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
