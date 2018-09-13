package com.ramup.gandrade.pokerclub.login;


import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ramup.gandrade.pokerclub.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ALoginActivityTest {

    private static final String USER = "a";
    private static final String PASS = "aaaaaa";

    private IdlingResource mIdlingResource;

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

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
    public void loginActivityTest() {
        onView(withId(R.id.email)).perform(typeText(USER), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(PASS), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

    }
}
