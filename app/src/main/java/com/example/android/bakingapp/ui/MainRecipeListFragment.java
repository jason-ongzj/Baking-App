package com.example.android.bakingapp.ui;

import android.app.Fragment;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.android.bakingapp.BakingRecipe;
import com.example.android.bakingapp.Ingredients;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeSteps;
import com.example.android.bakingapp.data.BakingContract;
import com.example.android.bakingapp.utils.BakingDbUtils;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainRecipeListFragment extends Fragment {

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @Nullable
    @BindView(R.id.recipe_list_view)
    ListView mListView;
    @Nullable
    @BindView(R.id.recipe_grid_view)
    GridView mGridView;

    private MainRecipeListAdapter mAdapter;
    private ArrayList<Bitmap> mBitmapArrayList = new ArrayList<Bitmap>();
    private ArrayList<String> mRecipeNameList = new ArrayList<String>();
    private ArrayList<String> mBitmapFiles = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_activity_main, container, false);
        ButterKnife.bind(this, rootView);
        setRetainInstance(true);

        mAdapter = new MainRecipeListAdapter(getActivity());
        mAdapter.setInflater(inflater);
        if (savedInstanceState == null) {
            new GetRecipesTask().execute();
        } else {
            mRecipeNameList = savedInstanceState.getStringArrayList("RecipeNameList");
            mBitmapFiles = savedInstanceState.getStringArrayList("BitmapFiles");
            mAdapter.setRecipes(mRecipeNameList, mBitmapFiles);
        }

        if (mListView != null)
            mListView.setAdapter(mAdapter);
        else mGridView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("RecipeNameList", mRecipeNameList);
        outState.putStringArrayList("BitmapFiles", mBitmapFiles);
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

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        protected void onPostExecute(String response) {
            new AddDataToDb().execute(response);
            new GetBitmapArray().execute(response);
        }
    }

    private class AddDataToDb extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... response) {
            ArrayList<BakingRecipe> recipesList = new ArrayList<BakingRecipe>();

            getActivity().getContentResolver().delete(BakingContract.BakingEntry.RECIPE_URI, null, null);
            getActivity().getContentResolver().delete(BakingContract.BakingEntry.INGREDIENTS_URI, null, null);
            getActivity().getContentResolver().delete(BakingContract.BakingEntry.STEPS_URI, null, null);

            try {
                recipesList = BakingDbUtils.getRecipesFromJSON(response[0]);

                for (int i = 0; i < recipesList.size(); i++) {
                    BakingRecipe recipe = recipesList.get(i);
                    addRecipesToDb(recipe);
                    addIngredientsToDb(recipe);
                    addStepsToDb(recipe);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void addRecipesToDb(BakingRecipe recipe) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BakingContract.BakingEntry._ID, recipe.id);
            contentValues.put(BakingContract.BakingEntry.COLUMN_RECIPE_NAME, recipe.name);
            contentValues.put(BakingContract.BakingEntry.COLUMN_SERVINGS, recipe.servings);
            contentValues.put(BakingContract.BakingEntry.COLUMN_IMAGE, recipe.image);
            getActivity().getContentResolver().insert(BakingContract.BakingEntry.RECIPE_URI, contentValues);
        }

        private void addIngredientsToDb(BakingRecipe recipe) {
            ArrayList<Ingredients> ingredientsList = recipe.ingredientsList;
            for (int i = 0; i < ingredientsList.size(); i++) {
                Ingredients ingredient = ingredientsList.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(BakingContract.BakingEntry.RECIPE_INGREDIENT, recipe.name
                        + "_" + ingredient.getIngredient());
                contentValues.put(BakingContract.BakingEntry.COLUMN_RECIPE_NAME, recipe.name);
                contentValues.put(BakingContract.BakingEntry.COLUMN_INGREDIENTS, ingredient.getIngredient());
                contentValues.put(BakingContract.BakingEntry.COLUMN_QUANTITY, ingredient.getQuantity());
                contentValues.put(BakingContract.BakingEntry.COLUMN_MEASURE, ingredient.getMeasure());
                getActivity().getContentResolver().insert(BakingContract.BakingEntry.INGREDIENTS_URI, contentValues);
            }
        }

        private void addStepsToDb(BakingRecipe recipe) {
            ArrayList<RecipeSteps> stepsList = recipe.stepsList;
            for (int i = 0; i < stepsList.size(); i++) {
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
                getActivity().getContentResolver().insert(BakingContract.BakingEntry.STEPS_URI, contentValues);
            }
        }
    }

    private class GetBitmapArray extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... response) {
            ArrayList<BakingRecipe> recipesList = new ArrayList<BakingRecipe>();
            try {
                recipesList = BakingDbUtils.getRecipesFromJSON(response[0]);
                mRecipeNameList = BakingDbUtils.getRecipeNamesFromJSON(response[0]);
                for (int i = 0; i < recipesList.size(); i++) {
                    BakingRecipe recipe = recipesList.get(i);
                    RecipeSteps step = recipe.stepsList.get(recipe.stepsList.size() - 1);
                    String videoURL = (!step.getVideoURL().equals("")) ?
                            step.getVideoURL() : step.getThumbnailURL();

                    MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                    metadataRetriever.setDataSource(videoURL, new HashMap<String, String>());
                    Bitmap thumbnail = metadataRetriever.getFrameAtTime(0);
                    mBitmapArrayList.add(i, thumbnail);
                    metadataRetriever.release();

                    try {
                        String filename = "newImage" + Integer.toString(i) + ".png";
                        File f = new File(getActivity().getCacheDir(), filename);
                        f.createNewFile();

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        thumbnail.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                        byte[] bitmapdata = bos.toByteArray();

                        FileOutputStream fos = new FileOutputStream(f);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                        mBitmapFiles.add(i, f.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.setRecipes(mRecipeNameList, mBitmapFiles);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}