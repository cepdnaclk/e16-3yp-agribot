package com.example.agribot;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
//import android.support.test.rule.ActivityTestRule;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Pattern;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class LoginUITest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setUp(){
        mActivityRule.getActivity();
    }

    @Test
    public void test1(){
        onView(withId(R.id.errorMessage)).check(matches(not(isDisplayed())));
    }

    @Test
    public void test2(){
        onView(withId(R.id.editTextLoginMail)).perform(typeText("us"));
        onView(withId(R.id.editTextLoginMail)).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextLoginPasswrd)).perform(typeText("abc"));
        onView(withId(R.id.editTextLoginPasswrd)).perform(closeSoftKeyboard());

        onView(withId(R.id.Loginbutton)).perform(click());

        onView(withId(R.id.errorMessage)).check(matches(isDisplayed())).check(matches(withText("BAD INPUT")));

    }

    @Test
    public void test3(){
        onView(withId(R.id.editTextLoginMail)).perform(typeText("us"));
        onView(withId(R.id.editTextLoginMail)).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextLoginPasswrd)).perform(typeText(""));
        onView(withId(R.id.editTextLoginPasswrd)).perform(closeSoftKeyboard());

        onView(withId(R.id.Loginbutton)).perform(click());

        onView(withId(R.id.errorMessage)).check(matches(isDisplayed())).check(matches(withText("EMPTY INPUT")));
    }


    @Test
    public void test4(){
        onView(withId(R.id.editTextLoginMail)).perform(typeText("user1"));
        onView(withId(R.id.editTextLoginMail)).perform(closeSoftKeyboard());
        onView(withId(R.id.editTextLoginPasswrd)).perform(typeText("abc123"));
        onView(withId(R.id.editTextLoginPasswrd)).perform(closeSoftKeyboard());

        onView(withId(R.id.Loginbutton)).perform(click());

        onView(withId(R.id.errorMessage)).check(matches(isDisplayed())).check(matches(withText("Correct Input")));
    }

//    @Test
//    public void test5(){
//
//
//        //ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
//
//        onView(withId(R.id.editTextLoginMail)).perform(typeText("user1"));
//        onView(withId(R.id.editTextLoginMail)).perform(closeSoftKeyboard());
//        onView(withId(R.id.editTextLoginPasswrd)).perform(typeText("abc123"));
//        onView(withId(R.id.editTextLoginPasswrd)).perform(closeSoftKeyboard());
//
//        onView(withId(R.id.Loginbutton)).perform(click());
//
//        onView(withId(R.id.home)).check(matches(isDisplayed()));
//
//
//    }

}
