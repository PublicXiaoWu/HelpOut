package com.gkzxhn.helpout.model

import android.content.Context
import com.gkzxhn.helpout.entity.QualificationAuthentication
import com.gkzxhn.helpout.entity.UploadFile
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import rx.Observable


/**
 * Explanation:
 * @author LSX
 *    -----2018/9/6
 */

interface IQualificationAuthenticationModel : IBaseModel {

    fun certification(context: Context, body: RequestBody): Observable<Response<Void>>

    fun getCertification(context: Context): Observable<QualificationAuthentication>

    fun uploadFiles(context: Context, body: MultipartBody.Part): Observable<UploadFile>
}