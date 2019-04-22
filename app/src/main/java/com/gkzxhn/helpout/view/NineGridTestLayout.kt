package com.gkzxhn.helpout.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.gkzxhn.helpout.utils.ImageUtils
import com.gkzxhn.helpout.utils.ProjectUtils
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener

/**
 * @classname：NineGridTestLayout
 * @author：liushaoxiang
 * @date：2019/4/18 2:29 PM
 * @description：
 */
class NineGridTestLayout : NineGridLayout {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun displayOneImage(imageView: RatioImageView, url: String, parentWidth: Int): Boolean {

        ImageUtils.displayImage(mContext, imageView, url, ImageUtils.photoImageOption, object : ImageLoadingListener {
            override fun onLoadingStarted(imageUri: String, view: View) {

            }

            override fun onLoadingFailed(imageUri: String, view: View, failReason: FailReason) {

            }

            override fun onLoadingComplete(imageUri: String, view: View, bitmap: Bitmap) {
                val w = bitmap.width
                val h = bitmap.height

                val newW: Int
                val newH: Int
                if (h > w * MAX_W_H_RATIO) {
                    //h:w = 5:3
                    newW = parentWidth / 2
                    newH = newW * 5 / 3
                } else if (h < w) {
                    //h:w = 2:3
                    newW = parentWidth * 2 / 3
                    newH = newW * 2 / 3
                } else {
                    //newH:h = newW :w
                    newW = parentWidth / 2
                    newH = h * newW / w
                }
                setOneImageLayoutParams(imageView, newW, newH)
            }

            override fun onLoadingCancelled(imageUri: String, view: View) {

            }
        })
        return false
    }

    override fun displayImage(imageView: RatioImageView, url: String) {
//        Glide.with(mContext).load(url).into(imageView)
        ProjectUtils.loadImageByFileID(context,url,imageView)
//                ImageLoader.getInstance().displayImage(url, imageView, ImageUtils.INSTANCE.getPhotoImageOption());
    }

    override fun onClickImage(i: Int, url: String, urlList: List<String>) {
        Toast.makeText(mContext, "点击了图片$url", Toast.LENGTH_SHORT).show()
    }

    companion object {

        protected val MAX_W_H_RATIO = 3
    }
}
