package com.gkzxhn.helpout.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.customview.CenterCropRoundCornerTransform
import com.gkzxhn.helpout.extensions.dp2px
import com.gkzxhn.helpout.net.NetWorkCodeInfo
import java.io.File

/**
 * @classname：ProjectUtils
 * @author：liushaoxiang
 * @date：2018/10/12 11:12 AM
 * @description：本项目常用的方法
 */

object ProjectUtils {

    /**
     * @methodName： created by liushaoxiang on 2018/10/12 11:13 AM.
     * @description：给view设置触摸透明度变化
     */
    fun addViewTouchChange(v: View) {
        v.setOnTouchListener { _, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.alpha = 0.8f
                }
                MotionEvent.ACTION_UP -> {
                    v.alpha = 1.0f
                }
            }
            /****** 返回false 不拦截点击事件的处理 ******/
            false
        }
    }

    /**
     * @methodName ： created by liushaoxiang on 2018/11/1 9:35 AM.
     * @description ： uri 转文件
     */
    fun uri2File(cacheDir: File, uri: Uri): File {
        val uriFile = File(uri.path)
        return File(cacheDir, uriFile.name)
    }

    /**
     * @methodName： created by liushaoxiang on 2018/11/9 3:02 PM.
     * @description：认证是否通过
     */
    fun certificationStatus(): Boolean {
        val certificationStatus = App.SP.getString(Constants.SP_CERTIFICATIONSTATUS, "")
        return certificationStatus == Constants.CERTIFIED
    }

    /**
     * @methodName： created by liushaoxiang on 2018/11/20 8:56 PM.
     * @description：专业领域的转换
     */
    fun categoriesStrToType(str: String): String {
        if (str.isEmpty()) {
            return ""
        }
        return when (str) {
            "财产纠纷" -> "PROPERTY_DISPUTES"
            "婚姻家庭" -> "MARRIAGE_FAMILY"
            "交通事故" -> "TRAFFIC_ACCIDENT"
            "工伤赔偿" -> "WORK_COMPENSATION"
            "合同纠纷" -> "CONTRACT_DISPUTE"
            "刑事辩护" -> "CRIMINAL_DEFENSE"
            "房产纠纷" -> "HOUSING_DISPUTES"
            "劳动就业" -> "LABOR_EMPLOYMENT"
            else -> ""
        }
    }

    /**
     * @methodName： created by liushaoxiang on 2018/11/9 9:57 AM.
     * @description：专业类别的转化
     */
    fun categoriesConversion(str: String): String {
        if (str.isEmpty()) {
            return ""
        }
        return when (str) {
            "PROPERTY_DISPUTES" -> "财产纠纷"
            "MARRIAGE_FAMILY" -> "婚姻家庭"
            "TRAFFIC_ACCIDENT" -> "交通事故"
            "WORK_COMPENSATION" -> "工伤赔偿"
            "CONTRACT_DISPUTE" -> "合同纠纷"
            "CRIMINAL_DEFENSE" -> "刑事辩护"
            "HOUSING_DISPUTES" -> "房产纠纷"
            "LABOR_EMPLOYMENT" -> "劳动就业"
            else -> {
                "其它"
            }
        }
    }

    /****** 通过fileID加载图片 ******/
    fun loadImageByFileID(context: Context, fileId: String?, imageview: ImageView) {
        if (fileId == null || fileId.isEmpty()) {
            imageview.setImageResource(R.mipmap.ic_user_icon)
            return
        }
        val token = App.SP.getString(Constants.SP_TOKEN, "")
        if (token != null) {
            if (token.isNotEmpty()) {
                val mtoken = "Bearer $token"
                val addHeader = LazyHeaders.Builder().addHeader("Authorization", mtoken)
                val glideUrl = GlideUrl(NetWorkCodeInfo.BASE_URL + "/files/" + fileId, addHeader.build())
                Glide.with(context).load(glideUrl).into(imageview)
            }
        }
    }

    /****** 通过username加载圆形图片 ******/
    fun loadRoundImageByUserName(context: Context, userName: String?, imageview: ImageView) {
        if (userName == null || userName.isEmpty()) {
            imageview.setImageResource(R.mipmap.ic_user_icon)
            return
        }
        Log.v("OkHttp", "-----------图片userName:$userName")
        val token = App.SP.getString(Constants.SP_TOKEN, "")
        if (token != null) {
            if (token.isNotEmpty()) {
                val mtoken = "Bearer $token"
                val addHeader = LazyHeaders.Builder().addHeader("Authorization", mtoken)
                val glideUrl = GlideUrl(NetWorkCodeInfo.BASE_URL + "/users/" + userName + "/avatar", addHeader.build())
                val options = RequestOptions()
                options.placeholder(R.mipmap.ic_user_icon)
                options.error(R.mipmap.ic_user_icon)
                options.apply(RequestOptions.circleCropTransform())
//                options.transform(RoundedCorners(120))
                /****** 加上一个时间让其每5分钟更新 ******/
                options.signature(ObjectKey(userName + System.currentTimeMillis() / 1000 / 60 / 5))
                Glide.with(context).load(glideUrl)
                        .apply(options)
                        .into(imageview)
            }
        }
    }

    /****** 加载自已的圆形头像 ******/
    fun loadMyIcon(context: Context, imageview: ImageView) {
        val token = App.SP.getString(Constants.SP_TOKEN, "")
        if (token != null) {
            if (token.isNotEmpty()) {
                val mtoken = "Bearer $token"
                val addHeader = LazyHeaders.Builder().addHeader("Authorization", mtoken)
                val glideUrl = GlideUrl(NetWorkCodeInfo.BASE_URL + "/users/me/avatar", addHeader.build())
                val options = RequestOptions()
                options.placeholder(R.mipmap.ic_user_icon)
                options.error(R.mipmap.ic_user_icon)
//                options.transform(RoundedCorners(120))
                options.apply(RequestOptions.circleCropTransform())
                /****** 加上一个时间让其每次都更新 ******/
                options.signature(ObjectKey(App.SP.getString(Constants.SP_MY_ICON, "defValue")))
                Glide.with(context).load(glideUrl)
                        .apply(options)
                        .into(imageview)
            }
        }
    }

    /****** 加载自已的头像 ******/
    fun loadMyIconNoRound(context: Context, imageview: ImageView) {
        val token = App.SP.getString(Constants.SP_TOKEN, "")
        if (token != null) {
            if (token.isNotEmpty()) {
                val mtoken = "Bearer $token"
                val addHeader = LazyHeaders.Builder().addHeader("Authorization", mtoken)
                val glideUrl = GlideUrl(NetWorkCodeInfo.BASE_URL + "/users/me/avatar", addHeader.build())
                val options = RequestOptions()
                options.placeholder(R.mipmap.ic_user_icon)
                options.error(R.mipmap.ic_user_icon)
                /****** 加上一个时间让其每次都更新 ******/
                options.signature(ObjectKey(App.SP.getString(Constants.SP_MY_ICON, "defValue")))
                Glide.with(context).load(glideUrl)
                        .apply(options)
                        .into(imageview)
            }
        }
    }

    /**
     * 意见反馈加载圆角图片
     */
    fun loadRoundCorner(context: Context, url: Any, imageView: ImageView) {
        val token = App.SP.getString(Constants.SP_TOKEN, "")
        if (token != null) {
            if (token.isNotEmpty()) {
                val mtoken = "Bearer $token"
                val addHeader = LazyHeaders.Builder().addHeader("Authorization", mtoken)
                val glideUrl =
                        if (url is String) {
                            GlideUrl(NetWorkCodeInfo.BASE_URL + "/files/" + url, addHeader.build())
                        } else {
                            url
                        }
                val options = RequestOptions()
                options.transform(CenterCropRoundCornerTransform(6f.dp2px()))
                Glide.with(context)
                        .load(glideUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.bitmapTransform(CenterCropRoundCornerTransform(6f.dp2px())))
                        .into(imageView)
            }
        }
    }
}