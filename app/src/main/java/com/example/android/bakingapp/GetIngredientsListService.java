package com.example.android.bakingapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.example.android.bakingapp.data.BakingContract;

public class GetIngredientsListService extends IntentService {

    public static final String[] INGREDIENTS_PROJECTION = {
            BakingContract.BakingEntry.RECIPE_INGREDIENT,
            BakingContract.BakingEntry.COLUMN_RECIPE_NAME,
            BakingContract.BakingEntry.COLUMN_INGREDIENTS,
            BakingContract.BakingEntry.COLUMN_QUANTITY,
            BakingContract.BakingEntry.COLUMN_MEASURE
    };

    public static final int INDEX_RECIPE_INGREDIENT = 0;
    public static final int INDEX_RECIPE_NAME = 1;
    public static final int INDEX_INGREDIENT = 2;
    public static final int INDEX_QUANTITY = 3;
    public static final int INDEX_MEASURE = 4;

    public static final String RECIPE_ID = "com.example.android.bakingapp.extra.RECIPE_ID";

    public GetIngredientsListService() { super("GetIngredientsListService");}

    public static void retrieveIngredientsList(Context context, String recipe){
        Intent intent = new Intent(context, GetIngredientsListService.class);
        intent.putExtra(RECIPE_ID, recipe);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            String recipe = intent.getStringExtra(RECIPE_ID);
            String[] ingredientsList = handleIngredientsList(intent.getStringExtra(recipe));
        }
    }

    private String[] handleIngredientsList(String recipe){
        Cursor cursor = getContentResolver().query(
                BakingContract.BakingEntry.INGREDIENTS_URI,
                new String[] {BakingContract.BakingEntry.COLUMN_RECIPE_NAME},
                "name=?",
                new String[]{recipe},
                null);
        int count = cursor.getCount();
        String[] ingredientsList = new String[count];
        for(int i = 0; i < count; i++){
            cursor.moveToPosition(i);
            String ingredient = cursor.getString(INDEX_INGREDIENT);
            String quantity = cursor.getString(INDEX_QUANTITY);
            String measure = cursor.getString(INDEX_MEASURE);
            String ingredientsQtyMeasure =
                    ingredient + ": " + quantity + measure;
            ingredientsList[i] = ingredientsQtyMeasure;
        }
        cursor.close();
        return ingredientsList;
    }
}
