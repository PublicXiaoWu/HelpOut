package com.gkzxhn.helpout.model.iml

import android.content.Context
import com.gkzxhn.helpout.entity.LawyersInfo
import com.gkzxhn.helpout.model.IConversationModel
import com.gkzxhn.helpout.net.RetrofitClient
import rx.Observable
import rx.schedulers.Schedulers


/**
 * Explanation:
 * @author LSX
 *    -----2018/9/6
 */

class ConversationModel : BaseModel(), IConversationModel {

    override fun getLawyersInfo(context: Context): Observable<LawyersInfo> {
        return RetrofitClient.getInstance(context).mApi?.getLawyersInfo()?.subscribeOn(Schedulers.io()) as Observable<LawyersInfo>
    }


}