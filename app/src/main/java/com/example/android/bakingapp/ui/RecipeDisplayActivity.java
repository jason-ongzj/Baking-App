package com.example.android.bakingapp.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.R2;
import com.example.android.bakingapp.data.BakingContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDisplayActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        DataCommunications{
    public static final String TAG = "RecipeDisplayActivity";

    @Nullable
    @BindView(R2.id.recipe_info_container) FrameLayout mRecipeInfoContainer;

    private static final int ID_RECIPE_STEPS_LOADER = 156;

    private boolean mTwoPane;
    private String recipeName;
    private Fragment recipeDisplayFragment;
    private RecipeInstructionFragment recipeInstructionFragment;

    private String[] mRecipeData;
    private int[] mAdapterData;

    public static final String[] RECIPE_STEPS_PROJECTION = {
            BakingContract.BakingEntry.RECIPE_STEP,
            BakingContract.BakingEntry.COLUMN_RECIPE_NAME,
            BakingContract.BakingEntry.COLUMN_SHORT_DESCRIPTION,
            BakingContract.BakingEntry.COLUMN_DESCRIPTION,
            BakingContract.BakingEntry.COLUMN_VIDEO_URL,
            BakingContract.BakingEntry.COLUMN_THUMBNAIL_URL,
            BakingContract.BakingEntry.COLUMN_STEP_ID
    };

    public static final int INDEX_RECIPE_STEP = 0;
    public static final int INDEX_RECIPE_NAME = 1;
    public static final int INDEX_SHORT_DESCRIPTION = 2;
    public static final int INDEX_DESCRIPTION = 3;
    public static final int INDEX_VIDEO_URL = 4;
    public static final int INDEX_THUMBNAIL_URL = 5;
    public static final int INDEX_STEP_ID = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_display);
        ButterKnife.bind(this);

        // Null condition for testing purposes
        if(getIntent().getExtras() == null){
            recipeName = "Nutella Pie";
        } else {
            recipeName = getIntent().getExtras().getString("recipe");
        }

        recipeDisplayFragment = getFragmentManager().findFragmentById(R.id.fragment_recipe_display);

        // Implement 2-pane mode
        if (mRecipeInfoContainer != null){
            mTwoPane = true;
            ((RecipeDisplayFragment) recipeDisplayFragment).isTwoPane(true);
            if (savedInstanceState == null) {

                FragmentManager fragmentManager = getFragmentManager();

                recipeInstructionFragment = new RecipeInstructionFragment();

                fragmentManager.beginTransaction()
                        .add(R.id.recipe_info_container, recipeInstructionFragment)
                        .commit();

            } else {

                recipeInstructionFragment = (RecipeInstructionFragment) getFragmentManager().getFragment(
                        savedInstanceState, "RecipeInstructionFragment");
                mTwoPane = savedInstanceState.getBoolean("mTwoPane");

            }
        } else {
            mTwoPane = false;
            ((RecipeDisplayFragment) recipeDisplayFragment).isTwoPane(false);
        }

        getSupportLoaderManager().initLoader(ID_RECIPE_STEPS_LOADER, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mTwoPane) {
            getFragmentManager().putFragment(outState,
                    "RecipeInstructionFragment", recipeInstructionFragment);
            outState.putBoolean("mTwoPane", mTwoPane);
        }
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
        if (recipeDisplayFragment instanceof RecipeDisplayFragment) {
            ((RecipeDisplayFragment) recipeDisplayFragment).setAdapterCursor(data);
            ((RecipeDisplayFragment) recipeDisplayFragment).setRecipe(recipeName);
        }
        if (recipeInstructionFragment != null){
            recipeInstructionFragment.setCursor(data);
        }
    }

    public boolean isTwoPane(){
        return mTwoPane;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (recipeDisplayFragment instanceof RecipeDisplayFragment) {
            ((RecipeDisplayFragment) recipeDisplayFragment).setAdapterCursor(null);
        }
        if (recipeInstructionFragment != null){
            recipeInstructionFragment.destroyCursor();
        }
    }

    @Override
    public String[] getStringData() {
        return mRecipeData;
    }

    @Override
    public void setStringData(String description, String videoURL, String thumbnailURL, String recipe) {
        mRecipeData = new String[] {description, videoURL, thumbnailURL, recipe};
    }

    @Override
    public int[] getAdapterData() {
        return mAdapterData;
    }

    @Override
    public void setAdapterData(int position, int adapterCount) {
        mAdapterData = new int[]{position, adapterCount};
    }

    // Only applicable in a 2-pane mode
    @Override
    public RecipeInstructionFragment getInstructionFragment() {
        return recipeInstructionFragment;
    }
}
