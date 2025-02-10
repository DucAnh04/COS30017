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

    // Companion object holds constants used throughout the activity.
    companion object {
        const val MAX_HOLD = 9             // The maximum number of holds (i.e., climbing stages)
        const val MAX_SCORE = 18           // The maximum score allowed (score is capped at 18)
        const val TAG = "ClimbApp"         // Tag used for logging
    }

    // Variables to hold the current state.
    private var score = 0                   // Current score of the climber
    private var currentHold = 0             // Current hold reached (from 0 up to MAX_HOLD)
    private var hasFallen = false           // Flag indicating whether the climber has fallen
    private var timerStarted = false        // Flag indicating whether the CountUpTimer is currently running

    // UI elements defined in the layout.
    private lateinit var scoreTextView: TextView   // Displays the current score
    private lateinit var timerTextView: TextView   // Displays the elapsed time
    private lateinit var climbButton: Button       // Button to simulate climbing action
    private lateinit var fallButton: Button        // Button to simulate falling action
    private lateinit var resetButton: Button       // Button to reset the session
    private lateinit var languageButton: Button    // Button to toggle the language

    // Custom CountUpTimer that ticks every 1000 ms (1 second).
    // This timer will call the abstract onTick() method every second.
    private lateinit var countUpTimer: CountUpTimer

    /**
     * Called when the activity is first created.
     * Initializes the layout, UI elements, timer, and restores state if available.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view; Android automatically selects the correct layout (portrait/landscape)
        setContentView(R.layout.activity_main)

        // Initialize UI elements by finding them in the layout.
        scoreTextView = findViewById(R.id.scoreTextView)
        timerTextView = findViewById(R.id.timerTextView)
        climbButton = findViewById(R.id.climbButton)
        fallButton = findViewById(R.id.fallButton)
        resetButton = findViewById(R.id.resetButton)
        languageButton = findViewById(R.id.languageButton)

        // Create an instance of CountUpTimer with a 1-second interval.
        countUpTimer = object : CountUpTimer(1000) {
            override fun onTick(timeElapsed: Long) {
                // If the score has reached or exceeded MAX_SCORE, stop the timer.
                if (score >= MAX_SCORE) {
                    countUpTimer.stop()
                    timerStarted = false  // Ensure we mark the timer as not running
                } else {
                    // Calculate the elapsed seconds and update timerTextView.
                    val seconds = timeElapsed / 1000
                    timerTextView.text = getString(R.string.timer, seconds)
                }
            }
        }

        // If savedInstanceState is available, restore previous state.
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt("score", 0)
            currentHold = savedInstanceState.getInt("currentHold", 0)
            hasFallen = savedInstanceState.getBoolean("hasFallen", false)
            val timeElapsed = savedInstanceState.getLong("timeElapsed", 0)
            timerStarted = savedInstanceState.getBoolean("timerStarted", false)
            // Restore the elapsed time in the timer.
            countUpTimer.setTimeElapsed(timeElapsed)
            // Even if the timer is not running, update the timerTextView so that the elapsed time is preserved.
            timerTextView.text = getString(R.string.timer, timeElapsed / 1000)
            // If the timer was running before, restart it.
            if (timerStarted) {
                countUpTimer.start()
            }
            Log.d(TAG, "State restored: score=$score, currentHold=$currentHold, hasFallen=$hasFallen, timeElapsed=$timeElapsed")
        } else {
            Log.d(TAG, "Starting new session with score=0")
        }

        // Update the score display based on the current state.
        updateScoreDisplay()

        // Set the onClickListener for the Climb button.
        climbButton.setOnClickListener {
            // If the climber has fallen or already reached the maximum hold, ignore the climb attempt.
            if (hasFallen || currentHold == MAX_HOLD) {
                Log.d(TAG, "Climb attempt ignored.")
                return@setOnClickListener
            }

            // If the timer is not already running, start it.
            if (!timerStarted) {
                countUpTimer.start()
                timerStarted = true
            }

            currentHold++  // Increment the current hold

            // Calculate the points to add based on the current hold:
            // - Holds 1-3 (Blue zone): add 1 point per hold.
            // - Holds 4-6 (Green zone): add 2 points per hold.
            // - Holds 7-9 (Red zone): add 3 points per hold.
            val pointsToAdd = when (currentHold) {
                in 1..3 -> 1
                in 4..6 -> 2
                in 7..9 -> 3
                else -> 0
            }
            score += pointsToAdd
            if (score > MAX_SCORE) score = MAX_SCORE  // Cap the score if it exceeds MAX_SCORE

            Log.d(TAG, "Climb: reached hold $currentHold (+$pointsToAdd points), score now $score")
            updateScoreDisplay()

            // If the maximum hold is reached or the score reaches MAX_SCORE, stop the timer.
            if (currentHold == MAX_HOLD || score >= MAX_SCORE) {
                countUpTimer.stop()
                timerStarted = false
            }
        }

        // Set the onClickListener for the Fall button.
        fallButton.setOnClickListener {
            // Ignore the fall if the climber hasn't reached any hold, has already fallen, or is at the top.
            if (currentHold < 1 || hasFallen || currentHold == MAX_HOLD) {
                Log.d(TAG, "Fall attempt ignored.")
                return@setOnClickListener
            }

            score -= 3  // Deduct 3 points for falling
            if (score < 0) score = 0  // Ensure score does not drop below 0
            hasFallen = true  // Mark that the climber has fallen

            Log.d(TAG, "Fall: score decreased by 3, now $score. Climber has fallen.")
            updateScoreDisplay()
            countUpTimer.stop()
            timerStarted = false  // Ensure the timer remains stopped on fall
        }

        // Set the onClickListener for the Reset button.
        resetButton.setOnClickListener {
            // Reset all state variables.
            score = 0
            currentHold = 0
            hasFallen = false
            timerStarted = false

            Log.d(TAG, "Reset: score and holds cleared.")
            updateScoreDisplay()
            // Reset the timer's elapsed time and update the timer display.
            countUpTimer.reset()
            timerTextView.text = getString(R.string.timer, 0)
        }

        // Set the onClickListener for the Language button.
        languageButton.setOnClickListener {
            // Switch the app language.
            changeLanguage()
        }
    }

    /**
     * Changes the app language between English and Vietnamese.
     *
     * This function checks the current locale. If it is English ("en"), it switches to Vietnamese ("vi"),
     * otherwise it switches back to English. After updating the locale in the configuration,
     * it calls recreate() to restart the activity so that the new language resources are applied.
     */
    private fun changeLanguage() {
        val currentLocale = resources.configuration.locales[0]
        val newLocale = if (currentLocale.language == "en") Locale("vi") else Locale("en")
        Locale.setDefault(newLocale)
        val config = Configuration()
        config.setLocale(newLocale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate() // Restart the activity to apply changes and reload resources
    }

    /**
     * Updates the displayed score and sets the text color based on the current hold zone.
     *
     * The scoreTextView is updated using a string resource (which formats the score),
     * and the text color is changed according to the current hold:
     * - Blue for holds 1-3.
     * - Green for holds 4-6.
     * - Red for holds 7-9.
     * If no hold has been reached, the text remains black.
     */
    private fun updateScoreDisplay() {
        scoreTextView.text = getString(R.string.score, score)
        val textColor = when (currentHold) {
            in 1..3 -> ContextCompat.getColor(this, R.color.blue)
            in 4..6 -> ContextCompat.getColor(this, R.color.green)
            in 7..MAX_HOLD -> ContextCompat.getColor(this, R.color.red)
            else -> Color.BLACK
        }
        scoreTextView.setTextColor(textColor)
    }

    /**
     * Saves the current state before the activity is destroyed.
     *
     * The current score, hold number, fall status, timer elapsed time, and whether the timer was running
     * are stored in the outState Bundle. This allows the activity to restore its state after configuration changes.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("score", score)
        outState.putInt("currentHold", currentHold)
        outState.putBoolean("hasFallen", hasFallen)
        outState.putLong("timeElapsed", countUpTimer.getTimeElapsed())
        outState.putBoolean("timerStarted", timerStarted)
        Log.d(TAG, "onSaveInstanceState: score=$score, currentHold=$currentHold, hasFallen=$hasFallen, timeElapsed=${countUpTimer.getTimeElapsed()}")
        super.onSaveInstanceState(outState)
    }
}
