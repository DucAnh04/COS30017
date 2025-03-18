package com.example.assignment_2

import android.os.IBinder
import android.view.WindowManager
import androidx.test.espresso.Root
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BorrowActivityTest {

    @Test
    fun testBorrowSuccess() {
        ActivityScenario.launch(BorrowActivity::class.java)

        // Enter valid quantity
        onView(withId(R.id.quantityEditText)).perform(typeText("1"), closeSoftKeyboard())

        // Click confirm borrow button
        onView(withId(R.id.confirmBorrowButton)).perform(click())

        // Check for success toast (using ToastMatcher if implemented)
        onView(withText(containsString("borrowed successfully"))).inRoot(ToastMatcher()).check(matches(isDisplayed()))
    }

    @Test
    fun testCreditLimitExceeded() {
        ActivityScenario.launch(BorrowActivity::class.java)

        // Enter quantity to exceed credit limit
        onView(withId(R.id.quantityEditText)).perform(typeText("100"), closeSoftKeyboard())

        // Click confirm borrow button
        onView(withId(R.id.confirmBorrowButton)).perform(click())

        // Check for credit limit exceeded message
        onView(withText(containsString("Credit limit exceeded"))).inRoot(ToastMatcher()).check(matches(isDisplayed()))
    }

    @Test
    fun testInvalidQuantity() {
        ActivityScenario.launch(BorrowActivity::class.java)

        // Enter invalid quantity
        onView(withId(R.id.quantityEditText)).perform(typeText("0"), closeSoftKeyboard())

        // Click confirm borrow button
        onView(withId(R.id.confirmBorrowButton)).perform(click())

        // Check for invalid quantity message
        onView(withText(containsString("Please enter a valid quantity"))).inRoot(ToastMatcher()).check(matches(isDisplayed()))
    }

    @Test
    fun testCancelBorrow() {
        ActivityScenario.launch(BorrowActivity::class.java)

        // Click cancel button
        onView(withId(R.id.cancelBorrowButton)).perform(click())

        // Check for cancellation toast
        onView(withText(containsString("Borrowing canceled"))).inRoot(ToastMatcher()).check(matches(isDisplayed()))
    }
}
class ToastMatcher : TypeSafeMatcher<Root>() {
    override fun describeTo(description: Description) {
        description.appendText("is a Toast")
    }

    override fun matchesSafely(root: Root): Boolean {
        val type = root.windowLayoutParams.get().type
        if (type == WindowManager.LayoutParams.TYPE_TOAST) {
            val windowToken = root.decorView.windowToken
            val appToken = root.decorView.applicationWindowToken
            return windowToken === appToken
        }
        return false
    }
}