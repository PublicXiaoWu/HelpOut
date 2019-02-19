package com.gkzxhn.helpout.model

import android.content.Context
import com.gkzxhn.helpout.entity.AlipaySign
import com.gkzxhn.helpout.entity.LawyersInfo
import retrofit2.Response
import rx.Observable


/**
 * Explanation:
 * @author LSX
 *    -----2018/9/6
 */

interface IBountyModel : IBaseModel {


    fun getAlipaySign(context: Context): Observable<AlipaySign>
    fun bingAlipay(context: Context, authCode: String): Observable<Response<Void>>
    fun getLawyersInfo(context: Context): Observable<LawyersInfo>
    fun unbingAlipay(context: Context): Observable<Response<Void>>
}