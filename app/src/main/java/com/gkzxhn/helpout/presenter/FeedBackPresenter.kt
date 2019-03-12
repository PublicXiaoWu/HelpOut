package com.gkzxhn.helpout.presenter

import android.content.Context
import android.util.Log
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.entity.FeedBackRequestInfo
import com.gkzxhn.helpout.entity.UploadFile
import com.gkzxhn.helpout.model.iml.CustomerModel
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.view.FeedBackView
import okhttp3.ResponseBody
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import java.io.File
import java.lang.Exception

class FeedBackPresenter(context: Context, view: FeedBackView)
    : BasePresenter<CustomerModel, FeedBackView>(context, CustomerModel(context), view) {

    /**
     * 上传图片
     */
    fun uploadImg(position: Int, file: File): Subscription? {
        return mContext?.let {
            mModel.uploadFiles(it, file)
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<UploadFile>(it) {
                        override fun success(t: UploadFile) {
                            t.id?.let { mView?.showUploadSuccess(position, it) }
                        }

                        override fun onError(t: Throwable?) {
                            super.onError(t)
                            mView?.showUploadError(position, t
                                    ?: Exception(it.getString(R.string.upload_failed)))
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

    /**
     * 提交意见反馈
     */
    fun postFeedBack(feedBackRequestInfo: FeedBackRequestInfo) {
        mContext?.let {
            mModel.postFeedBack(feedBackRequestInfo)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<ResponseBody>(it) {
                    override fun success(t: ResponseBody) {
                        mView?.uploadSuccess()
                    }
                })
        }
    }
}