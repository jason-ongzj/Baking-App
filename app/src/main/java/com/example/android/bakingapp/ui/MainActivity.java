package com.example.android.bakingapp.ui;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ListView;

import com.example.android.bakingapp.BakingRecipe;
import com.example.android.bakingapp.Ingredients;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeSteps;
import com.example.android.bakingapp.data.BakingContract;
import com.example.android.bakingapp.data.BakingDbHelper;
import com.example.android.bakingapp.utils.BakingDbUtils;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recipe_list_view) ListView mListView;

    private RecipeListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipes);
        ButterKnife.bind(this);

        BakingDbHelper dbHelper = new BakingDbHelper(this);
        dbHelper.getWritableDatabase();
        new GetRecipesTask().execute();

        mAdapter = new RecipeListAdapter(MainActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        mAdapter.setInflater(inflater);
        mListView.setAdapter(mAdapter);
    }

    private class GetRecipesTask extends AsyncTask<Void, Void, String> {
        private static final String TAG = "AsyncTask";
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: ");
            String urlString = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod(REQUEST_METHOD);
                urlConnection.setReadTimeout(READ_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);

                urlConnection.connect();

                InputStreamReader streamReader = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                streamReader.close();

                return stringBuilder.toString();

            } catch(IOException e) {
                e.printStackTrace();
            }
            finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        protected void onPostExecute(String response) {
            try{
                new AddDataToDb().execute(response);
                mAdapter.setRecipes(BakingDbUtils.getRecipeNamesFromJSON(response));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private class AddDataToDb extends AsyncTask<String, Void, ArrayList<BakingRecipe>>{
        @Override
        protected ArrayList<BakingRecipe> doInBackground(String... response) {
            ArrayList<BakingRecipe> recipesList = new ArrayList<BakingRecipe>();

            getContentResolver().delete(BakingContract.BakingEntry.RECIPE_URI, null, null);
            getContentResolver().delete(BakingContract.BakingEntry.INGREDIENTS_URI, null, null);
            getContentResolver().delete(BakingContract.BakingEntry.STEPS_URI, null, null);

            try{
                recipesList = BakingDbUtils.getRecipesFromJSON(response[0]);

                for (int i = 0; i < recipesList.size(); i++){
                    BakingRecipe recipe = recipesList.get(i);
                    addRecipesToDb(recipe);
                    addIngredientsToDb(recipe);
                    addStepsToDb(recipe);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        // Implement hide circular loader
        @Override
        protected void onPostExecute(ArrayList<BakingRecipe> recipesList) {
            super.onPostExecute(recipesList);
//            recipesList.get(1).
        }

        private void addRecipesToDb(BakingRecipe recipe){
            ContentValues contentValues = new ContentValues();
            contentValues.put(BakingContract.BakingEntry._ID, recipe.id);
            contentValues.put(BakingContract.BakingEntry.COLUMN_RECIPE_NAME, recipe.name);
            contentValues.put(BakingContract.BakingEntry.COLUMN_SERVINGS, recipe.servings);
            contentValues.put(BakingContract.BakingEntry.COLUMN_IMAGE, recipe.image);
            getContentResolver().insert(BakingContract.BakingEntry.RECIPE_URI, contentValues);
        }

        private void addIngredientsToDb(BakingRecipe recipe){
            ArrayList<Ingredients> ingredientsList = recipe.ingredientsList;
            for(int i = 0; i < ingredientsList.size(); i++){
                Ingredients ingredient = ingredientsList.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(BakingContract.BakingEntry.RECIPE_INGREDIENT, recipe.name
                        + "_" + ingredient.getIngredient());
                contentValues.put(BakingContract.BakingEntry.COLUMN_RECIPE_NAME, recipe.name);
                contentValues.put(BakingContract.BakingEntry.COLUMN_INGREDIENTS, ingredient.getIngredient());
                contentValues.put(BakingContract.BakingEntry.COLUMN_QUANTITY, ingredient.getQuantity());
                contentValues.put(BakingContract.BakingEntry.COLUMN_MEASURE, ingredient.getMeasure());
                getContentResolver().insert(BakingContract.BakingEntry.INGREDIENTS_URI, contentValues);
            }
        }

        private void addStepsToDb(BakingRecipe recipe){
            ArrayList<RecipeSteps> stepsList = recipe.stepsList;
            for(int i = 0; i < stepsList.size(); i++){
                RecipeSteps step = stepsList.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(BakingContract.BakingEntry.RECIPE_STEP, recipe.name
                        + "_" + Integer.toString(step.getStepID()));
                contentValues.put(BakingContract.BakingEntry.COLUMN_RECIPE_NAME, recipe.name);
                contentValues.put(BakingContract.BakingEntry.COLUMN_SHORT_DESCRIPTION, step.getShortDescription());
                contentValues.put(BakingContract.BakingEntry.COLUMN_DESCRIPTION, step.getDescription());
                contentValues.put(BakingContract.BakingEntry.COLUMN_VIDEO_URL, step.getVideoURL());
                contentValues.put(BakingContract.BakingEntry.COLUMN_THUMBNAIL_URL, step.getThumbnailURL());
                contentValues.put(BakingContract.BakingEntry.COLUMN_STEP_ID, step.getStepID());
                getContentResolver().insert(BakingContract.BakingEntry.STEPS_URI, contentValues);
            }
        }
    }
}
