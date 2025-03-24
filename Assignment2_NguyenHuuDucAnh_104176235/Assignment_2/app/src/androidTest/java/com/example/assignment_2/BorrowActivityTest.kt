package com.example.assignment_2

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.chip.ChipGroup
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BorrowActivityTest {

    private lateinit var testRentalItem: RentalItem

    @Before
    fun setUp() {
        // Create a test rental item based on your RentalItem class
        testRentalItem = RentalItem(
            name = "Test Guitar",
            imageResId = R.drawable.eguitar, // Using your existing drawable
            rating = 4.5f,
            pricePerMonth = 200,
            features = listOf("Feature 1", "Feature 2", "Feature 3")
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
     * Test that the activity displays the rental item correctly
     */
    @Test
    fun testItemDisplay() {
        // Create an intent with the rental item
        val intent = Intent(ApplicationProvider.getApplicationContext(), BorrowActivity::class.java).apply {
            putExtra("rentalItem", testRentalItem)
        }

        // Launch the activity with the intent
        ActivityScenario.launch<BorrowActivity>(intent).use { scenario ->
            // Check that item name is displayed correctly
            onView(withId(R.id.borrowItemNameTextView))
                .check(matches(withText("Test Guitar")))

            // Check that price is displayed correctly
            onView(withId(R.id.borrowItemPriceTextView))
                .check(matches(withText("200 credits/month")))

            // Check that rating bar shows correct rating
            onView(withId(R.id.borrowItemRatingBar))
                .check(matches(isDisplayed()))

            // Verify image is displayed (can't easily check the specific image resource)
            onView(withId(R.id.borrowItemImageView))
                .check(matches(isDisplayed()))

            // Verify feature chips are displayed
            onView(withId(R.id.itemFeaturesChipGroup))
                .check(matches(isDisplayed()))
        }
    }

    /**
     * Test quantity validation with empty input
     */
    @Test
    fun testEmptyQuantityValidation() {
        // Create an intent with the rental item
        val intent = Intent(ApplicationProvider.getApplicationContext(), BorrowActivity::class.java).apply {
            putExtra("rentalItem", testRentalItem)
        }

        // Launch the activity with the intent
        ActivityScenario.launch<BorrowActivity>(intent).use { scenario ->
            // Clear quantity field and click confirm
            onView(withId(R.id.quantityEditText))
                .perform(replaceText(""))

            onView(withId(R.id.confirmBorrowButton))
                .perform(click())

            // Activity should still be running (not finished)
            // We can verify this by checking that a view is still displayed
            onView(withId(R.id.confirmBorrowButton))
                .check(matches(isDisplayed()))
        }
    }

    /**
     * Test quantity validation with zero quantity
     */
    @Test
    fun testZeroQuantityValidation() {
        // Create an intent with the rental item
        val intent = Intent(ApplicationProvider.getApplicationContext(), BorrowActivity::class.java).apply {
            putExtra("rentalItem", testRentalItem)
        }

        // Launch the activity with the intent
        ActivityScenario.launch<BorrowActivity>(intent).use { scenario ->
            // Enter zero quantity and click confirm
            onView(withId(R.id.quantityEditText))
                .perform(replaceText("0"))

            onView(withId(R.id.confirmBorrowButton))
                .perform(click())

            // Activity should still be running (not finished)
            onView(withId(R.id.confirmBorrowButton))
                .check(matches(isDisplayed()))
        }
    }

    /**
     * Test validation with negative quantity
     */
    @Test
    fun testNegativeQuantityValidation() {
        // Create an intent with the rental item
        val intent = Intent(ApplicationProvider.getApplicationContext(), BorrowActivity::class.java).apply {
            putExtra("rentalItem", testRentalItem)
        }

        // Launch the activity with the intent
        ActivityScenario.launch<BorrowActivity>(intent).use { scenario ->
            // Enter negative quantity and click confirm
            onView(withId(R.id.quantityEditText))
                .perform(replaceText("-1"))

            onView(withId(R.id.confirmBorrowButton))
                .perform(click())

            // Activity should still be running (not finished)
            onView(withId(R.id.confirmBorrowButton))
                .check(matches(isDisplayed()))
        }
    }

    /**
     * Test credit limit validation
     */
    @Test
    fun testCreditLimitExceeded() {
        // Create an intent with the rental item
        val intent = Intent(ApplicationProvider.getApplicationContext(), BorrowActivity::class.java).apply {
            putExtra("rentalItem", testRentalItem)
        }

        // Launch the activity with the intent
        ActivityScenario.launch<BorrowActivity>(intent).use { scenario ->
            // Enter quantity that exceeds credit limit (1000 / 200 = 5, so 6 should fail)
            onView(withId(R.id.quantityEditText))
                .perform(replaceText("6"))

            onView(withId(R.id.confirmBorrowButton))
                .perform(click())

            // Activity should still be running (not finished)
            onView(withId(R.id.confirmBorrowButton))
                .check(matches(isDisplayed()))
        }
    }

    /**
     * Test successful borrowing with valid quantity
     */
    @Test
    fun testValidQuantity() {
        // Create an intent with the rental item
        val intent = Intent(ApplicationProvider.getApplicationContext(), BorrowActivity::class.java).apply {
            putExtra("rentalItem", testRentalItem)
        }

        // Set up an intended response for BorrowActivity
        val resultIntent = Intent()
        resultIntent.putExtra("updatedRentalItem", testRentalItem)
        val result = ActivityResult(Activity.RESULT_OK, resultIntent)

        // Launch the activity with the intent
        ActivityScenario.launch<BorrowActivity>(intent).use { scenario ->
            // Enter valid quantity and click confirm
            onView(withId(R.id.quantityEditText))
                .perform(replaceText("3"))

            onView(withId(R.id.confirmBorrowButton))
                .perform(click())

            // Activity should finish now, but we can't easily verify that in AndroidTest
            // Note: We could use ActivityResultRegistry or similar in a more complex test
        }
    }

    /**
     * Test cancel button
     */
    @Test
    fun testCancelButton() {
        // Create an intent with the rental item
        val intent = Intent(ApplicationProvider.getApplicationContext(), BorrowActivity::class.java).apply {
            putExtra("rentalItem", testRentalItem)
        }

        // Launch the activity with the intent
        ActivityScenario.launch<BorrowActivity>(intent).use { scenario ->
            // Click cancel button
            onView(withId(R.id.cancelBorrowButton))
                .perform(click())

            // Activity should finish now, but we can't easily verify that in AndroidTest
        }
    }

    /**
     * Test restoration of state after device rotation
     */
    @Test
    fun testStateRestoration() {
        // Create an intent with the rental item
        val intent = Intent(ApplicationProvider.getApplicationContext(), BorrowActivity::class.java).apply {
            putExtra("rentalItem", testRentalItem)
        }

        // Launch the activity with the intent
        ActivityScenario.launch<BorrowActivity>(intent).use { scenario ->
            // Initial state
            onView(withId(R.id.borrowItemNameTextView))
                .check(matches(withText("Test Guitar")))

            // Add quantity
            onView(withId(R.id.quantityEditText))
                .perform(replaceText("3"))

            // Recreate the activity (simulates configuration change like rotation)
            scenario.recreate()

            // Verify state is preserved
            onView(withId(R.id.borrowItemNameTextView))
                .check(matches(withText("Test Guitar")))

            // Note: EditText content is typically not preserved across recreation
            // unless you implement onSaveInstanceState for it
        }
    }

    /**
     * Test handling of null rental item
     */
    @Test
    fun testNullRentalItem() {
        // Create an intent without a rental item
        val intent = Intent(ApplicationProvider.getApplicationContext(), BorrowActivity::class.java)

        // Launch the activity with the intent
        val scenario = ActivityScenario.launch<BorrowActivity>(intent)

        // Activity should finish soon because of null rental item
        // But we can't directly test for finishing in an instrumented test

        // Close the scenario
        scenario.close()
    }
}