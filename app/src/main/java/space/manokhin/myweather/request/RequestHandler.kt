package space.manokhin.myweather.request

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import cz.msebera.android.httpclient.Header
import android.view.View
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.loopj.android.http.*
import org.json.JSONObject
import java.io.UnsupportedEncodingException


class RequestHandler() {

    private val client: SyncHttpClient = SyncHttpClient()

    // You can add more parameters if you need here.
    fun makeRequest(context: Context, reqParams: RequestParams, url: String, listener: RequestListener) {
        SyncHttpClient().get(url, reqParams, object : JsonHttpResponseHandler() {


            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                listener.onSuccess(statusCode, headers, response)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseString: String?,
                throwable: Throwable?
            ) {
                Log.e("▒▒▒▒▒▒▒ GET FAILED ", url)

                val builder = AlertDialog.Builder(context)
                builder.setTitle("▒▒▒▒▒ ERROR ▒▒▒▒▒")
                var error_msg: String? = null
                try {
                    error_msg = String(responseString!!.toByteArray(), Charsets.UTF_8)
                } catch (e1: UnsupportedEncodingException) {
                    error_msg = e1.localizedMessage
                }

                builder.setMessage(context.javaClass.simpleName + " -> " + error_msg)
                    .setCancelable(true)
                    .setPositiveButton("OK")
                    { dialog, _ -> dialog.dismiss() }
                val alert = builder.create()
                alert.show()
            }

            override fun onRetry(retryNo: Int) {
                Log.e("▒▒▒▒▒▒▒ RETRYING ", ".....$retryNo")
            }
        })
    }
}

