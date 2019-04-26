package com.gkzxhn.helpout.presenter

import android.content.Context
import android.util.Log
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.CircleoffriendsPicture
import com.gkzxhn.helpout.entity.PublishRequest
import com.gkzxhn.helpout.entity.UploadFile
import com.gkzxhn.helpout.entity.rxbus.RxBusBean
import com.gkzxhn.helpout.model.iml.CustomerModel
import com.gkzxhn.helpout.net.HttpObserverNoDialog
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.BaseView
import com.luck.picture.lib.entity.LocalMedia
import okhttp3.ResponseBody
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import java.io.File
import java.util.*

class PublishLifeCirclePresenter(context: Context, view: BaseView)
    : BasePresenter<CustomerModel, BaseView>(context, CustomerModel(context), view) {

    private var fileNameList: MutableList<String> = ArrayList()
    var fileCount = 0
    private var content = ""

    /**
     * 上传图片
     */
    fun uploadImg(file: File): Subscription? {
        return mContext?.let {
            mModel.uploadFiles(it, file)
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserverNoDialog<UploadFile>(it) {
                        override fun success(t: UploadFile) {
                            Log.e(this.javaClass.simpleName, t.filename)
                            t.filename?.let { it1 -> fileNameList.add(it1) }
                            if (fileNameList.size == fileCount) {
                                publishLifeCircle(content, fileNameList)
                            }
                        }

                        override fun onError(t: Throwable?) {
                            if (fileNameList.isNotEmpty()) {
                                fileNameList.forEach {
                                    deleteImg(it)
                                }
                            }
                            super.onError(t)
                        }
                    })
        }

    }

    /**
     * 删除图片
     */
    fun deleteImg(filename: String) {
        mModel.deleteFile(filename)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    Log.d(filename.toString(), "删除成功")
                }, {
                    Log.e(this.javaClass.simpleName, it.message?.toString())
                })
    }

    fun uploadAllImg(selectList: MutableList<LocalMedia>, content: String) {
        fileNameList.clear()
        fileCount = selectList.size
        this.content = content
        if (selectList.isNotEmpty()) {
            selectList.forEach {
                val file = File(if (it.isCompressed) it.compressPath else it.path)
                uploadImg(file)
            }
        }else {
            publishLifeCircle(content)
        }
    }

    /**
     * 发布生活圈
     */
    fun publishLifeCircle(content: String, filenameList: List<String>? = null) {
        val request = PublishRequest(content)
        request.content = content
        if (filenameList != null && filenameList.isNotEmpty()) {
            request.circleoffriendsPicture = filenameList.map {
                CircleoffriendsPicture(it)
            }
        }
        mContext?.let {
            mModel.publishLifeCircle(request)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserverNoDialog<ResponseBody>(it) {
                    override fun success(t: ResponseBody) {
                        //发布成功
                        mContext?.let { it.showToast(it.getString(R.string.publish_success)) }
                        RxBus.instance.post(RxBusBean.PublishEntity(0))
                    }

                    override fun onError(t: Throwable?) {
                        //发布失败
                        if (fileNameList.isNotEmpty()) {
                            fileNameList.forEach {
                                deleteImg(it)
                            }
                        }
                        super.onError(t)
                        mContext?.let { it.showToast(it.getString(R.string.publish_failed)) }
                        RxBus.instance.post(RxBusBean.PublishEntity(1))
                    }
                })
        }
    }
}