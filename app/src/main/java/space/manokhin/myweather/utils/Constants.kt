package space.manokhin.myweather.utils

import space.manokhin.myweather.R

class Constants {
    companion object {
        private val DESCRIPTIONS = mapOf("Clear_night" to R.drawable.clear_night,
            "Clear_day" to R.drawable.sun,
            "Clouds" to R.drawable.clouds_grey,
            "Fog" to R.drawable.mist,
            "Mist" to R.drawable.mist,
            "Snow" to R.drawable.snow,
            "Rain" to R.drawable.rain_dynamic,
            "Drizzle" to R.drawable.rain_dynamic
        )

        const val API_KEY = "8904e67811989b14b61148a26a941300"
        private const val GLTF_ASSET_FOLDER =
            "https://raw.githubusercontent.com/andreyTheGreatest/gltf_assets/main/"
        private const val rain = "rain/model.gltf"
        private const val cloud = "cloud/cloud_02.gltf"
        private const val star = "star/Star_01.gltf"
        private const val sun = "sun/sun_02.gltf"

        private const val fog = "fog/fog.glb"
        private const val snow = "snow/snow.gltf"
        private val ASSETS = mapOf(
            "Clear_night" to star,
            "Clear_day" to sun,
            "Clouds" to cloud,
            "Fog" to fog,
            "Mist" to fog,
            "Snow" to snow,
            "Rain" to rain,
            "Drizzle" to rain
        )
        const val WEATHER_URL = "https://api.openweathermap.org/data/2.5"
        const val LOCATION_URL = "https://geocode.xyz/"
        fun ICON_URL(icon: String) = "https://openweathermap.org/img/wn/$icon@4x.png"
        fun get3DAssetByWeather(weather: String): String? {
            return ASSETS[weather]
        }
        fun getGifByWeather(weather: String): Int? {
            return DESCRIPTIONS[weather]
        }
        fun getGradientByTime(delimiter: Float): Int {
            return when {
                (delimiter > 0) -> R.drawable.brightest_sky_gradient
                else -> R.drawable.darkest_sky_gradient
            }
        }
    }
}