package com.gkzxhn.helpout.model.iml

import android.content.Context
import com.gkzxhn.helpout.entity.AlipaySign
import com.gkzxhn.helpout.entity.LivesCircle
import com.gkzxhn.helpout.model.ILivesCircleModel
import com.gkzxhn.helpout.net.RetrofitClient
import com.gkzxhn.helpout.net.RetrofitClientChat
import rx.Observable
import rx.schedulers.Schedulers


/**
 * Explanation:
 * @author LSX
 *    -----2018/9/6
 */

class LivesCircleModel : BaseModel(), ILivesCircleModel {

    override fun getLivesCircle(context: Context,page :String,size:String): Observable<LivesCircle> {
        return RetrofitClientChat.Companion.getInstance(context).mApi
                .getLivesCircle(page,size,"createdTime,desc")
                .subscribeOn(Schedulers.io())
                as Observable<LivesCircle>
    }



}