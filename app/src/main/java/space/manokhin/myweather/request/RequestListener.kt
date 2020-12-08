package space.manokhin.myweather.request

import cz.msebera.android.httpclient.Header
import org.json.JSONObject

interface RequestListener {
    fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject)
}