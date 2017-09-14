package com.example.android.bakingapp;

import java.util.ArrayList;

/**
 * Created by Ben on 9/14/2017.
 */

public class BakingRecipe {

    public String name;
    public int id;
    public ArrayList<Ingredients> ingredientsList;
    public ArrayList<RecipeSteps> stepsList;
    public int servings;
    public String image;

    public BakingRecipe(){};

}
