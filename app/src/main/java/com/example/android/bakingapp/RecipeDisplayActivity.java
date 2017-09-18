package com.example.android.bakingapp;

import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.bakingapp.data.BakingContract;
import com.example.android.bakingapp.ui.RecipeStepsListFragment;

public class RecipeDisplayActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String TAG = "RecipeDisplayActivity";
    private String recipeName;
    private Fragment recipeDisplayFragment;
    private static final int ID_RECIPE_STEPS_LOADER = 156;

    private static final String[] RECIPE_STEPS_PROJECTION = {
            BakingContract.BakingEntry.RECIPE_STEP,
            BakingContract.BakingEntry.COLUMN_RECIPE_NAME,
            BakingContract.BakingEntry.COLUMN_SHORT_DESCRIPTION,
            BakingContract.BakingEntry.COLUMN_DESCRIPTION,
            BakingContract.BakingEntry.COLUMN_VIDEO_URL,
            BakingContract.BakingEntry.COLUMN_THUMBNAIL_URL,
            BakingContract.BakingEntry.COLUMN_STEP_ID
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_information_display);
        recipeName = getIntent().getExtras().getString("recipe");
        Log.d(TAG, "onCreate: " + recipeName);
        recipeDisplayFragment = getFragmentManager().findFragmentById(R.id.fragment_recipe_steps);
        getSupportLoaderManager().initLoader(ID_RECIPE_STEPS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(recipeName != null) {
            String[] recipes = {recipeName};
            switch (id) {
                case ID_RECIPE_STEPS_LOADER:
                    Uri stepsUri = BakingContract.BakingEntry.STEPS_URI;

                    return new CursorLoader(this,
                            stepsUri,
                            RECIPE_STEPS_PROJECTION,
                            "name=?",
                            recipes,
                            null);
                default:
                    throw new RuntimeException("Loader not implemented: " + id);

            }
        }
        else return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (recipeDisplayFragment instanceof RecipeStepsListFragment) {
            ((RecipeStepsListFragment) recipeDisplayFragment).setAdapterCursor(data);
        }
//        Log.d(TAG, "onLoadFinished: RecipeListAdapter cursor" + mAdapter.getCursor().getCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (recipeDisplayFragment instanceof RecipeStepsListFragment)
            ((RecipeStepsListFragment) recipeDisplayFragment).setAdapterCursor(null);
    }
}
