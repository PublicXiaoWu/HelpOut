package com.gkzxhn.helpout.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.util.Pair
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.ImageActivity
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.net.NetWorkCodeInfo
import com.gkzxhn.helpout.utils.ProjectUtils
import com.luck.picture.lib.entity.LocalMedia
import java.io.File

/**
 * @classname：NineGridTestLayout
 * @author：liushaoxiang
 * @date：2019/4/18 2:29 PM
 * @description：
 */
class NineGridTestLayout : NineGridLayout {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun displayOneImage(imageView: RatioImageView, url: Any, parentWidth: Int): Boolean {
        var image: ViewTarget<ImageView, Drawable>? = null
        if (url is String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.transitionName = url
                pairs = pairs.plus(Pair(imageView as View, url))
            }
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
                    image = Glide.with(context).load(glideUrl).apply(options).into(imageView)
                }
            }
        } else if (url is LocalMedia) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.transitionName = url.path
                pairs = pairs.plus(Pair(imageView as View, url.path))
            }
            val path = if (url.isCompressed) url.compressPath else url.path
            image = Glide.with(context).load(File(path))
                    .apply(RequestOptions()
                            .placeholder(R.color.main_gary_bg)
                            .error(R.color.main_gary_bg))
                    .into(imageView)
        }
        if (image != null) {
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

    override fun displayImage(imageView: RatioImageView, url: Any) {
        if (url is String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.transitionName = url
                pairs = pairs.plus(Pair(imageView as View, url))
            }
            ProjectUtils.loadImageByFileID(context, url, imageView)
        } else if (url is LocalMedia) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.transitionName = url.path
                pairs = pairs.plus(Pair(imageView as View, url.path))
            }
            val path = if (url.isCompressed) url.compressPath else url.path
            Glide.with(context).load(File(path))
                    .apply(RequestOptions()
                            .placeholder(R.color.main_gary_bg)
                            .error(R.color.main_gary_bg))
                    .into(imageView)
        }
    }

    var pairs = emptyArray<Pair<View, String>>()

    override fun onClickImage(i: Int, url: Any, urlList: List<Any>) {
//        ImageActivity.launch(context, ArrayList(urlList), i, getChildAt(i) as ImageView)
        val list = urlList.map {
            if (it is String) {
                it
            }else if (it is LocalMedia) {
                if (it.isCompressed) "local&&${it.path}"
                else "local&&${it.path}"
            }else ""
        }
        ImageActivity.launch(context, ArrayList(list), i, pairs = *pairs)
//        Toast.makeText(mContext, "点击了图片$url", Toast.LENGTH_SHORT).show()
    }

    companion object {

        protected val MAX_W_H_RATIO = 3
    }
}
