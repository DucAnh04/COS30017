package com.example.assignment1

import android.widget.Button
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.util.Locale
import org.robolectric.RuntimeEnvironment
import java.util.concurrent.TimeUnit


@RunWith(RobolectricTestRunner::class)
// Remove explicit absolute manifest path to let Robolectric auto-detect it.
@Config(sdk = [28])
class MainActivityTest {

    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        // Build the activity normally.
        activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .start()
            .resume()
            .visible()
            .get()
    }

    @Test
    fun testInitialScore() {
        val scoreTextView = activity.findViewById<TextView>(R.id.scoreTextView)
        assertEquals("Score: 0", scoreTextView.text.toString())
    }

    @Test
    fun testClimbButton() {
        val climbButton = activity.findViewById<Button>(R.id.climbButton)
        // Click the climb button 2 times to increase the score
        for (i in 1..2) {
            climbButton.performClick()
        }
        val scoreTextView = activity.findViewById<TextView>(R.id.scoreTextView)
        assertEquals("Score: 2", scoreTextView.text.toString())
    }

    @Test
    fun testFallButton() {
        val climbButton = activity.findViewById<Button>(R.id.climbButton)
        val fallButton = activity.findViewById<Button>(R.id.fallButton)
        val scoreTextView = activity.findViewById<TextView>(R.id.scoreTextView)
        // Click the climb button 4 times to increase the score
        for (i in 1..4) {
            climbButton.performClick()
        }
        fallButton.performClick()
        assertEquals("Score: 2", scoreTextView.text.toString())
    }

    @Test
    fun testResetButton() {
        val climbButton = activity.findViewById<Button>(R.id.climbButton)
        val resetButton = activity.findViewById<Button>(R.id.resetButton)
        // Increase score then reset.
        climbButton.performClick()
        resetButton.performClick()
        val scoreTextView = activity.findViewById<TextView>(R.id.scoreTextView)
        val timerTextView = activity.findViewById<TextView>(R.id.timerTextView)
        assertEquals("Score: 0", scoreTextView.text.toString())
        assertEquals("Time: 0", timerTextView.text.toString())

    }

    @Test
    fun testLanguageButtonClick() {
        // Build the activity normally; default language is assumed to be English.
        activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .start()
            .resume()
            .visible()
            .get()

        // Verify the initial score text is in English.
        var scoreTextView = activity.findViewById<TextView>(R.id.scoreTextView)
        assertEquals("Score: 0", scoreTextView.text.toString())

        // This triggers the changeLanguage() method which toggles the locale and calls recreate().
        val languageButton = activity.findViewById<Button>(R.id.languageButton)
        languageButton.performClick()

        // Since changeLanguage() calls recreate(), we simulate the activity's recreation.
        // (In Robolectric, recreate() creates a new instance, so we rebuild the activity.)
        activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .start()
            .resume()
            .visible()
            .get()

        // Now the resources should be loaded in Vietnamese.
        scoreTextView = activity.findViewById(R.id.scoreTextView)
        // Verify that the score text now displays in Vietnamese ("Điểm: 0").
        assertEquals("Điểm: 0", scoreTextView.text.toString())
    }

    @Test
    fun testTimerFunctionality() {
        // Build the activity using Robolectric.
        activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .start()
            .resume()
            .visible()
            .get()

        // Get the timerTextView and ensure it initially displays "Time: 0"
        val timerTextView = activity.findViewById<TextView>(R.id.timerTextView)
        assertEquals("Time: 0", timerTextView.text.toString())

        // Simulate clicking the Climb button to start the timer.
        // According to your MainActivity logic, clicking Climb will start the timer if it isn't already running.
        val climbButton = activity.findViewById<Button>(R.id.climbButton)
        climbButton.performClick()

        // Use Robolectric's shadowOf(mainLooper) to simulate 2500 milliseconds passing.
        // Since your CountUpTimer ticks every 1000 ms, after 2500ms the timer should display 2 seconds (integer division).
        shadowOf(activity.mainLooper).idleFor(2000L, TimeUnit.MILLISECONDS)
        assertEquals("Time: 2", timerTextView.text.toString())

        // Simulate an additional 1500 ms passing (totaling 4000 ms or 4 seconds).
        shadowOf(activity.mainLooper).idleFor(2000L, TimeUnit.MILLISECONDS)
        assertEquals("Time: 4", timerTextView.text.toString())
    }

}
