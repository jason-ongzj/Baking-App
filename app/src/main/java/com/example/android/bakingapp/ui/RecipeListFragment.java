package com.example.android.bakingapp.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.BakingContract;

/**
 * Created by Ben on 9/16/2017.
 */

public class RecipeListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>{
    public static final String TAG = "RecipeListFragment";

    private static final int ID_RECIPE_LOADER = 123;

    public RecipeListFragment(){};
    private RecipeListAdapter mAdapter;

    private static final String[] RECIPE_PROJECTION = {
            BakingContract.BakingEntry._ID,
            BakingContract.BakingEntry.COLUMN_RECIPE_NAME,
            BakingContract.BakingEntry.COLUMN_SERVINGS,
            BakingContract.BakingEntry.COLUMN_IMAGE
    };

    public static final int INDEX_RECIPE_ID = 0;
    public static final int INDEX_RECIPE_NAME = 1;
    public static final int INDEX_RECIPE_SERVINGS = 2;
    public static final int INDEX_RECIPE_IMAGE= 3;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.recipe_list_view);
        mAdapter = new RecipeListAdapter(getActivity());
        mAdapter.setInflater(inflater);
        listView.setAdapter(mAdapter);


        getActivity().getSupportLoaderManager().initLoader(ID_RECIPE_LOADER, null, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case ID_RECIPE_LOADER:
                Uri recipeUri = BakingContract.BakingEntry.RECIPE_URI;

                return new CursorLoader(getActivity(),
                        recipeUri,
                        RECIPE_PROJECTION,
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
