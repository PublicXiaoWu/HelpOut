package com.gkzxhn.helpout.model

import android.content.Context
import com.gkzxhn.helpout.entity.AccountInfo
import com.gkzxhn.helpout.entity.ImInfo
import com.gkzxhn.helpout.entity.LawyersInfo
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import rx.Observable


/**
 * Explanation:
 * @author LSX
 *    -----2018/9/6
 */

interface ILoginModel : IBaseModel {


    fun getLawyersInfo(context: Context): Observable<LawyersInfo>

    fun login(context: Context, body: RequestBody): Observable<Response<Void>>

    fun getToken(context: Context, phoneNumber: String, code: String): Observable<Response<ResponseBody>>?

    fun uploadCrash(context: Context, body: RequestBody): Observable<Response<Void>>

    fun getIMInfo(context: Context): Observable<ImInfo>
    fun getAccountInfo(context: Context): Observable<AccountInfo>
    fun getCode(context: Context, map: RequestBody): Observable<Response<Void>>
}