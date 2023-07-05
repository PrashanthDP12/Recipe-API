package com.recipe.management.recipes.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeMDB {

    @Id
    private ObjectId id;

    private String title;

    private String description;

    private int cookingTime;

    private String ingredients;

    private String method;

    private boolean isFavorite;

    private List<Review> reviews;

    private double averageRating;

    private String recipeKey;

    {
        reviews = new ArrayList<>();
        recipeKey = generateUniqueRecipeKey();
    }

    public RecipeMDB(String title, String description, int cookingTime, String ingredients, String method, boolean isFavorite) {
        this.title = title;
        this.description = description;
        this.cookingTime = cookingTime;
        this.ingredients = ingredients;
        this.method = method;
        this.isFavorite = isFavorite;
    }

    public void calculateAverageRating() {
        if (reviews != null && !reviews.isEmpty()) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Review review : reviews) {
                sum = sum.add(review.getRating());
            }
            averageRating = sum.divide(BigDecimal.valueOf(reviews.size()), 2, RoundingMode.HALF_UP).doubleValue();
        } else {
            averageRating = 0.0;
        }
    }

    private String generateUniqueRecipeKey() {
        return UUID.randomUUID().toString();
    }
}

