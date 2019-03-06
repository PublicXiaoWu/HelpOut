package com.gkzxhn.helpout.model.iml

import android.content.Context
import com.gkzxhn.helpout.entity.QualificationAuthentication
import com.gkzxhn.helpout.entity.UploadFile
import com.gkzxhn.helpout.model.IQualificationAuthenticationModel
import com.gkzxhn.helpout.net.RetrofitClient
import com.gkzxhn.helpout.net.RetrofitClientPublic
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import rx.Observable
import rx.schedulers.Schedulers

/**
 * @classname：QualificationAuthenticationModel
 * @author：liushaoxiang
 * @date：2018/10/31 4:28 PM
 * @description：
 */

class QualificationAuthenticationModel : BaseModel(), IQualificationAuthenticationModel {

    override fun getCertification(context: Context): Observable<QualificationAuthentication> {
        return RetrofitClient.Companion.getInstance(context).mApi
                .getCertification()
                .subscribeOn(Schedulers.io()) as Observable<QualificationAuthentication>
    }

    override fun certification(context: Context, body: RequestBody): Observable<Response<Void>> {
        return RetrofitClient.Companion.getInstance(context).mApi
                .certification(body)
                .subscribeOn(Schedulers.io()) as Observable<Response<Void>>
    }

    override fun uploadFiles(context: Context, body: MultipartBody.Part): Observable<UploadFile> {
        return RetrofitClientPublic.getInstance(context).mApi?.uploadFiles(body,"PROTECTED")
                ?.subscribeOn(Schedulers.io()) as Observable<UploadFile>
    }

}