package com.recipe.management.recipes.exception;
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}