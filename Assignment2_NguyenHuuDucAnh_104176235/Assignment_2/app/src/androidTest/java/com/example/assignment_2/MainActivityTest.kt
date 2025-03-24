package com.example.assignment_2

import android.app.Activity
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var testRentalItems: List<RentalItem>

    @Before
    fun setUp() {
        // Initialize test data that matches what we expect in MainActivity
        testRentalItems = listOf(
            RentalItem(
                name = "Electric Guitar",
                imageResId = R.drawable.eguitar,
                rating = 4.5f,
                pricePerMonth = 100,
                features = listOf("6 Strings", "Solid Body", "Sunburst Finish")
            ),
            RentalItem(
                name = "Drum Set",
                imageResId = R.drawable.drum,
                rating = 4.0f,
                pricePerMonth = 150,
                features = listOf("5 Pieces", "Cymbals Included", "Double Bass Pedal")
            ),
            RentalItem(
                name = "Classical Piano",
                imageResId = R.drawable.piano,
                rating = 4.0f,
                pricePerMonth = 350,
                features = listOf("88 Keys", "Weighted Action", "Rosewood Finish")
            )
        )

        // Initialize Intents
        Intents.init()
    }

    @After
    fun tearDown() {
        // Release Intents
        Intents.release()
    }

    /**
     * Test that the initial item is displayed correctly
     */
    @Test
    fun testInitialItemDisplay() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Check that initial item is displayed correctly
            onView(withId(R.id.itemNameTextView))
                .check(matches(withText("Electric Guitar")))

            onView(withId(R.id.itemPriceTextView))
                .check(matches(withText("100 credits/month")))

            onView(withId(R.id.itemRatingBar))
                .check(matches(isDisplayed()))

            onView(withId(R.id.itemImageView))
                .check(matches(isDisplayed()))
        }
    }

    /**
     * Test that the next button cycles through items
     */
    @Test
    fun testNextButtonCycling() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Initial item should be "Electric Guitar"
            onView(withId(R.id.itemNameTextView))
                .check(matches(withText("Electric Guitar")))

            // Click next button to move to "Drum Set"
            onView(withId(R.id.nextButton))
                .perform(click())

            onView(withId(R.id.itemNameTextView))
                .check(matches(withText("Drum Set")))
            onView(withId(R.id.itemPriceTextView))
                .check(matches(withText("150 credits/month")))

            // Click next button to move to "Classical Piano"
            onView(withId(R.id.nextButton))
                .perform(click())

            onView(withId(R.id.itemNameTextView))
                .check(matches(withText("Classical Piano")))
            onView(withId(R.id.itemPriceTextView))
                .check(matches(withText("350 credits/month")))

            // Click next button to cycle back to "Electric Guitar"
            onView(withId(R.id.nextButton))
                .perform(click())

            onView(withId(R.id.itemNameTextView))
                .check(matches(withText("Electric Guitar")))
        }
    }

    /**
     * Test that the borrow button launches BorrowActivity
     */
    @Test
    fun testBorrowButtonLaunchesActivity() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Click the borrow button
            onView(withId(R.id.borrowButton))
                .perform(click())

            // Verify that BorrowActivity is launched with the correct intent extras
            Intents.intended(allOf(
                hasComponent(BorrowActivity::class.java.name),
                hasExtra("rentalItem", testRentalItems[0])
            ))
        }
    }

    /**
     * Test handling of activity results
     */
    @Test
    fun testActivityResultHandling() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Use scenario.onActivity to get access to the activity
            scenario.onActivity { activity ->
                // Create a result intent with an updated rental item
                val resultIntent = Intent()
                val updatedItem = RentalItem(
                    name = "Electric Guitar",
                    rating = 5.0f, // Updated rating
                    features = listOf("6 Strings", "Solid Body", "Sunburst Finish"),
                    pricePerMonth = 120, // Updated price
                    imageResId = R.drawable.eguitar
                )
                resultIntent.putExtra("updatedRentalItem", updatedItem)

                // Simulate activity result
                activity.onActivityResult(1, Activity.RESULT_OK, resultIntent)
            }

            // Verify that the item was updated
            onView(withId(R.id.itemPriceTextView))
                .check(matches(withText("120 credits/month")))
        }
    }

    /**
     * Test state restoration after recreating the activity
     */
    @Test
    fun testStateRestoration() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Move to the second item
            onView(withId(R.id.nextButton))
                .perform(click())

            // Verify we're on the second item
            onView(withId(R.id.itemNameTextView))
                .check(matches(withText("Drum Set")))

            // Recreate the activity (simulates configuration change like rotation)
            scenario.recreate()

            // Verify state is preserved (still on second item)
            onView(withId(R.id.itemNameTextView))
                .check(matches(withText("Drum Set")))
        }
    }

    /**
     * Test UI elements visibility
     */
    @Test
    fun testUIElementsVisibility() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Check that all UI elements are visible
            onView(withId(R.id.itemNameTextView))
                .check(matches(isDisplayed()))

            onView(withId(R.id.itemPriceTextView))
                .check(matches(isDisplayed()))

            onView(withId(R.id.itemRatingBar))
                .check(matches(isDisplayed()))

            onView(withId(R.id.itemImageView))
                .check(matches(isDisplayed()))

            onView(withId(R.id.nextButton))
                .check(matches(isDisplayed()))

            onView(withId(R.id.borrowButton))
                .check(matches(isDisplayed()))

            onView(withId(R.id.searchView))
                .check(matches(isDisplayed()))
        }
    }

    /**
     * Test borrow flow with result handling
     */
    @Test
    fun testCompleteBorrowFlow() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Click borrow button to launch BorrowActivity
            onView(withId(R.id.borrowButton))
                .perform(click())

            // At this point, BorrowActivity would be launched
            // We can't easily test the full flow in a single test without mocking
            // but we can verify that the intent was fired correctly
            Intents.intended(hasComponent(BorrowActivity::class.java.name))

            // In an actual flow, we would now need to simulate the result coming back
            // This part would need ActivityScenario.registerActivityResult in a real test
        }
    }
}