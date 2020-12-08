package space.manokhin.myweather.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import space.manokhin.myweather.R
import space.manokhin.myweather.db.Forecast
import java.text.SimpleDateFormat
import java.util.*


class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.ViewHolder>  {

    private var weather: List<Forecast>
    private var context: Context

    constructor(context: Context, weatherList: List<Forecast>) {
        this.weather = weatherList
        this.context = context
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).
                inflate(R.layout.list_row_weather, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.weather.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = weather[position]
        val iconUrl = "https://openweathermap.org/img/wn/${data.weatherIcon}.png"
        val sdf = SimpleDateFormat("EEEE")
        val time = data.currentDay

        val dateFormat = Date(time * 1000L)
        val weekday = sdf.format(dateFormat)
        with(holder) {
            weather.text = data.weather
            temperature.text = "${data.tempDay}° / ${data.tempNight}°"
            day.text = weekday
            Glide.with(context).load(iconUrl).into(icon)
        }
    }

    class ViewHolder: RecyclerView.ViewHolder {

        var icon: ImageView
        var weather: TextView
        var day: TextView
        var temperature: TextView

        constructor(itemView: View): super(itemView) {
            icon = itemView.findViewById(R.id.icon)
            weather = itemView.findViewById(R.id.weather)
            day = itemView.findViewById(R.id.day)
            temperature = itemView.findViewById(R.id.temperature)
        }
    }

}
