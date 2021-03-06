package com.gkzxhn.helpout.model

import android.content.Context
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import rx.Observable


/**
 * Explanation:
 * @author LSX
 *    -----2018/9/6
 */

interface IPhoneChangeModel : IBaseModel {

    fun getCode(context: Context,map: RequestBody): Observable<Response<Void>>


    fun login(context: Context, body: RequestBody): Observable<Response<Void>>

    fun updatePhoneNumber(context: Context, body: RequestBody): Observable<Response<Void>>
    fun getToken(context: Context, phoneNumber: String, code: String): Observable<Response<ResponseBody>>?
}