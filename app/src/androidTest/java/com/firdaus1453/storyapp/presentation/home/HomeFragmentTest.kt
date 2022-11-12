package com.firdaus1453.storyapp.presentation.home

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.data.remote.ApiConfig
import com.firdaus1453.storyapp.util.EspressoIdlingResource
import com.firdaus1453.storyapp.util.JsonConverter
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class HomeFragmentTest {

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun getStories_Success() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        launchFragmentInContainer<HomeFragment>(themeResId = R.style.Theme_StoryApp).onFragment {
            navController.setGraph(R.navigation.mobile_navigation)
            Navigation.setViewNavController(it.requireView(), navController)
        }

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.rv_stories))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        onView(ViewMatchers.withText("coba"))
            .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))

        onView(withId(R.id.rv_stories))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    ViewMatchers.hasDescendant(ViewMatchers.withText("TESTING"))
                )
            )
    }

    @Test
    fun getStories_Error() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        launchFragmentInContainer<HomeFragment>(themeResId = R.style.Theme_StoryApp).onFragment {
            navController.setGraph(R.navigation.mobile_navigation)
            Navigation.setViewNavController(it.requireView(), navController)
        }

        val mockResponse = MockResponse()
            .setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.tv_title_info))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}