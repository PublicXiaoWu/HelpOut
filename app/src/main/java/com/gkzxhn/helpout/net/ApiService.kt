package com.gkzxhn.helpout.net

import com.gkzxhn.helpout.entity.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import rx.Observable


/**
 * Explanation: 接口
 * @author LSX
 *    -----2018/1/29
 */

interface ApiService {

    // 获取验证码
    @POST("/sms/verification-codes")
    fun getCode(@Body map: RequestBody): Observable<Response<Void>>

    //    注册 登录
    @POST("users/of-mobile")
    @Headers("Content-Type:application/json;charset=utf-8")
    fun login(@Body map: RequestBody): Observable<Response<Void>>

    //    获取网易云信的账号
    @GET("im/users/me")
    fun getImInfo(): Observable<ImInfo>

    //    获取我的账号明细
    @GET("users/me")
    fun getAccountInfo(): Observable<AccountInfo>

    //    获取网易云信的账号
    @GET("im/users/by-username")
    fun getImAccount(@Query("username") username: String): Observable<ImInfo>

    //    获取指定法律咨询所剩视频时长
    @GET("/lawyer/my/legal-advice/{id}/video-duration")
    fun getVideoDuration(@Path("id") id: String): Observable<VideoDuration>

    /**
     * 修改我的手机号码
     */
    @PUT("users/me/phone-number")
    fun updatePhoneNumber(@Body requestBody: RequestBody): Observable<Response<Void>>

    /**
     * 支付宝提现
     */
    @POST("/lawyer/withdrawal/alipay")
    fun withdrawAli(@Body requestBody: RequestBody): Observable<Response<Void>>

    /**
     *获取律师详情
     */
    @GET("lawyer/profiles")
    fun getLawyersInfo(): Observable<LawyersInfo>

    /**
     *获取律师身份状态
     */
    @GET("/user/lawyer-identity ")
    fun getLawyersState(): Observable<LawyersInfo>

    /**
     *获取android最新版本
     */
    @GET("/versions/latest?clientKey=assistant.app&deviceType=ANDROID")
    fun updateApp(): Observable<UpdateInfo>

    /****** 设置接单状态 ******/
    @POST("lawyer/profiles/service-status")
    @Headers("Content-Type:application/json;charset=utf-8")
    fun setOrderState(@Body map: RequestBody): Observable<Response<Void>>

    /****** 添加或更新律师认证 ******/
    @POST("lawyer/certification")
    @Headers("Content-Type:application/json;charset=utf-8")
    fun certification(@Body map: RequestBody): Observable<Response<Void>>

    /****** 添加或更新律师认证 ******/
    @GET("lawyer/certification")
    @Headers("Content-Type:application/json;charset=utf-8")
    fun getCertification(): Observable<QualificationAuthentication>

    /**
     * 获取token
     */
    @POST("oauth/token")
    @FormUrlEncoded
    fun getToken(@Field("grant_type") grant_type: String,
                 @Field("username") username: String? = null,
                 @Field("password") password: String? = null,
                 @Field("refresh_token") refreshToken: String? = null): Observable<Response<ResponseBody>>

    /*****  崩溃日志上传  */
    @POST("crash-logs")
    fun uploadCrash(@Body map: RequestBody): Observable<Response<Void>>

    /*****  修改头像  */
    @PUT("users/me/avatar")
    @Multipart
    fun modifyAvatar(@Part file: MultipartBody.Part): Observable<Response<Void>>

    /*****  修改我的账号明细  */
    @PUT("users/me")
    fun modifyAccountInfo(@Body map: RequestBody): Observable<Response<Void>>

    /*****  上传文件  */
    @POST("/files")
    @Multipart
    fun uploadFiles(@Part file: MultipartBody.Part, @Query("type") type: String): Observable<UploadFile>

    // 下载文件
    @GET("/files/{id}")
    fun downloadFile(@Header("Range") range: String, @Path("id") id: String): Observable<ResponseBody>

    // 下载文件
    @GET("/files/{id}")
    fun downloadImage(@Path("id") id: String): Observable<ResponseBody>

    //    获取抢单列表
    @GET("lawyer/rush/legal-advice")
    fun getOrderReceiving(@Query("page") page: String, @Query("size") size: String, @Query("sort") sort: String): Observable<OrderReceiving>


    //    查询我的处理中的法律咨询订单
    @GET("/lawyer/my/legal-advice/processing")
    fun getProcessingOrder(): Observable<OrderInfo>

    //    获取账单列表
    @GET("/bill")
    fun getTransaction(@Query("page") page: String, @Query("size") size: String): Observable<MoneyList>

    //    获取我的IM通知
    @GET("im/notifications/my")
    fun getNotifications(@Query("page") page: String, @Query("size") size: String, @Query("sort") sort: String): Observable<NotificationInfoList>

    //    获取抢单的明细
    @GET("/lawyer/rush/legal-advice/{id}")
    fun getOrderRushInfo(@Path("id") id: String): Observable<OrderRushInfo>

    //    获取指定法律咨询评论明细
    @GET("/lawyer/my/legal-advice/{id}/comment")
    fun getOrderComment(@Path("id") id: String): Observable<OrderComment>

    //    接单
    @POST("lawyer/rush/legal-advice/{id}/accepted")
    fun acceptRushOrder(@Path("id") id: String): Observable<OrderMyInfo>

    //    获取我的咨询列表
    @GET("lawyer/my/legal-advice")
    fun getOrderDispose(@Query("page") page: String, @Query("size") size: String): Observable<OrderDispose>

    //    获取我的咨询列表（所有订单）
    @GET("lawyer/my/legal-advice")
    fun getAllOrderDispose(@Query("page") page: String, @Query("size") size: String): Observable<OrderDispose>

    //    获取我的咨询的明细
    @GET("/lawyer/my/legal-advice/{id}")
    fun getOrderMyInfo(@Path("id") id: String): Observable<OrderMyInfo>

    //  接单
    @POST("lawyer/my/legal-advice/{id}/accepted")
    fun acceptMyOrder(@Path("id") id: String, @Query("reward") reward: String): Observable<OrderMyInfo>

    //  拒绝单
    @POST("lawyer/my/legal-advice/{id}/refused")
    fun rejectMyOrder(@Path("id") id: String): Observable<OrderMyInfo>

    //  查询我的支付宝绑定
    @GET("/lawyer/alipay")
    fun getAlipayInfo(): Observable<AlipayInfo>

    //  获取支付宝授权参数信息的签名
    @GET("/lawyer/alipay/auth/sign")
    fun getAlipaySign(): Observable<AlipaySign>

    // 绑定支付宝
    @POST("/lawyer/alipay/bind")
    fun bingAlipay(@Query("authCode") authCode: String): Observable<Response<Void>>

    // 绑定支付宝
    @POST("/lawyer/alipay/unbind")
    fun unbingAlipay(): Observable<Response<Void>>

    /**
     * 发布抢单
     */
    @POST("/customer/legal-advice/rush")
    fun publishOrder(@Body map: RequestBody): Observable<PublishOrderInfo>

    /**
     * 获取支付宝订单
     */
    @GET("/legal-advice/{id}/alipay")
    fun getAliOrder(@Path("id") id: String): Observable<ResponseBody>

    /**
     * 客户获取指定法律咨询明细
     */
    @GET("/customer/legal-advice/{id}")
    fun getCustomerOrderDetail(@Path("id") id: String): Observable<CustomerOrderDetailInfo>

    /**
     * 模拟通话
     */
    @GET("/notifications/netease/{id}/processed")
    fun mockVideoChart(@Path("id") id: String): Observable<ResponseBody?>

    /**
     * 删除用户的法律咨询订单
     */
    @DELETE("/customer/legal-advice/{id}")
    fun deleteOrder(@Path("id") id: String): Observable<ResponseBody?>

    /**
     * 删除律师的法律咨询订单
     */
    @DELETE("lawyer/my/legal-advice/{id}")
    fun deleteLawyerOrder(@Path("id") id: String): Observable<ResponseBody?>

    /**
     * 取消法律咨询
     */
    @POST("/customer/legal-advice/{id}/cancelled")
    fun cancelOrder(@Path("id") id: String): Observable<ResponseBody?>

    /**
     * 评论订单
     */
    @POST("/customer/comments")
    fun applyOrderComments(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 获取法律咨询订单评论
     */
    @GET("/customer/legal-advice/{id}/comment")
    fun getOrderComments(@Path("id") id: String): Observable<CommentInfo>

    /**
     * 查询法律咨询列表
     */
    @GET("/customer/legal-advice")
    fun getMyLawsOrder(@Query("page") page: Int,
                       @Query("size") size: Int = 10,
                       @Query("sort") sort: String? = null): Observable<OrderDispose>

    /**
     * 意见反馈
     */
    @POST("/feedbacks")
    fun postFeedback(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 删除指定文件
     */
    @DELETE("/files/{filename}")
    fun deleteFile(@Path("filename") filename: String): Observable<ResponseBody>

    /**
     * 获取微信订单
     */
    @GET("/legal-advice/{id}/we-chat-pay")
    fun getWxOrder(@Path("id") id: String): Observable<WXLawOrderInfo>

    /**
     * 获取好友信息
     */
    @GET("/customer/customerfriend/get-customer-friend")
    fun getFriendInfo(@Query("phoneNumber") phoneNumber: String? = null, @Query("username") username: String? = null, @Query("account") account: String? = null): Observable<FriendInfo>

    /**
     * 获取生活圈明细
     */
    @GET("/customer/circleoffriends/{livesCircleId}")
    fun getLivesCircleDetails(@Path("livesCircleId") livesCircleId: String): Observable<LivesCircleDetails>

    /**
     * 发布生活圈
     */
    @POST("/customer/circleoffriends/release")
    fun publishLifeCircle(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 添加好友
     */
    @POST("/customer/customerfriend/add")
    fun addFriend(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 点赞
     */
    @POST("/customer/circleoffriends/praise")
    fun praise(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 评论
     */
    @POST("/customer/circleoffriends/comment")
    fun comment(@Body requestBody: RequestBody): Observable<ResponseBody>

    /**
     * 获取最新未看生活圈
     */
    @GET("/customer/circleoffriends/get-circlefriend-newest")
    fun getLivesCircleNew(): Observable<LivesCircleNew>

    //    获取所有人生活圈
    @GET("/customer/circleoffriends/getCircleoffriends")
    fun getLivesCircle(@Query("page") page: String, @Query("size") size: String): Observable<LivesCircle>

    //    获取我的生活圈
    @GET("/customer/circleoffriends/getMyCircleoffriends")
    fun getMyLivesCircle(@Query("page") page: String, @Query("size") size: String, @Query("sort") sort: String): Observable<LivesCircle>

    //    获取别人的生活圈
    @GET("/customer/circleoffriends/get-friend-circlefriend")
    fun getLivesCircleByUserName(@Query("username") username: String, @Query("page") page: String, @Query("size") size: String, @Query("sort") sort: String): Observable<LivesCircle>


}