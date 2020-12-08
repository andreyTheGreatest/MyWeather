package space.manokhin.myweather.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.androidnetworking.AndroidNetworking.initialize
import com.facebook.common.util.UriUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.google.ar.core.ArCoreApk
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_weather.*
import org.json.JSONArray
import org.json.JSONObject
import space.manokhin.myweather.R
import space.manokhin.myweather.adapters.WeatherAdapter
import space.manokhin.myweather.db.City
import space.manokhin.myweather.db.Forecast
import space.manokhin.myweather.db.RoomDB
import space.manokhin.myweather.request.RequestHandler
import space.manokhin.myweather.request.RequestListener
import space.manokhin.myweather.utils.Constants
import space.manokhin.myweather.utils.Constants.Companion.WEATHER_URL
import space.manokhin.myweather.utils.Constants.Companion.get3DAssetByWeather
import space.manokhin.myweather.utils.HttpUtils
import space.manokhin.myweather.utils.JsonToCityConverter
import space.manokhin.myweather.utils.JsonToForecastConverter
import java.io.UnsupportedEncodingException
import java.util.*
import java.util.concurrent.CompletableFuture


class WeatherActivity : AppCompatActivity() {

    private lateinit var obj: JSONObject
    private lateinit var curWeatherFuture: CompletableFuture<JSONObject>
    private lateinit var forecastFuture: CompletableFuture<JSONObject>
    private lateinit var city: City
    private lateinit var cityName: TextView
    private lateinit var temperature: TextView
    private lateinit var weather: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var gif: SimpleDraweeView
    private lateinit var realFeel: TextView
    private lateinit var windSpeed: TextView
    private lateinit var database: RoomDB
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var forecasts: MutableList<Forecast>
    private var gradient: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_weather)
        initialize(applicationContext)
        maybeEnableArButton()

        database = RoomDB.getInstance(this@WeatherActivity)

        initiateViews()

        val value = intent.getIntExtra("city_id", 0)
        city = database.cityDao().findById(value)
        forecasts = database.forecastDao().getByCityId(city.id)
        if (forecasts.size > 0) {

            adjustForecastsView(forecasts.take(3))
            requestCurrentWeatherInfoByCityName(city.name)
        }
        if (city.icon!!.isNotEmpty()) fillViews()

        // set pull to refresh listener
        pullToRefresh = findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            requestCurrentWeatherInfoByCityName(city.name)
            pullToRefresh.isRefreshing = false
        }

        requestCurrentWeatherInfoByCityName(city.name)

        val timeStamp: Int = (System.currentTimeMillis()/1000).toInt()
        if (forecasts.size == 0 || timeStamp - forecasts[0].currentDay > 86400) {
            Log.d("DAY HAS PASSED", "$timeStamp")
            //CompletableFuture.allOf(curWeatherFuture)

            reqCurWeatherWeekInfoByCityLocation()
        }
    }

    private fun initiateViews() {
        gif = findViewById(R.id.gif)
        cityName = findViewById(R.id.cityName)
        temperature = findViewById(R.id.temperature)
        weather = findViewById(R.id.weather)
        weatherIcon = findViewById(R.id.weatherIcon)
        realFeel = findViewById(R.id.realFeel)
        windSpeed = findViewById(R.id.windSpeed)
        recyclerView = findViewById(R.id.recycler_view_weather)
    }

//    private fun forwardGeocoding(name: String) {
//        val reqParams = RequestParams("json", 1)
//        Log.d("GEOCODING REQUEST", "---------------- attempting request.....")
//        val serverResp = calculateAsync(reqParams, LOCATION_URL, "$name?").get()
//        reqCurWeatherWeekInfoByCityLocation(
//            serverResp["longt"].toString(),
//            serverResp["latt"].toString()
//        )
//    }


    private fun  calculateAsync(reqParams: RequestParams, url: String, addition_url: String): CompletableFuture<JSONObject> {

        return CompletableFuture
            .supplyAsync {
                runOnUiThread {
                    startLoading()
                }
                val handler = RequestHandler()
                var resp: JSONObject? = null
                HttpUtils(url).wrapperGet(handler, applicationContext, addition_url, reqParams, object: RequestListener {
                    override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                        try {
                            resp = JSONObject(response.toString())
                            Log.v("Server response","$response")
                        } catch ( e1: UnsupportedEncodingException) {
                            e1.printStackTrace()
                        }
                    }
                })
                resp!!
            }
            .thenApply {
                runOnUiThread {
                    stopLoading()
                }
                it
            }
    }

    private fun requestCurrentWeatherInfoByCityName(value: String?) {
        val reqParams = RequestParams("appid", Constants.API_KEY)
        reqParams.add("q", value)
        reqParams.add("units", "metric")
        Log.d("CURRENT REQUEST", "---------------- attempting request.....")
        curWeatherFuture = calculateAsync(reqParams, WEATHER_URL, "/weather?")


        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            JsonToCityConverter(curWeatherFuture.get()).convert(city)
            runOnUiThread {
                fillViews()
            }
        }
        mainHandler.post(myRunnable)
    }

    private fun startLoading() {
        update.text = "updating..."
        animation.setAnimation("loading.json")
        animation.playAnimation()
    }

    private fun stopLoading() {
        update.text = "updated"
        animation.setAnimation("success.json")
        animation.progress = 0.0f
        animation.playAnimation()
        animation.addAnimatorUpdateListener {
            val progress = (it.animatedValue as Float * 100).toInt()
            if (progress > 90) {
                update.text = ""
                animation.cancelAnimation()
                animation.visibility = View.GONE
            }
        }
    }

    private fun reqCurWeatherWeekInfoByCityLocation() {

        val reqParams = RequestParams("appid", Constants.API_KEY)
        reqParams.add("exclude", "hourly,minutely")
        reqParams.add("units", "metric")
        Log.d("WEEK REQUEST", "---------------- attempting request.....")


        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            Log.d("CITY AFTER CURWEATHER", "---------------- $city")
            reqParams.add("lon", city.lon)
            reqParams.add("lat", city.lat)
            forecastFuture = calculateAsync(reqParams, WEATHER_URL, "/onecall?")
            val forecast = JSONArray(forecastFuture.get()["daily"].toString())
            val dataList: MutableList<Forecast> = mutableListOf()
            for (i in 0 until forecast.length()) {
                Log.d("FORECAST", "---------------- ${forecast[i]}")
                val item = forecast.getJSONObject(i)
                dataList.add(JsonToForecastConverter(item).convert(city.id))
            }
            database.forecastDao().insertAll(dataList.toList())
            runOnUiThread {
                Log.d("DATALIST", "---------------- ${dataList[0]}")
                adjustForecastsView(dataList.take(3))
            }
        }
        mainHandler.post(myRunnable)
    }

    private fun adjustForecastsView(cityList: List<Forecast>) {
        linearLayoutManager = LinearLayoutManager(this@WeatherActivity)
        weatherAdapter = WeatherAdapter(this@WeatherActivity, cityList)
        recyclerView.post {
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.adapter = weatherAdapter
        }
    }

    private fun fillViews() {

        cityName.text = city.name
        temperature.text = city.temp!!.toString()
        weather.text = city.weather
        realFeel.text = "${city.feels_like} °C"
        windSpeed.text = "${city.wind} m/s"

        val sunrise = city.sunrise!!.toInt()
        val sunset = city.sunset!!.toInt()
        val dt = city.dt!!.toInt()
        val dtsr = dt - sunrise
        val dtss = dt - sunset
        gradient = when {
            dtsr < 0 -> R.drawable.darkest_sky_gradient
            dtss < 0 -> R.drawable.brightest_sky_gradient
            dtss > dtsr -> R.drawable.brightest_sky_gradient
            else -> R.drawable.darkest_sky_gradient
        }
        Log.d("WEATHER", "---------------- ${city.weather}")
        val controller: DraweeController = Fresco.newDraweeControllerBuilder()
            .setUri(
                Uri.Builder()
                    .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                    .path(
                        if (city.weather == "Clear" && gradient == R.drawable.darkest_sky_gradient)
                            Constants.getGifByWeather("Clear_night")!!.toString()
                        else if (city.weather == "Clear") {
                            Constants.getGifByWeather("Clear_day")!!.toString()
                        } else Constants.getGifByWeather(city.weather!!)!!.toString()
                    )
                    .build()
            )
            .setAutoPlayAnimations(true)
            .build()


        gif.controller = controller
        background.setBackgroundResource(gradient)
        database.cityDao().update(city)
    }

    fun showCityManager(view: View) {
        val intent = Intent(this@WeatherActivity, MainActivity::class.java)
        if (background.background.constantState == resources.getDrawable(R.drawable.darkest_sky_gradient).constantState)
            intent.putExtra("background", R.drawable.darkest_sky_gradient)
        else intent.putExtra("background", R.drawable.brightest_sky_gradient)
        startActivity(intent)
    }

    fun startAR(view: View) {
        val intent = Intent(this@WeatherActivity, ARActivity::class.java)
        intent.putExtra("weather",
            if (city.weather == "Clear" && gradient == R.drawable.darkest_sky_gradient)
                "Clear_night"
            else if (city.weather == "Clear")
                "Clear_day"
            else city.weather!!)
        startActivity(intent)
    }

    private fun maybeEnableArButton() {
        val availability = ArCoreApk.getInstance().checkAvailability(this)
        if (availability.isTransient) {
            // Re-query at 5Hz while compatibility is checked in the background.
            Handler().postDelayed({
                maybeEnableArButton()
            }, 200)
        }
        if (availability.isSupported) {
            settings.visibility = View.VISIBLE
            settings.isEnabled = true
        }
        else {
            settings.visibility = View.INVISIBLE
            settings.isEnabled = false
        }
    }
    //        val sceneViewerIntent = Intent(Intent.ACTION_VIEW)
//        val asset = when {
//            city.weather == "Clear" && gradient == R.drawable.darkest_sky_gradient ->
//                get3DAssetByWeather("Clear_night")!!.toString()
//            city.weather == "Clear" ->
//                get3DAssetByWeather("Clear_day")!!.toString()
//            else -> get3DAssetByWeather(city.weather!!)
//        }
//        val requestUrl = "https://arvr.google.com/scene-viewer/1.0?file=$asset"
//        Log.d("3D ASSET URL", requestUrl)
//        val sample =
//            "https://arvr.google.com/scene-viewer/1.0?file=https://raw.githubusercontent.com/andreyTheGreatest/gltf_assets/main/sun/sun_02.gltf"
//        val dummy =
//            "https://arvr.google.com/scene-viewer/1.0?file=https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Duck/glTF/Duck.gltf"
//        sceneViewerIntent.data = Uri.parse(sample)
//        sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox")
//        startActivity(sceneViewerIntent)
}
