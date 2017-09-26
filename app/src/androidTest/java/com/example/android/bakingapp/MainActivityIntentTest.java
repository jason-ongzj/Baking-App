package com.example.android.bakingapp;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingapp.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class MainActivityIntentTest {

    private static final String INTENT_RECIPE = "recipe";
    private static final String recipe = "Nutella Pie";
    private static final String RECIPE_INTRODUCTION = "Recipe Introduction";

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(
            MainActivity.class);

    @Test
    public void clickListViewItem_OpenRecipeDisplayActivity(){

        onData(anything()).inAdapterView(withId(R.id.recipe_list_view)).atPosition(0).perform(click());

        // Check recipe name has been passed to next activity
        intended(hasExtra(INTENT_RECIPE, recipe));

        // Check output is being correctly displayed
        onView(withId(R.id.recyclerView_recipeSteps))
                .check(matches(hasDescendant(withText(RECIPE_INTRODUCTION))));
    }
}
