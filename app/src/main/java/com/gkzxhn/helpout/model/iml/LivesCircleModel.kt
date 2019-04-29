package com.gkzxhn.helpout.model.iml

import android.content.Context
import com.gkzxhn.helpout.entity.LivesCircle
import com.gkzxhn.helpout.model.ILivesCircleModel
import com.gkzxhn.helpout.net.RetrofitClientChat
import okhttp3.RequestBody
import okhttp3.ResponseBody
import rx.Observable
import rx.schedulers.Schedulers


/**
 * Explanation:
 * @author LSX
 *    -----2018/9/6
 */

class LivesCircleModel : BaseModel(), ILivesCircleModel {
    override fun getLivesCircleByUserName(context: Context, userName: String, page: String, size: String): Observable<LivesCircle> {
        return RetrofitClientChat.Companion.getInstance(context).mApi
                .getLivesCircleByUserName(userName,page, size, "createdTime,desc")
                .subscribeOn(Schedulers.io())
                as Observable<LivesCircle>
    }

    override fun praise(context: Context, body: RequestBody): Observable<ResponseBody> {
        return RetrofitClientChat.Companion.getInstance(context).mApi
                .praise(body)
                .subscribeOn(Schedulers.io())
                as Observable<ResponseBody>
    }override fun comment(context: Context, body: RequestBody): Observable<ResponseBody> {
        return RetrofitClientChat.Companion.getInstance(context).mApi
                .comment(body)
                .subscribeOn(Schedulers.io())
                as Observable<ResponseBody>
    }

    override fun getLivesCircle(context: Context, page: String, size: String): Observable<LivesCircle> {
        return RetrofitClientChat.Companion.getInstance(context).mApi
                .getLivesCircle(page, size)
                .subscribeOn(Schedulers.io())
                as Observable<LivesCircle>
    }

    override fun getMyLivesCircle(context: Context, page: String, size: String): Observable<LivesCircle> {
        return RetrofitClientChat.Companion.getInstance(context).mApi
                .getMyLivesCircle(page, size, "createdTime,desc")
                .subscribeOn(Schedulers.io())
                as Observable<LivesCircle>
    }


}