package com.example.dessertpusher

import android.content.ActivityNotFoundException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ShareCompat
import com.example.dessertpusher.databinding.ActivityMainBinding
import timber.log.Timber

const val KEY_REVENUE = "key_revenue"
const val KEY_DESSERTS_SOLD = "desserts_sold"
const val KEY_DESSERT_TIMER = "dessert_timer"

class MainActivity : AppCompatActivity() {

    private var revenue = 0
    private var dessertsSold = 0
    private lateinit var dessertTimer: DessertTimer

    // Contains all the views
    private lateinit var binding: ActivityMainBinding

    /**
     * Simple data class that represents a dessert. Includes the resource id integer associeted with
     * the image, the price it's sold for, and the startProductionAmount, wich determines when
     * the dessert starts to be produced.
     */
    data class Dessert(val imageId: Int, val price: Int, val startProductionAmount: Int)

    // Create a list of all desserts, in order of when they start being produced
    private val allDesserts = listOf(
        Dessert(R.drawable.cupcake, 5, 0),
        Dessert(R.drawable.donut, 10, 5),
        Dessert(R.drawable.eclair, 15, 20),
        Dessert(R.drawable.froyo, 30, 50),
        Dessert(R.drawable.gingerbread, 50, 100),
        Dessert(R.drawable.honeycomb, 100, 200),
        Dessert(R.drawable.icecreamsandwich, 500, 500),
        Dessert(R.drawable.jellybean, 1000, 1000),
        Dessert(R.drawable.kitkat, 2000, 2000),
        Dessert(R.drawable.lollipop, 3000, 4000),
        Dessert(R.drawable.marshmallow, 4000, 8000),
        Dessert(R.drawable.nougat, 5000, 16000),
        Dessert(R.drawable.oreo, 6000, 20000)
    )
    private var currentDessert = allDesserts[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate called!")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dessertButton.setOnClickListener {
            onDessertClicked()
        }

        dessertTimer = DessertTimer(this.lifecycle)


        savedInstanceState?.let {
            revenue = savedInstanceState.getInt(KEY_REVENUE)
            dessertsSold = savedInstanceState.getInt(KEY_DESSERTS_SOLD)
            dessertTimer.secondsCount =savedInstanceState.getInt(KEY_DESSERT_TIMER)
        }

        // Set the TextViews to the right values
        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Make sure the correct dessert is showing
        binding.dessertButton.setImageResource(currentDessert.imageId)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_REVENUE, revenue)
        outState.putInt(KEY_DESSERTS_SOLD, dessertsSold)
        outState.putInt(KEY_DESSERT_TIMER, dessertTimer.secondsCount)
        Timber.i("onSaveInstanceState called!")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        Timber.i("onStart called!")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume called!")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy called!")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause called!")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop called!")
    }

    /**
     * Updates the score when the dessert is clicked. Possibly shows a new dessert.
     */
    private fun onDessertClicked() {

        // Update the score
        revenue += currentDessert.price
        dessertsSold++

        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Show the next dessert
        showCurrentDessert()
    }

    /**
     * Determine the dessert to show.
     */
    private fun showCurrentDessert() {
        var newDessert = allDesserts[0]
        for (dessert in allDesserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                newDessert = dessert
            }
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            else break
        }

        // If the new dessert is actually different thant the current dessert, update the image
        if (newDessert != currentDessert) {
            currentDessert = newDessert
            binding.dessertButton.setImageResource(newDessert.imageId)
        }
    }

    /**
     * Menu methods
     */
    private fun onShare() {
        val shareIntent = ShareCompat.IntentBuilder.from(this)
            .setText(getString(R.string.share_text, dessertsSold, revenue))
            .setType("text/plain")
            .intent
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.sharing_not_available),
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareMenuButton -> onShare()
        }
        return super.onOptionsItemSelected(item)
    }
}