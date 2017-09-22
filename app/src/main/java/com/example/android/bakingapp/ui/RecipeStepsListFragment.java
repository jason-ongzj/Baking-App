package com.example.android.bakingapp.ui;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeDisplayActivity;
import com.example.android.bakingapp.RecipeInstructionActivity;

public class RecipeStepsListFragment extends Fragment implements
    RecipeStepsListAdapter.RecipeInstructionOnClickHandler{

    public static final String TAG = "RecipeStepsListFragment";

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

        mAdapter = new RecipeStepsListAdapter(this);
        mAdapter.setContext(getActivity());
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onInstructionsClicked(String description, String uriString,
                                      String thumbnailUri, int position) {
        if (!RecipeDisplayActivity.mTwoPane) {
            Intent intent = new Intent(getActivity(), RecipeInstructionActivity.class);
            intent.putExtra("InstructionSet", new String[]{description, uriString, thumbnailUri});
            intent.putExtra("ItemCount", mAdapter.getItemCount());
            intent.putExtra("ItemPosition", position);
            intent.putExtra("RecipeName", mAdapter.getRecipe());
            startActivity(intent);
            mAdapter.closeCursor();
        } else {
            Bundle bundle = new Bundle();
        }
//        Log.d(TAG, "onInstructionsClicked: " + Integer.toString(mAdapter.getItemCount()));
    }

    public void setAdapterCursor(Cursor cursor){
        mAdapter.setCursor(cursor);
    }

    public void setRecipe(String recipe) {mAdapter.setRecipe(recipe);}
}
