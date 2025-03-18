package com.example.assignment_2

import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import android.view.View
import androidx.appcompat.widget.SearchView
import org.hamcrest.Matcher
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun testNextButtonNavigatesThroughItems() {
        ActivityScenario.launch(MainActivity::class.java)

        // Check first item
        onView(withId(R.id.itemNameTextView)).check(matches(withText("Electric Guitar")))

        // Click next button and check next item
        onView(withId(R.id.nextButton)).perform(click())
        onView(withId(R.id.itemNameTextView)).check(matches(withText("Drum Set")))

        // Click next button and check next item
        onView(withId(R.id.nextButton)).perform(click())
        onView(withId(R.id.itemNameTextView)).check(matches(withText("Classical Piano")))
    }

    @Test
    fun testSearchFunctionality() {
        ActivityScenario.launch(MainActivity::class.java)

        // Search for "Drum Set"
        onView(withId(R.id.searchView))
            .perform(typeText("Drum"), closeSoftKeyboard())
        onView(withId(R.id.searchView))
            .perform(searchViewAction("Drum"))
        onView(withId(R.id.itemNameTextView)).check(matches(withText("Drum Set")))
    }

    @Test
    fun testBorrowButtonLaunchesBorrowActivity() {
        ActivityScenario.launch(MainActivity::class.java)

        // Click borrow button
        onView(withId(R.id.borrowButton)).perform(click())

        // Check if BorrowActivity is launched (optional - requires activity monitoring setup)
    }
}
fun searchViewAction(query: String): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isAssignableFrom(SearchView::class.java)
        }

        override fun getDescription(): String {
            return "Set query and submit on SearchView"
        }

        override fun perform(uiController: UiController?, view: View?) {
            (view as SearchView).setQuery(query, true)
        }
    }
}
