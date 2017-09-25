package com.example.android.bakingapp;

public class Ingredients {

    private final String quantity;
    private final String measure;
    private final String ingredient;

    public Ingredients(String quantity, String measure, String ingredient){
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }
}
