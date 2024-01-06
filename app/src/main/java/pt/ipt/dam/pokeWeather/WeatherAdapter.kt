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
import pt.ipt.dam.pokeWeather.model.WeatherItem

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onSaveClick(position: Int, newText: String)
    }

    private val weatherItems = mutableListOf<String>()

    private var itemClickListener: OnItemClickListener? = null

    private val itemsInEditMode = mutableSetOf<Int>()

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weatherTextView: TextView = itemView.findViewById(R.id.weatherTextView)
        val editWeatherTextView: EditText = itemView.findViewById(R.id.editWeatherTextView)
        val btnSave: Button = itemView.findViewById(R.id.btnSave)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout.weather_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weatherItem = weatherItems[position]

        if (isEditMode(position)) {
            // Edit mode
            holder.weatherTextView.visibility = View.GONE
            holder.editWeatherTextView.visibility = View.VISIBLE
            holder.btnSave.visibility = View.VISIBLE
            holder.btnDelete.visibility = View.GONE

            holder.editWeatherTextView.setText(weatherItem)

            holder.btnSave.setOnClickListener {
                itemClickListener?.onSaveClick(position, holder.editWeatherTextView.text.toString())
            }
        } else {
            // Display mode
            holder.weatherTextView.visibility = View.VISIBLE
            holder.editWeatherTextView.visibility = View.GONE
            holder.btnSave.visibility = View.GONE
            holder.btnDelete.visibility = View.VISIBLE

            holder.weatherTextView.text = weatherItem

            holder.weatherTextView.setOnClickListener {
                enterEditMode(position)
            }

            holder.btnDelete.setOnClickListener {
                itemClickListener?.onDeleteClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return weatherItems.size
    }

    fun addWeatherItem(weatherItem: String) {
        weatherItems.add(weatherItem)
        notifyItemInserted(weatherItems.size - 1)
    }

    fun removeWeatherItem(position: Int) {
        if (position in 0 until weatherItems.size) {
            weatherItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun editWeatherItem(position: Int, newWeatherItem: String) {
        weatherItems[position] = newWeatherItem
        notifyItemChanged(position)
    }

    fun updateWeatherItem(position: Int, newText: String) {
        if (position in 0 until weatherItems.size) {
            weatherItems[position] = newText
            notifyItemChanged(position)
        }
    }

    fun setWeatherItems(items: List<String>) {
        weatherItems.clear()
        weatherItems.addAll(items)
        notifyDataSetChanged()
    }
    fun getWeatherItems(): List<String> {
        return weatherItems.toList()
    }

    private fun isEditMode(position: Int): Boolean {
        return position in itemsInEditMode
    }

    private fun enterEditMode(position: Int) {
        itemsInEditMode.add(position)
        notifyDataSetChanged()
    }

    fun exitEditMode(position: Int) {
        if (position in itemsInEditMode) {
            itemsInEditMode.remove(position)
            notifyItemChanged(position)
        }
    }
}