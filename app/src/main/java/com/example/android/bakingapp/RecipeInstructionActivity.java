package com.example.android.bakingapp;

import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.android.bakingapp.data.BakingContract;
import com.example.android.bakingapp.ui.RecipeInstructionFragment;

public class RecipeInstructionActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>{

    private String mRecipe;
    private Fragment recipeInstructionFragment;

    public static final int ID_RECIPE_STEPS_LOADER = 156;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_recipe_instruction);

        recipeInstructionFragment = getFragmentManager().findFragmentById(R.id.fragment_recipe_instruction);

        mRecipe = getIntent().getExtras().getString("RecipeName");
        getSupportLoaderManager().restartLoader(ID_RECIPE_STEPS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mRecipe!= null) {
            String[] recipes = {mRecipe};
            switch (id) {
                case ID_RECIPE_STEPS_LOADER:
                    Uri stepsUri = BakingContract.BakingEntry.STEPS_URI;

                    return new CursorLoader(this,
                            stepsUri,
                            RecipeDisplayActivity.RECIPE_STEPS_PROJECTION,
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
        if (recipeInstructionFragment instanceof RecipeInstructionFragment) {
            ((RecipeInstructionFragment) recipeInstructionFragment).setCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (recipeInstructionFragment instanceof RecipeInstructionFragment) {
            ((RecipeInstructionFragment) recipeInstructionFragment).destroyCursor();
        }
    }
}
