package com.gkzxhn.helpout.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.ImageActivity
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.net.NetWorkCodeInfo
import com.gkzxhn.helpout.utils.ProjectUtils

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
                ProjectUtils.loadImageByFileID(context, url, imageView)
        if (url.isEmpty()) {
            imageView.setImageResource(R.color.main_gary_bg)
            return false
        }
        val token = App.SP.getString(Constants.SP_TOKEN, "")
        if (token != null) {
            if (token.isNotEmpty()) {
                val mtoken = "Bearer $token"
                val addHeader = LazyHeaders.Builder().addHeader("Authorization", mtoken)
                val glideUrl = GlideUrl(NetWorkCodeInfo.BASE_URL + "/files/" + url, addHeader.build())
                val options = RequestOptions()
                options.placeholder(R.color.main_gary_bg)
                options.error(R.color.main_gary_bg)
                val image = Glide.with(context).load(glideUrl).apply(options).into(imageView)
                val w = image.view.width
                val h = image.view.height

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
        }


//
//        }
//        ImageUtils.displayImage(mContext, imageView, url, ImageUtils.photoImageOption, object : ImageLoadingListener {
//            override fun onLoadingStarted(imageUri: String, view: View) {
//
//            }
//
//            override fun onLoadingFailed(imageUri: String, view: View, failReason: FailReason) {
//
//            }
//
//            override fun onLoadingComplete(imageUri: String, view: View, bitmap: Bitmap) {
//                val w = bitmap.width
//                val h = bitmap.height
//
//                val newW: Int
//                val newH: Int
//                if (h > w * MAX_W_H_RATIO) {
//                    //h:w = 5:3
//                    newW = parentWidth / 2
//                    newH = newW * 5 / 3
//                } else if (h < w) {
//                    //h:w = 2:3
//                    newW = parentWidth * 2 / 3
//                    newH = newW * 2 / 3
//                } else {
//                    //newH:h = newW :w
//                    newW = parentWidth / 2
//                    newH = h * newW / w
//                }
//                setOneImageLayoutParams(imageView, newW, newH)
//            }
//
//            override fun onLoadingCancelled(imageUri: String, view: View) {
//
//            }
//        })
        return false
    }

    override fun displayImage(imageView: RatioImageView, url: String) {
        ProjectUtils.loadImageByFileID(context, url, imageView)
    }

    override fun onClickImage(i: Int, url: String, urlList: List<String>) {
        ImageActivity.launch(context, ArrayList(urlList), i, getChildAt(i) as ImageView)
//        Toast.makeText(mContext, "点击了图片$url", Toast.LENGTH_SHORT).show()
    }

    companion object {

        protected val MAX_W_H_RATIO = 3
    }
}
