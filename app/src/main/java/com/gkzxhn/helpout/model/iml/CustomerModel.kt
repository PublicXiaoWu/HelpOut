package com.gkzxhn.helpout.model.iml

import android.content.Context
import com.gkzxhn.helpout.entity.*
import com.gkzxhn.helpout.extensions.getRequestBody
import com.gkzxhn.helpout.net.RetrofitClient
import com.gkzxhn.helpout.net.RetrofitClientPublic
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import rx.Observable
import rx.schedulers.Schedulers
import java.io.File

class CustomerModel (val context: Context) : BaseModel() {

    /**
     * 发布抢单
     */
    fun publishOrder(publishRequestInfo: PublishRequestInfo): Observable<PublishOrderInfo> {
        return RetrofitClient.getInstance(context)
                .mApi
                .publishOrder(Gson().getRequestBody(publishRequestInfo))
                .subscribeOn(Schedulers.io())
    }

    /**
     * 获取支付宝订单
     */
    fun getAliOrder(id: String) :Observable<ResponseBody>{
        return RetrofitClient.getInstance(context)
                .mApi
                .getAliOrder(id)
                .subscribeOn(Schedulers.io())
    }

    /**
     * 获取订单详情
     */
    fun getCustomerOrderDetail(id: String) :Observable<CustomerOrderDetailInfo>{
        return RetrofitClient.getInstance(context)
                .mApi
                .getCustomerOrderDetail(id)
                .subscribeOn(Schedulers.io())
    }

    /**
     * 获取im账号
     */
    fun getImAccount(context: Context,username:String): Observable<ImInfo> {
        return RetrofitClientPublic.Companion.getInstance(context).mApi
                ?.getImAccount(username)
                ?.subscribeOn(Schedulers.io())
                as Observable<ImInfo>
    }

    /**
     * 模拟通话
     */
    fun mockVideoChart(id: String) :Observable<ResponseBody?>{
        return RetrofitClient.getInstance(context)
                .mApi
                .mockVideoChart(id)
                .subscribeOn(Schedulers.io())
    }

    /**
     * 提交评论
     */
    fun applyOrderComment(commentRequestInfo: CommentInfo): Observable<ResponseBody> {
        return RetrofitClient.getInstance(context)
                .mApi
                .applyOrderComments(Gson().getRequestBody(commentRequestInfo))
                .subscribeOn(Schedulers.io())
    }

    /**
     * 获取评论
     */
    fun getComment(orderId: String) : Observable<CommentInfo> {
        return RetrofitClient.getInstance(context)
                .mApi
                .getOrderComments(orderId)
                .subscribeOn(Schedulers.io())
    }

    /**
     * 取消订单
     */
    fun cancelOrder(orderId: String): Observable<ResponseBody?> {
        return RetrofitClient.getInstance(context)
                .mApi
                .cancelOrder(orderId)
                .subscribeOn(Schedulers.io())
    }

    /**
     * 删除订单
     */
    fun deleteOrder(orderId: String): Observable<ResponseBody?> {
        return RetrofitClient.getInstance(context)
                .mApi
                .deleteOrder(orderId)
                .subscribeOn(Schedulers.io())
    }

    /**
     * 获取我的法律咨询订单列表
     */
    fun getMyLawOrder(page: Int): Observable<OrderDispose> {
        return RetrofitClient.getInstance(context)
                .mApi
                .getMyLawsOrder(page)
                .subscribeOn(Schedulers.io())
    }

    /**
     * 提交意见反馈
     */
    fun postFeedBack(feedBackRequestInfo: FeedBackRequestInfo): Observable<ResponseBody>? {
        return RetrofitClientPublic.getInstance(context)
                .mApi
                ?.postFeedback(Gson().getRequestBody(feedBackRequestInfo))
                ?.subscribeOn(Schedulers.io())
    }

    /**
     * 上传文件
     */
    fun uploadFiles(context: Context, file: File): Observable<UploadFile>? {
        val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return RetrofitClientPublic.getInstance(context)
                .mApi?.uploadFiles(body,"PROTECTED")
                ?.subscribeOn(Schedulers.io())
    }

    /**
     * 删除指定文件
     */
    fun deleteFile(filename: String): Observable<ResponseBody>? {
        return RetrofitClientPublic.getInstance(context)
                .mApi?.deleteFile(filename)
                ?.subscribeOn(Schedulers.io())
    }

    /**
     * 获取微信订单
     */
    fun getWxOrder(id: String) : Observable<WXLawOrderInfo> {
        return RetrofitClient.getInstance(context)
                .mApi
                .getWxOrder(id)
                .subscribeOn(Schedulers.io())
    }
}