package com.recipe.management.recipes.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeMDB {

    @Id
    private ObjectId id;

    private String title;

    private String description;

    private int cookingTime;

    private String ingredients;

    private String method;
    
    private boolean isFavorite;
}

