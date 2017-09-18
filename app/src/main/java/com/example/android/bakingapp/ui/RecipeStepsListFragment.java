package com.example.android.bakingapp.ui;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;

/**
 * Created by Ben on 9/17/2017.
 */

public class RecipeStepsListFragment extends Fragment {

    private RecipeStepsListAdapter mAdapter;

    public RecipeStepsListFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_steps, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_recipeSteps);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new RecipeStepsListAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public void setAdapterCursor(Cursor cursor){
        mAdapter.setCursor(cursor);
    }
}
