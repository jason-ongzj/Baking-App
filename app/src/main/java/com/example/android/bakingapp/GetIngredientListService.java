package com.example.android.bakingapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.bakingapp.data.BakingContract;

import static com.google.android.exoplayer2.mediacodec.MediaCodecInfo.TAG;

public class GetIngredientListService extends IntentService {

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

    private static int recipeID = 3;

    public static final String GET_PREVIOUS_INGREDIENT_LIST = "GetPreviousIngredientList";
    public static final String GET_NEXT_INGREDIENT_LIST = "GetNextIngredientList";
    public static final String GET_INGREDIENT_LIST = "GetIngredientList";
    public static final String RECIPE_ID = "com.example.android.bakingapp.extra.RECIPE_ID";

    public GetIngredientListService() { super("GetIngredientsListService");}

    public static void retrieveIngredientsList(Context context){
        Intent intent = new Intent(context, GetIngredientListService.class);
        intent.setAction(GET_INGREDIENT_LIST);
        intent.putExtra(RECIPE_ID, recipeID);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            switch (intent.getAction()){
                case GET_INGREDIENT_LIST:
                    recipeID = intent.getIntExtra(RECIPE_ID, 0);
                    String ingredientsList = handleIngredientsList(queryRecipe(recipeID));
                    handleActionUpdateIngredientsListWidget();
                    Log.d(TAG, "onHandleIntent: " + ingredientsList);
                    break;
                case GET_PREVIOUS_INGREDIENT_LIST:
                    if(recipeID != 1)
                        recipeID--;
                    else recipeID = 4;
                    handleActionUpdateIngredientsListWidget();
                    break;
                case GET_NEXT_INGREDIENT_LIST:
                    if(recipeID != 4)
                        recipeID++;
                    else recipeID = 1;
                    handleActionUpdateIngredientsListWidget();
                    break;
            }
        }
    }

    private String handleIngredientsList(String recipe){
        Cursor cursor = getContentResolver().query(
                BakingContract.BakingEntry.INGREDIENTS_URI,
                null,
                "name=?",
                new String[]{recipe},
                null);
        int count = cursor.getCount();
        String ingredientsList = "";
        for(int i = 0; i < count; i++){
            cursor.moveToPosition(i);
            String ingredient = cursor.getString(INDEX_INGREDIENT);
            String quantity = cursor.getString(INDEX_QUANTITY);
            String measure = cursor.getString(INDEX_MEASURE);
            String ingredientsQtyMeasure =
                    ingredient + ": " + quantity + measure + "\n\n";
            ingredientsList += ingredientsQtyMeasure;
        }
        cursor.close();
        return ingredientsList;
    }

    private String queryRecipe(int recipeID){
        Uri uri = BakingContract.BakingEntry.RECIPE_URI.buildUpon()
                .appendPath(Integer.toString(recipeID))
                .build();
        Cursor cursor = getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);
        String recipe = "";
        if(cursor != null) {
            cursor.moveToFirst();
            // 1 is the index for recipe name in RECIPES_TABLE
            recipe = cursor.getString(1);
            cursor.close();
        }
        return recipe;
    }

    private void handleActionUpdateIngredientsListWidget(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName
                (this, BakingAppWidgetProvider.class));
        String recipeName = queryRecipe(recipeID);
        BakingAppWidgetProvider.updateIngredientsList(this, appWidgetManager, appWidgetIds,
                handleIngredientsList(recipeName), recipeName);
    }
}
