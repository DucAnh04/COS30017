package com.example.assignment1

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    // Companion object holds constants for maximum holds, maximum score, and a tag for logging.
    companion object {
        const val MAX_HOLD = 9             // Maximum number of holds on the wall
        const val MAX_SCORE = 18           // Maximum score allowed
        const val TAG = "ClimbApp"         // Tag for log messages
    }

    // Variables to keep track of the current score, current hold, and whether a fall has occurred.
    private var score = 0
    private var currentHold = 0            // Tracks the current hold number (from 0 to MAX_HOLD)
    private var hasFallen = false          // Indicates if the climber has fallen (prevents further climbing)

    // UI elements: TextView to display the score and Buttons for each action.
    private lateinit var scoreTextView: TextView
    private lateinit var climbButton: Button
    private lateinit var fallButton: Button
    private lateinit var resetButton: Button
    private lateinit var languageButton: Button

    // Called when the activity is first created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout defined in res/layout/activity_main.xml based on the current orientation.
        setContentView(R.layout.activity_main)

        // Initialize UI elements by finding them in the layout.
        scoreTextView = findViewById(R.id.scoreTextView)
        climbButton = findViewById(R.id.climbButton)
        fallButton = findViewById(R.id.fallButton)
        resetButton = findViewById(R.id.resetButton)
        languageButton = findViewById(R.id.languageButton)

        // Restore the saved state if the activity is recreated (for example, after a rotation).
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt("score", 0)
            currentHold = savedInstanceState.getInt("currentHold", 0)
            hasFallen = savedInstanceState.getBoolean("hasFallen", false)
            Log.d(TAG, "State restored: score=$score, currentHold=$currentHold, hasFallen=$hasFallen")
        } else {
            Log.d(TAG, "Starting new session with score=0")
        }

        // Update the display with the current score and appropriate text color.
        updateScoreDisplay()

        // Set the click listener for the Climb button.
        climbButton.setOnClickListener {
            // Prevent climbing if the climber has already fallen.
            if (hasFallen) {
                Log.d(TAG, "Attempted climb after fall. Ignored.")
                return@setOnClickListener
            }
            // Prevent climbing if the maximum hold has been reached.
            if (currentHold == MAX_HOLD) {
                Log.d(TAG, "Top hold reached. Further climbing is disabled.")
                return@setOnClickListener
            }

            // Increment the current hold number as the climber moves up.
            currentHold++ // Move to the next hold

            // Determine the points to add based on the current hold zone.
            // Holds 1-3 (Blue zone): +1 point each.
            // Holds 4-6 (Green zone): +2 points each.
            // Holds 7-9 (Red zone): +3 points each.
            val pointsToAdd = when (currentHold) {
                in 1..3 -> 1
                in 4..6 -> 2
                in 7..9 -> 3
                else -> 0
            }
            // Update the score by adding the calculated points.
            score += pointsToAdd

            // Ensure that the score does not exceed the maximum allowed score.
            if (score > MAX_SCORE) score = MAX_SCORE

            // Log the action for debugging purposes.
            Log.d(TAG, "Climb: reached hold $currentHold (+$pointsToAdd points), score now $score")

            // Update the UI to reflect the new score and possibly change the text color.
            updateScoreDisplay()
        }

        // Set the click listener for the Fall button.
        fallButton.setOnClickListener {
            // Prevent a fall if the climber hasn't reached the first hold.
            if (currentHold < 1) {
                Log.d(TAG, "Fall attempted before reaching first hold.")
                return@setOnClickListener
            }
            // If the climber is at the top hold, falling has no effect.
            if (currentHold == MAX_HOLD) {
                Log.d(TAG, "Fall attempted at top hold. No action taken.")
                return@setOnClickListener
            }
            // Prevent a fall if the climber has already fallen.
            if (hasFallen) {
                Log.d(TAG, "Fall attempted after already falling.")
                return@setOnClickListener
            }

            // Subtract 3 points due to the fall.
            score -= 3
            // Ensure that the score does not go below 0.
            if (score < 0) score = 0

            // Mark that a fall has occurred so that further climbing is disabled.
            hasFallen = true

            // Log the fall action.
            Log.d(TAG, "Fall: score decreased by 3, now $score. Climber has fallen.")

            // Update the UI with the new score.
            updateScoreDisplay()
        }

        // Set the click listener for the Reset button.
        resetButton.setOnClickListener {
            // Reset the score, hold count, and fall status.
            score = 0
            currentHold = 0
            hasFallen = false

            // Log the reset action.
            Log.d(TAG, "Reset: score and holds cleared.")

            // Update the UI to reflect the reset state.
            updateScoreDisplay()
        }

        // Set the click listener for the Language button.
        languageButton.setOnClickListener {
            // Call changeLanguage() to switch between English and Vietnamese.
            changeLanguage()
        }
    }

    /**
     * Changes the app language between English and Vietnamese.
     *
     * This function checks the current locale; if it is English ("en"),
     * it changes to Vietnamese ("vi"), otherwise it switches back to English.
     * The locale is updated in the app's resources, and then the activity is recreated
     * so that the new language takes effect.
     */
    private fun changeLanguage() {
        // Get the current locale from the resources.
        val currentLocale = resources.configuration.locales[0]
        // Determine the new locale: switch to Vietnamese if current is English, otherwise switch to English.
        val newLocale = if (currentLocale.language == "en") Locale("vi") else Locale("en")
        // Set the new locale as default.
        Locale.setDefault(newLocale)
        // Create a new Configuration instance and set the new locale.
        val config = Configuration()
        config.setLocale(newLocale)
        // Update the app's resources with the new configuration.
        resources.updateConfiguration(config, resources.displayMetrics)
        // Recreate the activity so that it reloads resources using the new locale.
        recreate()
    }

    /**
     * Updates the displayed score and sets the text colour based on the current zone.
     *
     * The scoreTextView is updated with the current score using a formatted string
     * from the app's string resources. The text color changes according to the zone:
     * - Blue for holds 1-3.
     * - Green for holds 4-6.
     * - Red for holds 7-9.
     * If no hold has been reached, the text remains black.
     */
    private fun updateScoreDisplay() {
        // Update the score text using a formatted string resource.
        scoreTextView.text = getString(R.string.score, score)
        // Determine the color based on the current hold number.
        val textColor = when (currentHold) {
            in 1..3 -> ContextCompat.getColor(this, R.color.blue)
            in 4..6 -> ContextCompat.getColor(this, R.color.green)
            in 7..MAX_HOLD -> ContextCompat.getColor(this, R.color.red)
            else -> Color.BLACK
        }
        // Set the text color of the scoreTextView.
        scoreTextView.setTextColor(textColor)
    }

    /**
     * Called before the activity is destroyed to save the current state.
     *
     * This method saves the current score, hold count, and fall status into the provided Bundle,
     * so that these values can be restored if the activity is recreated (for example, during a rotation).
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("score", score)
        outState.putInt("currentHold", currentHold)
        outState.putBoolean("hasFallen", hasFallen)
        Log.d(TAG, "onSaveInstanceState: score=$score, currentHold=$currentHold, hasFallen=$hasFallen")
        super.onSaveInstanceState(outState)
    }
}
