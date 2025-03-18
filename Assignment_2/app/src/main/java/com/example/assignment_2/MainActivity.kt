package com.example.assignment_2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val CURRENT_INDEX_KEY = "currentIndex"
        private const val DETAIL_REQUEST_CODE = 1
    }

    private lateinit var items: List<RentalItem>
    private var currentIndex = 0

    private lateinit var itemImageView: ImageView
    private lateinit var itemNameTextView: TextView
    private lateinit var itemRatingBar: RatingBar
    private lateinit var itemPriceTextView: TextView
    private lateinit var borrowButton: Button
    private lateinit var nextButton: Button
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "onCreate called")

        // Initialize views
        itemImageView = findViewById(R.id.itemImageView)
        itemNameTextView = findViewById(R.id.itemNameTextView)
        itemRatingBar = findViewById(R.id.itemRatingBar)
        itemPriceTextView = findViewById(R.id.itemPriceTextView)
        borrowButton = findViewById(R.id.borrowButton)
        nextButton = findViewById(R.id.nextButton)
        searchView = findViewById(R.id.searchView)

        // Sample data
        items = listOf(
            RentalItem(
                name = "Electric Guitar",
                rating = 4.5f,
                features = listOf("6 Strings", "Solid Body", "Sunburst Finish"),
                pricePerMonth = 100,
                imageResId = R.drawable.eguitar
            ),
            RentalItem(
                name = "Drum Set",
                rating = 4.0f,
                features = listOf("5 Pieces", "Includes Cymbals", "Black Finish"),
                pricePerMonth = 150,
                imageResId = R.drawable.drum
            ),
            RentalItem(
                name = "Classical Piano",
                rating = 4.0f,
                features = listOf("88 Keys", "Wooden Finish", "Includes Bench"),
                pricePerMonth = 350,
                imageResId = R.drawable.piano
            )
        )

        // Restore saved instance state
        savedInstanceState?.let {
            currentIndex = it.getInt(CURRENT_INDEX_KEY, 0)
        }

        // Display the first item
        displayItem(currentIndex)

        // Set up button listeners
        nextButton.setOnClickListener {
            currentIndex = (currentIndex + 1) % items.size
            Log.d("MainActivity", "Next button clicked, currentIndex: $currentIndex")
            displayItem(currentIndex)
        }

        borrowButton.setOnClickListener {
            Intent(this, BorrowActivity::class.java).apply {
                putExtra("rentalItem", items[currentIndex])
                Log.d("MainActivity", "Borrow button clicked, item: ${items[currentIndex].name}")
                startActivityForResult(this, DETAIL_REQUEST_CODE)
            }
        }

        // Set up search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    items.indexOfFirst { item -> item.name.contains(query, ignoreCase = true) }
                        .takeIf { it != -1 }
                        ?.let { index ->
                            currentIndex = index
                            displayItem(currentIndex)
                        }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_INDEX_KEY, currentIndex)
        Log.d("MainActivity", "onSaveInstanceState called, currentIndex: $currentIndex")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentIndex = savedInstanceState.getInt(CURRENT_INDEX_KEY, 0)
        displayItem(currentIndex)
        Log.d("MainActivity", "onRestoreInstanceState called, currentIndex: $currentIndex")
    }

    private fun displayItem(index: Int) {
        items[index].let { item ->
            Log.d("MainActivity", "Displaying item: ${item.name}")
            itemImageView.setImageResource(item.imageResId)
            itemNameTextView.text = item.name
            itemRatingBar.rating = item.rating
            itemPriceTextView.text = "${item.pricePerMonth} credits/month"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DETAIL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<RentalItem>("updatedRentalItem")?.let { updatedItem ->
                items = items.toMutableList().apply {
                    set(currentIndex, updatedItem)
                }
                displayItem(currentIndex)
            }
        }
    }
}