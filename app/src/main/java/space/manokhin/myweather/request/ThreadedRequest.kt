package space.manokhin.myweather.request

import android.content.Context
import android.os.Handler
import android.util.Log
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_weather.*
import org.json.JSONObject
import space.manokhin.myweather.utils.HttpUtils
import java.io.UnsupportedEncodingException
import java.util.concurrent.CompletableFuture


class ThreadedRequest {
    lateinit var url: String
    lateinit var mHandler: Handler
    lateinit var pRunnable: Runnable
    lateinit var addition_url: String
    lateinit var ctx: Context
    lateinit var reqParams: RequestParams
    lateinit var data: JSONObject
    var stausCode: Int = 200

    constructor(newUrl: String, addUrl: String, requestParams: RequestParams, context: Context, jobj: JSONObject) {
        url = newUrl;
        addition_url = addUrl
        ctx = context
        reqParams = requestParams
        data = jobj
        mHandler = Handler()
    }

    fun start(newRun: Runnable) {
        pRunnable = newRun
        processRequest.start()
    }

    private val processRequest = object:Thread() {
        override fun run() {
             val res = CompletableFuture
                .supplyAsync {
                    val handler = RequestHandler()
                    var resp: JSONObject? = null
                    HttpUtils(url).wrapperGet(handler, ctx, addition_url, reqParams, object: RequestListener {
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
                }.get()

            data = res
            mHandler.post(pRunnable)
        }
    }
}