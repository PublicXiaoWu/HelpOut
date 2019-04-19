package com.gkzxhn.helpout.net

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
 * Explanation:聊天后台相关请求
 * @author LSX
 *    -----2018/9/6
 */
class RetrofitClientChat private constructor(context: Context, baseUrl: String) {
    var httpCacheDirectory: File? = null
    val mContext: Context = context
    var cache: Cache? = null
    var okHttpClient: OkHttpClient? = null
    lateinit var retrofit: Retrofit
    /****** 超时时间（秒） ******/
    val DEFAULT_TIMEOUT: Long = 3
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
                .addInterceptor(CacheInterceptorChat(mContext))
//                .addNetworkInterceptor(CacheInterceptor(mContext))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build()
        //retrofit创建了
        retrofit = Retrofit.Builder()
                .client(okHttpClient)
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(MyGsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(url)
                .build()
        return retrofit.create(ApiService::class.java)
    }

    companion object {
        private var instance: RetrofitClientChat? = null
        fun getInstance(context: Context, baseUrl: String): RetrofitClientChat {
            if (instance == null) {
                synchronized(RetrofitClientChat::class) {
                    if (instance == null) {
                        instance = RetrofitClientChat(context, baseUrl)
                    }
                }
            }
            return instance!!
        }

        fun getInstance(context: Context): RetrofitClientChat {
            if (instance == null) {
                synchronized(RetrofitClientChat::class) {
                    if (instance == null) {
                        instance = RetrofitClientChat(context, NetWorkCodeInfo.BASE_URL_CHAT)
                    }
                }
            }
            return instance!!
        }
    }

}