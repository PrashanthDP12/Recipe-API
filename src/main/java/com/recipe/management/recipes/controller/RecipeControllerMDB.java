package com.recipe.management.recipes.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.recipe.management.recipes.exception.NotFoundException;
import com.recipe.management.recipes.model.RecipeMDB;
import com.recipe.management.recipes.model.Review;
import com.recipe.management.recipes.service.RecipeServiceMDB;

@RestController
@RequestMapping("/recipes")
public class RecipeControllerMDB {

    private final RecipeServiceMDB recipeService;

    @Autowired
    public RecipeControllerMDB(RecipeServiceMDB recipeService) {
        this.recipeService = recipeService;
    }
    @GetMapping
    public ResponseEntity<List<RecipeMDB>> getAllRecipes() {
        List<RecipeMDB> recipes = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeMDB> getRecipeById(@PathVariable("id") String id) {
        ObjectId objectId = new ObjectId(id);
        RecipeMDB recipe = recipeService.findRecipeById(objectId)
                .orElseThrow(() -> new NotFoundException("Recipe not found"));
        return ResponseEntity.ok(recipe);
    }
    
    @PostMapping
    public ResponseEntity<RecipeMDB> createRecipe(@RequestBody RecipeMDB recipe) {
        RecipeMDB createdRecipe = recipeService.createRecipe(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RecipeMDB> updateRecipe(@PathVariable("id") String id, @RequestBody RecipeMDB updatedRecipe) {
        ObjectId objectId = new ObjectId(id);
        RecipeMDB updated = recipeService.updateRecipe(objectId, updatedRecipe);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            throw new NotFoundException("Recipe not found");
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable("id") String id) {
        ObjectId objectId = new ObjectId(id);
        recipeService.deleteRecipe(objectId);
        return ResponseEntity.ok("Recipe deleted successfully");
    }
    
   
    @GetMapping("/search")
    public ResponseEntity<Page<RecipeMDB>> searchRecipes(@RequestParam("keyword") String keyword,
                                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                                         @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeMDB> recipes = recipeService.searchRecipes(keyword, pageable);
        return ResponseEntity.ok(recipes);
    }
    
    // New endpoint to mark a recipe as favorite
    @PutMapping("/{id}/favorite")
    public ResponseEntity<RecipeMDB> markRecipeAsFavorite(@PathVariable("id") String id) {
        ObjectId objectId = new ObjectId(id);
        RecipeMDB recipe = recipeService.markRecipeAsFavorite(objectId);
        return ResponseEntity.ok(recipe);
    }
    
    // New endpoint to retrieve favorite recipes
    @GetMapping("/favorites")
    public ResponseEntity<List<RecipeMDB>> getFavoriteRecipes() {
        List<RecipeMDB> favoriteRecipes = recipeService.getFavoriteRecipes();
        return ResponseEntity.ok(favoriteRecipes);
    }
    
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<RecipeMDB> unmarkRecipeAsFavorite(@PathVariable("id") String id) {
        ObjectId objectId = new ObjectId(id);
        RecipeMDB recipe = recipeService.unmarkRecipeAsFavorite(objectId);
        return ResponseEntity.ok(recipe);
    }

    
    @PostMapping("/{id}/reviews")
    public ResponseEntity<Review> addReviewToRecipe(@PathVariable("id") ObjectId recipeId, @RequestBody Review review) {
        Review addedReview = recipeService.addReviewToRecipe(recipeId, review);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedReview);
    }
    
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<Review>> getReviewsForRecipe(@PathVariable("id") ObjectId recipeId) {
        List<Review> reviews = recipeService.getReviewsForRecipe(recipeId);
        return ResponseEntity.ok(reviews);
    }
    
    @DeleteMapping("/{id}/reviews")
    public ResponseEntity<String> deleteReview(@PathVariable("recipeId") ObjectId recipeId, @RequestParam("userId") String userId) {
        try {
            recipeService.deleteReview(recipeId, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Review deleted successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/reviews")
    public ResponseEntity<Review> updateReview(
            @PathVariable("id") ObjectId recipeId,
            @RequestParam("userId") String userId,
            @RequestBody Review updatedReview) {
        Review updated = recipeService.updateReview(recipeId, userId, updatedReview);
        return ResponseEntity.ok(updated);
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
