package com.gkzxhn.helpout.model.iml

import android.content.Context
import com.gkzxhn.helpout.model.IPhoneChangeModel
import com.gkzxhn.helpout.net.RetrofitClientPublic
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import rx.Observable
import rx.schedulers.Schedulers


/**
 * Explanation:
 * @author LSX
 *    -----2018/9/6
 */

class PhoneChangeModel : BaseModel(), IPhoneChangeModel {

    override fun login(context: Context, body: RequestBody): Observable<Response<Void>> {
        return RetrofitClientPublic.Companion.getInstance(context).mApi
                .login(body)
                .subscribeOn(Schedulers.io()) as Observable<Response<Void>>
    }

    override fun updatePhoneNumber(context: Context, body: RequestBody): Observable<Response<Void>> {
        return RetrofitClientPublic.Companion.getInstance(context).mApi
                .updatePhoneNumber(body)
                .subscribeOn(Schedulers.io()) as Observable<Response<Void>>

    }

    override fun getCode(context: Context, map: RequestBody): Observable<Response<Void>> {
        return RetrofitClientPublic.Companion.getInstance(context).mApi
                .getCode(map)
                .subscribeOn(Schedulers.io()) as Observable<Response<Void>>
    }


    override fun getToken(context: Context, phoneNumber: String, code: String): Observable<Response<ResponseBody>>? {
        return RetrofitClientPublic.Companion.getInstance(context)
                .mApi.getToken("password", phoneNumber, code)
                .subscribeOn(Schedulers.io())
    }
}