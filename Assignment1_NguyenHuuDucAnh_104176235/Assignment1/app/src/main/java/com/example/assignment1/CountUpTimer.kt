package com.example.assignment1

import android.os.Handler
import android.os.Looper

/**
 * Abstract CountUpTimer class.
 *
 * This class implements a simple timer that counts up (increases elapsed time)
 * at a fixed interval. It uses an Android Handler to schedule repeated executions
 * of a Runnable on the main (UI) thread. Subclasses must implement the abstract
 * onTick() method to define what should happen on each tick.
 *
 * @param interval The time interval in milliseconds between each tick.
 */
abstract class CountUpTimer(private val interval: Long) {

    // Stores the total elapsed time in milliseconds.
    private var timeElapsed: Long = 0

    // A Handler attached to the main looper, used to schedule timer ticks on the UI thread.
    private val handler = Handler(Looper.getMainLooper())

    // A Runnable that will be executed repeatedly.
    // Each time it's run, it increments the elapsed time, calls the onTick() callback,
    // and posts itself again after the specified interval.
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            // Increment the elapsed time by the interval value.
            timeElapsed += interval
            // Notify the subclass of the tick event, passing the updated elapsed time.
            onTick(timeElapsed)
            // Schedule the next tick after the specified interval.
            handler.postDelayed(this, interval)
        }
    }

    /**
     * Abstract method called on every tick of the timer.
     *
     * Subclasses should override this method to perform actions each time the timer ticks.
     *
     * @param timeElapsed The current elapsed time in milliseconds.
     */
    abstract fun onTick(timeElapsed: Long)

    /**
     * Starts the timer.
     *
     * This method posts the runnable to the Handler, beginning the periodic tick events.
     */
    fun start() {
        handler.post(runnable)
    }

    /**
     * Stops the timer.
     *
     * This method removes any pending posts of the runnable from the Handler,
     * effectively stopping further tick events.
     */
    fun stop() {
        handler.removeCallbacks(runnable)
    }

    /**
     * Resets the timer.
     *
     * Stops the timer and resets the elapsed time to zero.
     */
    fun reset() {
        stop()
        timeElapsed = 0
    }

    /**
     * Returns the current elapsed time.
     *
     * @return The elapsed time in milliseconds.
     */
    fun getTimeElapsed(): Long {
        return timeElapsed
    }

    /**
     * Sets the current elapsed time.
     *
     * This can be used to restore a previously saved elapsed time.
     *
     * @param timeElapsed The elapsed time in milliseconds to set.
     */
    fun setTimeElapsed(timeElapsed: Long) {
        this.timeElapsed = timeElapsed
    }
}
