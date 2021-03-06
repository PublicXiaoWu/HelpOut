package com.gkzxhn.helpout.net

import android.content.Context
import android.util.Base64
import android.util.Log
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.utils.NetworkUtils
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * Explanation: 公共平台相关
 * @author LSX
 *    -----2018/9/6
 */
class CacheInterceptorPublic(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response? {

        var request = chain?.request()
        if (NetworkUtils.isNetConneted(context)) {
            val credentials = "assistant.app" + ":" + "506a7b6dfc5d42fe857ea9494bb24014"
            val basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            val addHeader = request?.newBuilder()?.addHeader("Authorization", basic)

            val token = App.SP.getString(Constants.SP_TOKEN, "")
            if (token != null) {
                if (token.isNotEmpty()) {
                    val mtoken = "Bearer $token"
                    addHeader?.header("Authorization", mtoken)
                }
            }
            val method = addHeader?.method(request!!.method(), request.body())
            return chain?.proceed(method?.build())
        } else {
            Log.e("CacheInterceptor", " no network load cahe")
            request = request?.newBuilder()?.cacheControl(CacheControl.FORCE_CACHE)?.build()
            val response = chain?.proceed(request)
            //set cahe times is 3 days
            val maxStale = 60 * 60 * 24 * 3
            return response?.newBuilder()?.removeHeader("Pragma")?.removeHeader("Cache-Control")?.header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")?.build()
        }
    }
}
