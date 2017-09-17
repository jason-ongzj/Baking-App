package com.example.android.bakingapp.utils;

import android.util.Log;

import com.example.android.bakingapp.BakingRecipe;
import com.example.android.bakingapp.Ingredients;
import com.example.android.bakingapp.RecipeSteps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BakingDbUtils {

    public static final String TAG = "BakingDBUtils";

    public static ArrayList<BakingRecipe> getRecipesFromJSON(String response) throws JSONException {
        JSONArray jsonArray = new JSONArray(response);
        ArrayList<BakingRecipe> bakingRecipes = new ArrayList<BakingRecipe>();
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
            Log.d(TAG, recipe.image.toString());
//            Log.d(TAG, ingredientsJSON);
//            Log.d(TAG, recipe.ingredientsList.get(1).getQuantity());
//            Log.d(TAG, recipe.ingredientsList.get(1).getIngredient());
//            Log.d(TAG, recipe.ingredientsList.get(1).getMeasure());
//            Log.d(TAG, recipe.stepsList.get(1).getDescription());
//            Log.d(TAG, recipe.stepsList.get(1).getShortDescription());
//            Log.d(TAG, Integer.toString(recipe.stepsList.get(1).getStepID()));
//            Log.d(TAG, recipe.stepsList.get(1).getVideoURL());
//            Log.d(TAG, recipe.stepsList.get(1).getThumbnailURL());
            bakingRecipes.add(i, recipe);
        }
        return bakingRecipes;
    }

    public static void getIngredientsFromJSON(String response, BakingRecipe recipe) throws JSONException{
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

    public static void getStepsFromJSON(String response, BakingRecipe recipe) throws JSONException{
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
}
