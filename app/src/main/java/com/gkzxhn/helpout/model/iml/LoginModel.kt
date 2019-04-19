package com.gkzxhn.helpout.model.iml

import android.content.Context
import com.gkzxhn.helpout.entity.AccountInfo
import com.gkzxhn.helpout.entity.ImInfo
import com.gkzxhn.helpout.entity.LawyersInfo
import com.gkzxhn.helpout.model.ILoginModel
import com.gkzxhn.helpout.net.RetrofitClient
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

class LoginModel : BaseModel(), ILoginModel {
    override fun login(context: Context, body: RequestBody): Observable<Response<Void>> {
        return RetrofitClientPublic.Companion.getInstance(context).mApi
                ?.login(body)
                ?.subscribeOn(Schedulers.io()) as Observable<Response<Void>>

    }

    override fun getCode(context: Context, map: RequestBody): Observable<Response<Void>> {
        return RetrofitClientPublic.Companion.getInstance(context).mApi
                .getCode(map)
                .subscribeOn(Schedulers.io()) as Observable<Response<Void>>
    }

    override fun getLawyersInfo(context: Context): Observable<LawyersInfo> {
        return RetrofitClient.getInstance(context).mApi?.getLawyersInfo()?.subscribeOn(Schedulers.io()) as Observable<LawyersInfo>
    }

    override fun getToken(context: Context, phoneNumber: String, code: String): Observable<Response<ResponseBody>>? {
        return RetrofitClientPublic.Companion.getInstance(context)
                .mApi?.getToken("password", phoneNumber, code)
                ?.subscribeOn(Schedulers.io())
    }

    override fun uploadCrash(context: Context, body: RequestBody): Observable<Response<Void>> {
        return RetrofitClientPublic.Companion.getInstance(context).mApi
                ?.uploadCrash(body)
                ?.subscribeOn(Schedulers.io())
                as Observable<Response<Void>>
    }

    override fun getIMInfo(context: Context): Observable<ImInfo> {
        return RetrofitClientPublic.Companion.getInstance(context).mApi
                ?.getImInfo()
                ?.subscribeOn(Schedulers.io())
                as Observable<ImInfo>
    }

    override fun getAccountInfo(context: Context): Observable<AccountInfo> {
        return RetrofitClientPublic.Companion.getInstance(context).mApi
                ?.getAccountInfo()
                ?.subscribeOn(Schedulers.io())
                as Observable<AccountInfo>
    }


}