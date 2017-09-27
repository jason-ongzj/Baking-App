package com.example.android.bakingapp.ui;

import android.app.Fragment;
import android.content.Context;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeDisplayFragment extends Fragment implements
    RecipeDisplayAdapter.RecipeInstructionOnClickHandler{

    @Nullable
    @BindView(R.id.recyclerView_recipeSteps) RecyclerView recyclerView;
    private Unbinder unbinder;

    DataCommunications mCallback;
    private boolean mTwoPane;
    private RecipeDisplayAdapter mAdapter;

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
        unbinder = ButterKnife.bind(this, rootView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new RecipeDisplayAdapter(this);
        mAdapter.setContext(getActivity());
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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

        } else {

            mCallback.setStringData(description, uriString, thumbnailUri, mAdapter.getRecipe());
            mCallback.setAdapterData(position, mAdapter.getItemCount());
            mCallback.getInstructionFragment().updateFragmentViews();

        }
    }

    public void setAdapterCursor(Cursor cursor){
        mAdapter.setCursor(cursor);
    }

    public void setRecipe(String recipe) {mAdapter.setRecipe(recipe);}
}
