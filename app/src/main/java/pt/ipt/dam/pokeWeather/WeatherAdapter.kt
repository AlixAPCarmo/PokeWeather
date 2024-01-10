package pt.ipt.dam.pokeWeather

// WeatherAdapter.kt

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam.pokeWeather.R.layout

// Create the WeatherAdapter class that extends RecyclerView.Adapter
class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    // Interface to define item click listener events
    interface OnItemClickListener {
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onSaveClick(position: Int, newText: String)
    }

    // List to store weather items
    private val weatherItems = mutableListOf<String>()

    // Item click listener instance
    private var itemClickListener: OnItemClickListener? = null

    // Set to track items in edit mode
    private val itemsInEditMode = mutableSetOf<Int>()

    // Setter method for item click listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    // ViewHolder class to hold references to views for each item in the RecyclerView
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weatherTextView: TextView = itemView.findViewById(R.id.weatherTextView)
        val editWeatherTextView: EditText = itemView.findViewById(R.id.editWeatherTextView)
        val btnSave: Button = itemView.findViewById(R.id.btnSave)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    // Called when RecyclerView needs a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for a single item view
        val view = LayoutInflater.from(parent.context).inflate(layout.weather_item, parent, false)
        return ViewHolder(view)
    }

    // Called to bind data to the views within the ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weatherItem = weatherItems[position]

        if (isEditMode(position)) {
            // Edit mode
            holder.weatherTextView.visibility = View.GONE
            holder.editWeatherTextView.visibility = View.VISIBLE
            holder.btnSave.visibility = View.VISIBLE
            holder.btnDelete.visibility = View.GONE

            // Set text in the EditText to the current weather item
            holder.editWeatherTextView.setText(weatherItem)

            // Set click listener for the Save button
            holder.btnSave.setOnClickListener {
                itemClickListener?.onSaveClick(position, holder.editWeatherTextView.text.toString())
            }
        } else {
            // Display mode
            holder.weatherTextView.visibility = View.VISIBLE
            holder.editWeatherTextView.visibility = View.GONE
            holder.btnSave.visibility = View.GONE
            holder.btnDelete.visibility = View.VISIBLE

            // Set text in the TextView to the current weather item
            holder.weatherTextView.text = weatherItem

            // Set click listeners for the TextView and Delete button
            holder.weatherTextView.setOnClickListener {
                enterEditMode(position)
            }

            holder.btnDelete.setOnClickListener {
                itemClickListener?.onDeleteClick(position)
            }
        }
    }

    // Returns the total number of items in the weather data set
    override fun getItemCount(): Int {
        return weatherItems.size
    }

    // Add a new weather item to the list and update the RecyclerView
    fun addWeatherItem(weatherItem: String) {
        weatherItems.add(weatherItem)
        notifyItemInserted(weatherItems.size - 1)
    }

    // Remove a weather item at the specified position and update the RecyclerView
    fun removeWeatherItem(position: Int) {
        if (position in 0 until weatherItems.size) {
            weatherItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // Edit a weather item at the specified position and update the RecyclerView
    fun editWeatherItem(position: Int, newWeatherItem: String) {
        weatherItems[position] = newWeatherItem
        notifyItemChanged(position)
    }

    // Update a weather item at the specified position with new text and update the RecyclerView
    fun updateWeatherItem(position: Int, newText: String) {
        if (position in 0 until weatherItems.size) {
            weatherItems[position] = newText
            notifyItemChanged(position)
        }
    }

    // Set the entire list of weather items and update the RecyclerView
    fun setWeatherItems(items: List<String>) {
        weatherItems.clear()
        weatherItems.addAll(items)
        notifyDataSetChanged()
    }

    // Get a copy of the current weather items
    fun getWeatherItems(): List<String> {
        return weatherItems.toList()
    }

    // Check if an item is in edit mode
    private fun isEditMode(position: Int): Boolean {
        return position in itemsInEditMode
    }

    // Enter edit mode for a specific item
    private fun enterEditMode(position: Int) {
        itemsInEditMode.add(position)
        notifyDataSetChanged()
    }

    // Exit edit mode for a specific item and update the RecyclerView
    fun exitEditMode(position: Int) {
        if (position in itemsInEditMode) {
            itemsInEditMode.remove(position)
            notifyItemChanged(position)
        }
    }
}