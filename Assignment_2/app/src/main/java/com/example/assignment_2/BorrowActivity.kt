package com.example.assignment_2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class BorrowActivity : AppCompatActivity() {

    companion object {
        private const val RENTAL_ITEM_KEY = "rentalItem"
        private const val MAX_CREDIT_LIMIT = 1000
    }

    private var rentalItem: RentalItem? = null

    private lateinit var borrowItemImageView: ImageView
    private lateinit var borrowItemNameTextView: TextView
    private lateinit var borrowItemRatingBar: RatingBar
    private lateinit var borrowItemPriceTextView: TextView
    private lateinit var itemFeaturesChipGroup: ChipGroup
    private lateinit var quantityEditText: EditText
    private lateinit var confirmBorrowButton: Button
    private lateinit var cancelBorrowButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrow)

        Log.d("BorrowActivity", "onCreate called")

        // Initialize views
        borrowItemImageView = findViewById(R.id.borrowItemImageView)
        borrowItemNameTextView = findViewById(R.id.borrowItemNameTextView)
        borrowItemRatingBar = findViewById(R.id.borrowItemRatingBar)
        borrowItemPriceTextView = findViewById(R.id.borrowItemPriceTextView)
        itemFeaturesChipGroup = findViewById(R.id.itemFeaturesChipGroup)
        quantityEditText = findViewById(R.id.quantityEditText)
        confirmBorrowButton = findViewById(R.id.confirmBorrowButton)
        cancelBorrowButton = findViewById(R.id.cancelBorrowButton)

        // Retrieve the RentalItem passed via Intent or savedInstanceState
        rentalItem = savedInstanceState?.getParcelable(RENTAL_ITEM_KEY) ?: intent.getParcelableExtra("rentalItem")

        if (rentalItem == null) {
            Toast.makeText(this, "Error: Rental item not found.", Toast.LENGTH_SHORT).show()
            Log.e("BorrowActivity", "Rental item is null")
            finish()
            return
        }

        // Display item details
        displayItemDetails()

        // Set up button listeners
        confirmBorrowButton.setOnClickListener {
            if (validateBooking()) {
                val resultIntent = Intent().apply {
                    putExtra("updatedRentalItem", rentalItem)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                Toast.makeText(this, "${rentalItem?.name} borrowed successfully!", Toast.LENGTH_SHORT).show()
                Log.d("BorrowActivity", "Confirm borrow button clicked, item: ${rentalItem?.name}")
                finish()
            }
        }

        cancelBorrowButton.setOnClickListener {
            // Handle the cancellation logic here
            Toast.makeText(this, "Borrowing ${rentalItem?.name} canceled.", Toast.LENGTH_SHORT).show()
            Log.d("BorrowActivity", "Cancel borrow button clicked, item: ${rentalItem?.name}")
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(RENTAL_ITEM_KEY, rentalItem)
        Log.d("BorrowActivity", "onSaveInstanceState called, item: ${rentalItem?.name}")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        rentalItem = savedInstanceState.getParcelable(RENTAL_ITEM_KEY)
        displayItemDetails()
        Log.d("BorrowActivity", "onRestoreInstanceState called, item: ${rentalItem?.name}")
    }

    private fun displayItemDetails() {
        rentalItem?.let {
            Log.d("BorrowActivity", "Displaying item: ${it.name}")
            borrowItemImageView.setImageResource(it.imageResId)
            borrowItemNameTextView.text = it.name
            borrowItemRatingBar.rating = it.rating
            borrowItemPriceTextView.text = "${it.pricePerMonth} credits/month"

            itemFeaturesChipGroup.removeAllViews()
            it.features.forEach { feature ->
                val chip = Chip(this).apply {
                    text = feature
                    isCheckable = false
                }
                itemFeaturesChipGroup.addView(chip)
            }
        }
    }

    private fun validateBooking(): Boolean {
        val quantity = quantityEditText.text.toString().toIntOrNull()
        if (quantity == null || quantity <= 0) {
            Toast.makeText(this, "Please enter a valid quantity.", Toast.LENGTH_SHORT).show()
            return false
        }

        val totalCost = rentalItem?.pricePerMonth?.times(quantity) ?: 0
        if (totalCost > MAX_CREDIT_LIMIT) {
            Toast.makeText(this, "Credit limit exceeded! Max limit: $MAX_CREDIT_LIMIT credits.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}