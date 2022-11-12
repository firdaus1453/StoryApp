package com.firdaus1453.storyapp.presentation.main

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.presentation.detail.DetailActivity
import com.firdaus1453.storyapp.presentation.map.MapActivity
import com.firdaus1453.storyapp.util.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loadStories_Success() {
        Espresso.onView(withId(R.id.rv_stories))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.rv_stories))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(10))
    }

    @Test
    fun loadDetailStory_Success() {
        Intents.init()
        Espresso.onView(withId(R.id.rv_stories)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                ViewActions.click()
            )
        )
        Intents.intended(hasComponent(DetailActivity::class.java.name))
        Espresso.onView(withId(R.id.tv_first_letter_name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Intents.release()
    }

    @Test
    fun loadMap_Success() {
        Intents.init()
        Espresso.onView(withId(R.id.iv_map)).perform(ViewActions.click())

        Intents.intended(hasComponent(MapActivity::class.java.name))
        Espresso.onView(withId(R.id.map))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Intents.release()
    }
}