package com.gkzxhn.helpout.view

interface FeedBackView : BaseView {

    fun showUploadSuccess(position: Int, url: String)
    fun showUploadError(position: Int, e: Throwable)
    fun uploadSuccess()
}