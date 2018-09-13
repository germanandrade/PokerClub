package com.ramup.gandrade.pokerclub.login;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ramup.gandrade.pokerclub.Main2Activity;
import com.ramup.gandrade.pokerclub.R;
import com.ramup.gandrade.pokerclub.game.views.GameActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class GameActivityTest {


    private IdlingResource mIdlingResource;

    @Rule
    public ActivityTestRule<GameActivity> mActivityTestRule = new ActivityTestRule<>(GameActivity.class);

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().register(mIdlingResource);
        }
    }

    @Test
    public void getStartedTest() {
        onView(withId(R.id.buyButton)).perform(click());

    }
}
