package dev.dextra.newsapp

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.model.SourceResponse
import dev.dextra.newsapp.api.model.enums.Category
import dev.dextra.newsapp.api.model.enums.Country
import dev.dextra.newsapp.base.BaseInstrumentedTest
import dev.dextra.newsapp.base.FileUtils
import dev.dextra.newsapp.base.TestSuite
import dev.dextra.newsapp.base.mock.endpoint.ResponseHandler
import dev.dextra.newsapp.feature.news.NEWS_ACTIVITY_SOURCE
import dev.dextra.newsapp.feature.news.NewsActivity
import dev.dextra.newsapp.feature.sources.SourcesActivity
import dev.dextra.newsapp.utils.JsonUtils
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.activity_sources.*
import okhttp3.Request
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class NewsActivityInstrumentedTest : BaseInstrumentedTest() {

    val emptyResponse = SourceResponse(ArrayList(), "ok")
    val brazilResponse = SourceResponse(
        listOf(
            Source(
                "cat",
                "BR",
                "Test Brazil Description",
                "1234",
                "PT",
                "Test Brazil",
                "http://www.google.com.br"
            )
        ), "ok"
    )

    fun clickChildViewWithId(id: Int = R.id.source_item): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController, view: View) {
                val v = view.findViewById<View>(id)
                v.performClick()
            }
        }
    }

    @get:Rule
    val activityRule = ActivityTestRule(SourcesActivity::class.java, false, false)

    @get:Rule
    val activityNewsRule = ActivityTestRule(NewsActivity::class.java, false, false)

    @Before
    fun setupTest() {
        //we need to launch the activity here so the MockedEndpointService is set
        activityRule.launchActivity(null)
        Intents.init()
    }

    @Test
    fun testCountrySelectorWithStates() {
        //dynamic mock, BR = customResponse, US = empty response and CA = error response, everything else is the default json
        TestSuite.mock(TestConstants.sourcesURL).body(object : ResponseHandler {
            override fun getResponse(request: Request, path: String): String {
                val jsonData = FileUtils.readJson(path.substring(1) + ".json")!!
                return request.url.queryParameter("country")?.let {
                    when (it) {
                        Country.BR.name.toLowerCase() -> {
                            JsonUtils.toJson(brazilResponse)
                        }
                        Country.US.name.toLowerCase() -> {
                            JsonUtils.toJson(emptyResponse)
                        }
                        Country.CA.name.toLowerCase() -> {
                            throw RuntimeException()
                        }
                        else -> {
                            jsonData
                        }
                    }
                } ?: jsonData
            }
        }).apply()


        waitLoading()

        //select Brazil in the country list
        onView(withId(R.id.country_select)).perform(click())
        onData(equalTo(Country.BR)).inRoot(RootMatchers.isPlatformPopup()).perform(click())

        waitLoading()

        //check if the Sources list is displayed with the correct item and the empty and error states are hidden
        onView(withId(R.id.sources_list)).check(matches(isDisplayed()))
        onView(withId(R.id.error_state)).check(matches(not(isDisplayed())))
        onView(withId(R.id.empty_state)).check(matches(not(isDisplayed())))
        onView(ViewMatchers.withChild(ViewMatchers.withText("Test Brazil"))).check(matches(isDisplayed()))

        // select first source to open news activity
        onView(withId(R.id.sources_list)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        waitLoading()
        // check if there is news on Brazil
        onView(withId(R.id.news_list)).check(matches(isDisplayed()))

        // back to select another country
        pressBack()
        waitLoading()

        activityNewsRule.launchActivity(
            Intent().putExtra(
                NEWS_ACTIVITY_SOURCE, Source(
                    "general",
                    "us",
                    "Your trusted source for breaking news, analysis, exclusive interviews, headlines, and videos at ABCNews.com.",
                    "abc-news",
                    "end",
                    "ABC News",
                    "https://abcnews.go.com"
                )
            )
        )
        waitLoading()

        assert(activityNewsRule.activity.news_list.adapter?.itemCount == 10)
        onView(withId(R.id.news_list))
            .perform(
                activityNewsRule.activity.news_list.adapter?.itemCount?.minus(1)?.let { position ->
                    scrollToPosition<RecyclerView.ViewHolder>(
                        position
                    )
                })

        waitLoading()
        assert(activityNewsRule.activity.news_list.adapter?.itemCount == 20)
        waitLoading()

        pressBack()
        waitLoading()

        //clear the mocks to use just the json files
        TestSuite.clearEndpointMocks()
    }

    @Test
    fun testCategorySelectorWithStates() {
        //dynamic mock, if any category besides ALL is selected, show a custom response
        TestSuite.mock(TestConstants.sourcesURL).body(object : ResponseHandler {
            override fun getResponse(request: Request, path: String): String {
                val jsonData = FileUtils.readJson(path.substring(1) + ".json")!!
                return request.url.queryParameter("category")?.let {
                    if (it == Category.BUSINESS.name.toLowerCase()) JsonUtils.toJson(brazilResponse) else jsonData
                } ?: jsonData
            }
        }).apply()

        waitLoading()

        //select the Business category
        onView(withId(R.id.category_select)).perform(click())
        onData(equalTo(Category.BUSINESS)).inRoot(RootMatchers.isPlatformPopup()).perform(click())

        waitLoading()

        //check if the Sources list is displayed with the correct item and the empty and error states are hidden
        onView(withId(R.id.sources_list)).check(matches(isDisplayed()))
        onView(withId(R.id.error_state)).check(matches(not(isDisplayed())))
        onView(withId(R.id.empty_state)).check(matches(not(isDisplayed())))
        onView(ViewMatchers.withChild(ViewMatchers.withText("Test Brazil"))).check(matches(isDisplayed()))

    }


    @After
    fun clearTest() {
        Intents.release()
    }

}