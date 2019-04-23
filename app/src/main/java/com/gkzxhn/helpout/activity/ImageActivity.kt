package com.gkzxhn.helpout.activity

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.IntentConstants
import com.gkzxhn.helpout.utils.ProjectUtils
import kotlinx.android.synthetic.main.activity_image.*


/**
 * Created by 方 on 2018/4/20.
 */
class ImageActivity : BaseActivity() {

    companion object {
        fun launch(context: Activity, uri: Uri, requestCode: Int? = null, imageView: ImageView? = null) {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra(IntentConstants.INTENT_CROP_IMAGE_URI, uri)
            intent.putExtra(IntentConstants.INTENT_TYPE, requestCode)
            if (Build.VERSION.SDK_INT > 21 && null != imageView) {
                if (requestCode != null) {
                    context.startActivityForResult(intent, requestCode,
                            ActivityOptions.makeSceneTransitionAnimation(context as Activity,
                                    imageView, "img").toBundle())
                } else {
                    context.startActivity(intent,
                            ActivityOptions.makeSceneTransitionAnimation(context as Activity,
                                    imageView, "img").toBundle())
                }
            } else {
                if (requestCode != null) {
                    context.startActivityForResult(intent, requestCode)
                } else {
                    context.startActivity(intent)
                }
            }
        }

        fun launch(context: Activity, url: String, requestCode: Int? = null, imageView: ImageView? = null) {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra(IntentConstants.INTENT_String_URL, url)
            if (Build.VERSION.SDK_INT > 21 && null != imageView) {
                if (requestCode != null) {
                    context.startActivityForResult(intent, requestCode,
                            ActivityOptions.makeSceneTransitionAnimation(context as Activity,
                                    imageView, "img").toBundle())
                } else {
                    context.startActivity(intent,
                            ActivityOptions.makeSceneTransitionAnimation(context as Activity,
                                    imageView, "img").toBundle())
                }
            } else {
                if (requestCode != null) {
                    context.startActivityForResult(intent, requestCode)
                } else {
                    context.startActivity(intent)
                }
            }
        }

        fun launch(context: Context, urls: ArrayList<*>, index: Int = 0, imageView: ImageView? = null) {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putStringArrayListExtra(IntentConstants.INTENT_String_URLS, urls as ArrayList<String>)
            intent.putExtra(IntentConstants.INDEX, index)
            if (Build.VERSION.SDK_INT > 21 && null != imageView) {
                val transitionName = when (index) {
                    0 -> {
                        "img1"
                    }
                    1 -> {
                        "img2"
                    }
                    2 -> {
                        "img3"
                    }
                    3 -> {
                        "img4"
                    }
                    else -> {
                        "img1"
                    }
                }
                context.startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(context as Activity,
                                imageView, transitionName).toBundle())
            } else {
                context.startActivity(intent)
            }
        }
    }

    var uri: Uri? = null
    var url: String? = null
    var urls: ArrayList<String>? = null
    var index = 0
    var requestCode: Int? = null

    override fun provideContentViewId(): Int {
        return R.layout.activity_image
    }

    override fun init() {
        uri = intent.getParcelableExtra<Uri>(IntentConstants.INTENT_CROP_IMAGE_URI)
        url = intent.getStringExtra(IntentConstants.INTENT_String_URL)
        urls = intent.getStringArrayListExtra(IntentConstants.INTENT_String_URLS)
        index = intent.getIntExtra(IntentConstants.INDEX, 0)
        requestCode = intent.getIntExtra(IntentConstants.INTENT_TYPE, -1)

        if (url != null) {
            ProjectUtils.loadImageByFileID(this, url, image_view)
//            image_view.load(this, url!!, R.mipmap.img_error)
        } else if (uri != null) {
            image_view.setImageURI(uri)
//            setImage(uri!!)
        } else if (urls != null && urls?.isNotEmpty() == true) {
            image_view.visibility = View.GONE
            setViewPager(urls!!, index)
        }
        if (requestCode != -1) {
            ll_confirm.visibility = View.VISIBLE
        } else {
            ll_confirm.visibility = View.GONE
        }
        setOnclick()
    }

    private fun setViewPager(urls: ArrayList<String>, index: Int) {
        viewpager.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewpager.transitionName = when (index) {
                0 -> {
                    "img1"
                }
                1 -> {
                    "img2"
                }
                2 -> {
                    "img3"
                }
                3 -> {
                    "img4"
                }
                else -> {
                    "img1"
                }
            }
        }
        viewpager.adapter = object : PagerAdapter() {
            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view == `object`
            }

            override fun getCount(): Int {
                return urls.size ?: 1
            }

            override fun instantiateItem(container: ViewGroup, position1: Int): Any {
                val imageView = ImageView(this@ImageActivity)
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                imageView.layoutParams = params
                urls[position1].let {
                    ProjectUtils.loadImageByFileID(this@ImageActivity, it, imageView)
//                    imageView.load(this@ImageActivity, "$it?token=${Constants.IMAGE_TOKEN}", R.mipmap.img_error)
                }
                imageView.setOnClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAfterTransition()
                    } else {
                        finish()
                    }
                }
                container.addView(imageView)
                return imageView
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }
        }
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewpager.transitionName = when (position) {
                        0 -> {
                            "img1"
                        }
                        1 -> {
                            "img2"
                        }
                        2 -> {
                            "img3"
                        }
                        3 -> {
                            "img4"
                        }
                        else -> {
                            "img1"
                        }
                    }
                }

            }
        })
        viewpager.currentItem = index
    }

    private fun setOnclick() {
        tv_confirm.setOnClickListener {
            val data = Intent()
            data.putExtra(IntentConstants.INTENT_CROP_IMAGE_URI, uri)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
        tv_cancel.setOnClickListener {
            finish()
        }
        if (requestCode == -1) {
            image_view.setOnClickListener { onBackPressed() }
        }
    }

    private fun setImage(mImageCaptureUri: Uri) {

        // 不管是拍照还是选择图片每张图片都有在数据中存储也存储有对应旋转角度orientation值
        // 所以我们在取出图片是把角度值取出以便能正确的显示图片,没有旋转时的效果观看

        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cr = this.contentResolver
        val cursor = cr.query(mImageCaptureUri, null, null, null, null)// 根据Uri从数据库中找
        if (cursor != null) {
            cursor.moveToFirst()// 把游标移动到首位，因为这里的Uri是包含ID的所以是唯一的不需要循环找指向第一个就是了
            val filePath = cursor.getString(cursor.getColumnIndex("_data"))// 获取图片路
            val orientation = cursor.getString(cursor
                    .getColumnIndex("orientation"))// 获取旋转的角度
            cursor.close()
            if (filePath != null) {
                var bitmap = BitmapFactory.decodeFile(filePath)//根据Path读取资源图片
                var angle = 0
                if (orientation != null && "" != orientation) {
                    angle = Integer.parseInt(orientation)
                }
                if (angle != 0) {
                    // 下面的方法主要作用是把图片转一个角度，也可以放大缩小等
                    val m = Matrix()
                    val width = bitmap.width
                    val height = bitmap.height
                    m.setRotate(angle.toFloat()) // 旋转angle度
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                            m, true)// 从新生成图片

                }
                image_view.setImageBitmap(bitmap)
            }
        }
    }
}