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


@RunWith(RobolectricTestRunner::class)
// Remove explicit absolute manifest path to let Robolectric auto-detect it.
// Alternatively, if needed, you can use a relative path such as "src/main/AndroidManifest.xml".
@Config(sdk = [28])
class MainActivityTest {

    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
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
        climbButton.performClick()
        val scoreTextView = activity.findViewById<TextView>(R.id.scoreTextView)
        assertEquals("Score: 1", scoreTextView.text.toString())
    }

    @Test
    fun testFallButton() {
        val climbButton = activity.findViewById<Button>(R.id.climbButton)
        val fallButton = activity.findViewById<Button>(R.id.fallButton)
        val scoreTextView = activity.findViewById<TextView>(R.id.scoreTextView)
        // First click Climb so that falling is allowed.
        climbButton.performClick()
        fallButton.performClick()
        assertEquals("Score: 0", scoreTextView.text.toString())
    }

    @Test
    fun testResetButton() {
        val climbButton = activity.findViewById<Button>(R.id.climbButton)
        val resetButton = activity.findViewById<Button>(R.id.resetButton)
        // Increase score then reset.
        climbButton.performClick()
        resetButton.performClick()
        val scoreTextView = activity.findViewById<TextView>(R.id.scoreTextView)
        assertEquals("Score: 0", scoreTextView.text.toString())
    }

    @Test
    @Config(qualifiers = "vi")
    fun testLanguageButton() {
        // Rebuild the activity so that Robolectric loads the resources for Vietnamese
        activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .start()
            .resume()
            .visible()
            .get()

        val scoreTextView = activity.findViewById<TextView>(R.id.scoreTextView)
        // Now, the getString(R.string.score, score) call in updateScoreDisplay() will pick up the Vietnamese string,
        // for example "Điểm: 0" instead of "Score: 0".
        assertEquals("Điểm: 0", scoreTextView.text.toString())
    }

}
