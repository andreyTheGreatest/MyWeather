package space.manokhin.myweather.utils

import org.json.JSONArray
import org.json.JSONObject
import space.manokhin.myweather.db.Forecast

class JsonToForecastConverter(private val json: JSONObject) {
    private val temp = json["temp"] as JSONObject
    private val weather = (json["weather"] as JSONArray)[0] as JSONObject

    fun convert(city_id: Int): Forecast {
        var forecast = Forecast(city_id = city_id)
        forecast.weather = weather["main"].toString()
        forecast.weatherIcon = weather["icon"].toString()
        forecast.tempDay = temp["max"].toString().toFloat().toInt()
        forecast.tempNight = temp["min"].toString().toFloat().toInt()
        forecast.currentDay = json["dt"].toString().toInt()
        return forecast
    }
}