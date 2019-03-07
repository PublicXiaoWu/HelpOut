package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.utils.FileUtils
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.utils.toGMKSizeStr
import kotlinx.android.synthetic.main.activity_clean_storage.*
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：空间管理
 * @author：liushaoxiang
 * @date：2019/3/7 2:49 PM
 * @description：
 */
class CleanStorageActivity : BaseActivity() {

    var cacheKb = 0L
    val handle = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            avi.hide()
            val size = FileUtils.getFileSizes(externalCacheDir)
            cacheKb = size / 1024
            val percent = ((FileUtils.getFileSizes(externalCacheDir) / FileUtils.getTotalInternalMemorySize().toFloat()) * 100).toInt()

            tv_cache_storage.text = size.toGMKSizeStr()
            tv_storage_size_percent.text = getString(R.string.storage_size_percent, "${percent.toString()}%")
            bt_clean_storage.visibility = View.VISIBLE
            tv_cache_storage.visibility = View.VISIBLE
        }
    }

    override fun init() {
        initTopTitle()

        setOnclick()
        avi.show()
        tv_storage_size_percent.text = "正在计算已用空间"
        bt_clean_storage.visibility = View.GONE
        tv_cache_storage.visibility = View.GONE

        /**
        - 异步线程
         */
        Thread(Runnable {
            //返回主线程
            handle.sendEmptyMessageDelayed(1, 2000)
        }).start()
    }

    override fun provideContentViewId(): Int {
        return R.layout.activity_clean_storage
    }


    private fun setOnclick() {
        bt_clean_storage.setOnClickListener {
            if (cacheKb == 0L) {
                showToast(getString(R.string.had_no_cache))
            } else {
                val callback = MaterialDialog.SingleButtonCallback { dialog, which ->
                    if (FileUtils.delAllFile(externalCacheDir.absolutePath)) {
                        showToast(getString(R.string.clean_success))
                        cacheKb = 0
                        tv_cache_storage.text = "0KB"
                    }
                }
                showErrorMessage(title = null, content = getString(R.string.clean_storage_tips), callback = callback, canDismiss = true, positiveText = "确认", negativeText = "取消")
            }
        }
    }


    private fun initTopTitle() {
        tv_default_top_title.text = "存储空间"
        iv_default_top_back.setOnClickListener {
            finish()
        }
    }


    private fun showErrorMessage(title: String?, content: String, callback: MaterialDialog.SingleButtonCallback?, canDismiss: Boolean, positiveText: String, negativeText: String) {
        val builder = MaterialDialog.Builder(this)
                .content(content)
                .cancelable(canDismiss)
                .positiveText(if (callback != null) positiveText else getString(R.string.close))
                .onPositive(callback ?: MaterialDialog.SingleButtonCallback { dialog, which ->
                    dialog.dismiss()
                })
        if (title != null) {
            builder.title(title)
        }
        if (callback != null && canDismiss && !TextUtils.isEmpty(negativeText)) {
            builder.negativeText(negativeText)
        }
        builder.show()
    }
}
