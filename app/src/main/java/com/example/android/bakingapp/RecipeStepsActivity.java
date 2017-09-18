package com.example.android.bakingapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.android.bakingapp.data.BakingContract;
import com.example.android.bakingapp.ui.RecipeStepsListAdapter;

public class RecipeStepsActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = "RecipeStepsActivity";
    private RecipeStepsListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private static final int ID_RECIPE_STEPS_LOADER = 156;

    private static final String[] RECIPE_STEPS_PROJECTION = {
            BakingContract.BakingEntry.RECIPE_STEP,
            BakingContract.BakingEntry.COLUMN_SHORT_DESCRIPTION,
            BakingContract.BakingEntry.COLUMN_DESCRIPTION,
            BakingContract.BakingEntry.COLUMN_VIDEO_URL,
            BakingContract.BakingEntry.COLUMN_THUMBNAIL_URL,
            BakingContract.BakingEntry.COLUMN_STEP_ID
    };

    public static final int INDEX_RECIPE_STEP = 0;
    public static final int INDEX_SHORT_DESCRIPTION = 1;
    public static final int INDEX_DESCRIPTION = 2;
    public static final int INDEX_VIDEO_URL = 3;
    public static final int INDEX_THUMBNAIL_URL = 4;
    public static final int INDEX_STEP_ID = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_steps);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_recipeSteps);
        mAdapter = new RecipeStepsListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case ID_RECIPE_STEPS_LOADER:
                Uri stepsUri = BakingContract.BakingEntry.STEPS_URI;

                return new CursorLoader(this,
                        stepsUri,
                        RECIPE_STEPS_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader not implemeneted: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.setCursor(data);
        Log.d(TAG, "onLoadFinished: RecipeListAdapter cursor" + mAdapter.getCursor().getCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setCursor(null);
    }
}
