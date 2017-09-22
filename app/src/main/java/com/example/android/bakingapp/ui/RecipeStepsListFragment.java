package com.example.android.bakingapp.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeInstructionActivity;

public class RecipeStepsListFragment extends Fragment implements
    RecipeStepsListAdapter.RecipeInstructionOnClickHandler{

    DataCommunications mCallback;

    public static final String TAG = "RecipeStepsListFragment";
    private boolean mTwoPane;

    private RecipeStepsListAdapter mAdapter;

    public RecipeStepsListFragment(){
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mCallback = (DataCommunications) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement DataCommunication");
        }
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

    public void isTwoPane(boolean twoPane){
        mTwoPane = twoPane;
    }

    @Override
    public void onInstructionsClicked(String description, String uriString,
                                      String thumbnailUri, int position) {
        if (!mTwoPane) {
            Intent intent = new Intent(getActivity(), RecipeInstructionActivity.class);
            intent.putExtra("InstructionSet", new String[]{description, uriString, thumbnailUri});
            intent.putExtra("ItemCount", mAdapter.getItemCount());
            intent.putExtra("ItemPosition", position);
            intent.putExtra("RecipeName", mAdapter.getRecipe());
            startActivity(intent);
            mAdapter.closeCursor();
            Log.d(TAG, "onInstructionsClicked: 2 pane false");
        } else {
            mCallback.setStringData(description, uriString, thumbnailUri);
            mCallback.setAdapterData(position, mAdapter.getItemCount());
            mCallback.getInstructionFragment().updateFragmentViews();
        }
    }

    public void setAdapterCursor(Cursor cursor){
        mAdapter.setCursor(cursor);
    }

    public void setRecipe(String recipe) {mAdapter.setRecipe(recipe);}
}
