package com.example.android.bakingapp.utils;

import com.example.android.bakingapp.BakingRecipe;
import com.example.android.bakingapp.Ingredients;
import com.example.android.bakingapp.RecipeSteps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BakingAppDbUtils {

    public static ArrayList<BakingRecipe> getRecipesFromJSON(String response) throws JSONException {
        JSONArray jsonArray = new JSONArray(response);
        ArrayList<BakingRecipe> bakingRecipes = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++){

            BakingRecipe recipe = new BakingRecipe();
            JSONObject recipeJSON = jsonArray.getJSONObject(i);

            recipe.id = recipeJSON.getInt("id");
            recipe.name = recipeJSON.getString("name");
            recipe.servings = recipeJSON.getInt("servings");
            recipe.image = recipeJSON.getString("image");

            String ingredientsJSON = recipeJSON.getString("ingredients");
            String stepsJSON = recipeJSON.getString("steps");

            getIngredientsFromJSON(ingredientsJSON, recipe);
            getStepsFromJSON(stepsJSON, recipe);

            bakingRecipes.add(i, recipe);

        }

        return bakingRecipes;
    }

    public static ArrayList<String> getRecipeNamesFromJSON(String response) throws JSONException{
        JSONArray jsonArray = new JSONArray(response);
        ArrayList<String> recipeNames = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject recipeJSON = jsonArray.getJSONObject(i);
            recipeNames.add(i, recipeJSON.getString("name"));
        }

        return recipeNames;
    }

    private static void getIngredientsFromJSON(String response, BakingRecipe recipe) throws JSONException{
        JSONArray jsonArray = new JSONArray(response);
        ArrayList<Ingredients> ingredientsList = new ArrayList<Ingredients>();

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject ingredientJSON = jsonArray.getJSONObject(i);

            String quantity = ingredientJSON.getString("quantity");
            String measure = ingredientJSON.getString("measure");
            String ingredient = ingredientJSON.getString("ingredient");

            ingredientsList.add(i, new Ingredients(quantity, measure, ingredient));
        }

        recipe.ingredientsList = ingredientsList;
    }

    private static void getStepsFromJSON(String response, BakingRecipe recipe) throws JSONException{
        JSONArray jsonArray = new JSONArray(response);
        ArrayList<RecipeSteps> stepsList = new ArrayList<RecipeSteps>();

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject stepJSON = jsonArray.getJSONObject(i);
            int stepId = stepJSON.getInt("id");
            String shortDescription = stepJSON.getString("shortDescription");
            String description = stepJSON.getString("description");
            String videoURL = stepJSON.getString("videoURL");
            String thumbnailURL = stepJSON.getString("thumbnailURL");
            RecipeSteps recipeStep = new RecipeSteps(stepId, shortDescription, description,
                    videoURL, thumbnailURL);

            stepsList.add(i, recipeStep);
        }

        recipe.stepsList = stepsList;
    }

    public static ArrayList<String> getImagesFromJSON(String response) throws JSONException{
        JSONArray jsonArray = new JSONArray(response);
        ArrayList<String> recipeNames = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject recipeJSON = jsonArray.getJSONObject(i);
            recipeNames.add(i, recipeJSON.getString("image"));
        }

        return recipeNames;
    }
}
