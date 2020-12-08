package space.manokhin.myweather.utils

import org.json.JSONArray
import org.json.JSONObject
import space.manokhin.myweather.db.City

class JsonToCityConverter(jsonObj: JSONObject) {

    private val coord = getSubObject(jsonObj, "coord")
    private val main = getSubObject(jsonObj, "main")
    private val weather = (jsonObj.get("weather") as JSONArray)[0] as JSONObject
    private val wind = getSubObject(jsonObj, "wind")
    private val system = getSubObject(jsonObj, "sys")
    private val dt = jsonObj.get("dt")

    fun convert(city: City) {
        city.temp = main["temp"].toString().toDouble().toInt()
        city.feels_like = main["feels_like"].toString().toDouble()
        city.weather = weather["main"].toString()
        city.sunrise = system["sunrise"].toString().toInt()
        city.sunset = system["sunset"].toString().toInt()
        city.wind = wind["speed"].toString().toDouble()
        city.icon = weather["icon"] as String
        city.dt = dt.toString().toInt()
        city.lon = coord["lon"].toString()
        city.lat = coord["lat"].toString()
    }

    private fun getSubObject(jsonObj: JSONObject, key: String): JSONObject {
        return jsonObj.get(key) as JSONObject
    }

}