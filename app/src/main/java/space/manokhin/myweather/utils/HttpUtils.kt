package space.manokhin.myweather.utils

import android.content.Context
import android.util.Log
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.SyncHttpClient
import space.manokhin.myweather.request.RequestHandler
import space.manokhin.myweather.request.RequestListener


class HttpUtils(url: String) {

    private val BASE_URL = url

    private val client = SyncHttpClient()
    private val asyncClient = AsyncHttpClient()

    fun get(
        url: String,
        rp: RequestParams,
        responseHandler: AsyncHttpResponseHandler
    ) {
        Log.d("CONSTANTS", "${getAbsoluteUrl(url)}")
        asyncClient.get(
            getAbsoluteUrl(
                url
            ), rp, responseHandler)
    }

    fun wrapperGet(
        handler: RequestHandler,
        context: Context,
        url: String,
        rp: RequestParams,
        responseHandler: RequestListener
    ) {
        Log.d("CONSTANTS", "${getAbsoluteUrl(url)}")
        handler.makeRequest(context, rp, getAbsoluteUrl(url), responseHandler)
    }

    private fun getAbsoluteUrl(relativeUrl: String): String {
        return BASE_URL + relativeUrl
    }
}