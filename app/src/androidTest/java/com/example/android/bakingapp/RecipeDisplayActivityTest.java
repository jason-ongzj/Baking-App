package com.example.android.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingapp.ui.RecipeDisplayActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class RecipeDisplayActivityTest {
    // Testing for portrait mode

    public static final String description3 = "3. Press the cookie crumb mixture into the prepared " +
            "pie pan and bake for 12 minutes. Let crust cool to room temperature.";

    public static final String description4 = "4. Beat together the nutella, mascarpone, 1 teaspoon of " +
            "salt, and 1 tablespoon of vanilla on medium speed in a stand mixer or high speed with a hand mixer until fluffy.";

    @Rule
    public IntentsTestRule<RecipeDisplayActivity> mRecipeDisplayActivityRule = new IntentsTestRule<>(
            RecipeDisplayActivity.class);

    // Check correct text contents matched
    @Test
    public void clickRecyclerViewItem_OpensRecipeInstructionFragment(){
        onView(withId(R.id.recyclerView_recipeSteps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));

        onView(withId(R.id.StepDescription)).check(matches(withText(description3)));
    }

    // Check text contents on clicking next and previous
    @Test
    public void checkDescriptionOnNextAndPrevious(){
        boolean isTwoPane = mRecipeDisplayActivityRule.getActivity().isTwoPane();
        onView(withId(R.id.recyclerView_recipeSteps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));
        if(!isTwoPane) {
            onView(withId(R.id.Next)).perform(click());
            onView(withId(R.id.StepDescription)).check(matches(withText(description4)));
            onView(withId(R.id.Previous)).perform(click());
            onView(withId(R.id.StepDescription)).check(matches(withText(description3)));
        }
    }

    // Hide Previous button when clicking Previous to show first item
    @Test
    public void checkPrevButtonDisabledOnItemZero_OnClickPrev(){
        boolean isTwoPane = mRecipeDisplayActivityRule.getActivity().isTwoPane();
        onView(withId(R.id.recyclerView_recipeSteps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        if(!isTwoPane) {
            onView(withId(R.id.Previous)).perform(click());
            onView(withId(R.id.Previous)).check(matches(not(isDisplayed())));
        }
    }

    // Hide Next button when clicking Next to show last item
    @Test
    public void checkNextButtonDisabledOnLastItem_OnClickNext(){
        boolean isTwoPane = mRecipeDisplayActivityRule.getActivity().isTwoPane();
        onView(withId(R.id.recyclerView_recipeSteps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(5, click()));
        if(!isTwoPane) {
            onView(withId(R.id.Next)).perform(click());
            onView(withId(R.id.Next)).check(matches(not(isDisplayed())));
        }
    }

    // Hide Previous button on first item selection from recycler view, show Previous button
    // when clicking Next
    @Test
    public void checkPrevButtonDisabledOnItemZeroDisplay_EnabledOnClickNext(){
        boolean isTwoPane = mRecipeDisplayActivityRule.getActivity().isTwoPane();
        onView(withId(R.id.recyclerView_recipeSteps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        if(!isTwoPane) {
            onView(withId(R.id.Previous)).check(matches(not(isDisplayed())));
            onView(withId(R.id.Next)).perform(click());
            onView(withId(R.id.Previous)).check(matches(isDisplayed()));
        }
    }

    // Hide Next button on last item selection from recycler view, show Next button
    // when clicking Previous
    @Test
    public void checkNextButtonDisabledOnLastItemDisplay_EnabledOnClickPrev(){
        boolean isTwoPane = mRecipeDisplayActivityRule.getActivity().isTwoPane();
        onView(withId(R.id.recyclerView_recipeSteps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(6, click()));
        if(!isTwoPane) {
            onView(withId(R.id.Next)).check(matches(not(isDisplayed())));
            onView(withId(R.id.Previous)).perform(click());
            onView(withId(R.id.Next)).check(matches(isDisplayed()));
        }
    }
}
