package com.example.android.bakingapp.ui;

import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.BakingContract;

/**
 * Created by Ben on 9/17/2017.
 */

public class RecipeStepsListFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = "RecipeStepsListFragment";
    private RecipeStepsListAdapter mAdapter;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.recipe_steps, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_recipeSteps);
        mAdapter = new RecipeStepsListAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case ID_RECIPE_STEPS_LOADER:
                Uri stepsUri = BakingContract.BakingEntry.STEPS_URI;

                return new CursorLoader(getActivity(),
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
