package com.gkzxhn.helpout.net

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.gkzxhn.helpout.net.error_exception.MyGsonConverterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * Explanation: 共同平台相关请求
 * @author LSX
 *    -----2018/9/6
 */

class RetrofitClientPublic private constructor(context: Context, baseUrl: String) {

    var httpCacheDirectory: File? = null
    val mContext: Context = context
    var cache: Cache? = null
    var okHttpClient: OkHttpClient? = null
    lateinit var retrofit: Retrofit
    val DEFAULT_TIMEOUT: Long = 6
    val url = baseUrl
    lateinit var mApi: ApiService

    init {
        //缓存地址
        if (httpCacheDirectory == null) {
            httpCacheDirectory = File(mContext.cacheDir, "app_cache")
        }
        try {
            if (cache == null) {
                cache = Cache(httpCacheDirectory, 10 * 1024 * 1024)
            }
        } catch (e: Exception) {
            Log.e("OKHttp", "Could not create http cache", e)
        }

        mApi = provideHotApi()

    }

    fun provideHotApi(): ApiService {
        //okhttp创建了
        okHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .retryOnConnectionFailure(true)
                .addInterceptor(CacheInterceptorPublic(mContext))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build()
        //retrofit创建了
        retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(MyGsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(url)
                .build()
        return retrofit.create(ApiService::class.java)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var instance: RetrofitClientPublic? = null
        fun getInstance(context: Context): RetrofitClientPublic {
            if (instance == null) {
                synchronized(RetrofitClientPublic::class) {
                    if (instance == null) {
                        instance = RetrofitClientPublic(context, NetWorkCodeInfo.BASE_URL)
                    }
                }
            }
            return instance!!
        }
    }

}