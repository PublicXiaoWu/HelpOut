package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.entity.UpdateInfo
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.RetrofitClientPublic
import com.gkzxhn.helpout.utils.*
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.auth.AuthService
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.default_top.*
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @classname：SettingActivtiy
 * @author：liushaoxiang
 * @date：2018/10/11 3:52 PM
 * @description：设置
 */
class SettingActivity : BaseActivity() {
    override fun provideContentViewId(): Int {
        return R.layout.activity_setting
    }

    @SuppressLint("SetTextI18n")
    override fun init() {
        initTopTitle()
        ProjectUtils.addViewTouchChange(tv_setting_exit)
        var size = FileUtils.getFileSizes(externalCacheDir)
        val appSize = 73 * 1024 * 1024.toLong()
        size += appSize

        tv_setting_clear_size.text = size.toGMKSizeStr()
        tv_setting_version.text = "V_" + ObtainVersion.getVersionName(App.mContext)
    }

    private fun initTopTitle() {
        tv_default_top_title.text = getString(R.string.set)
        iv_default_top_back.setOnClickListener {
            finish()
        }

    }

    fun onClickSetting(view: View) {
        when (view.id) {

            /****** 存储空间 ******/
            R.id.v_setting_clear_bg -> {
                startActivity(Intent(this, CleanStorageActivity::class.java))
            }
            /****** 存储空间 ******/
            R.id.v_setting_idea_bg -> {
                startActivity(Intent(this, IdeaSubmitActivity::class.java))
            }
            /****** 版本更新 ******/
            R.id.v_setting_update_bg -> {
                updateApp()
            }
            /****** 退出账号 ******/
            R.id.tv_setting_exit -> {
                exit()
            }
        }
    }

    /**
     * @methodName： created by liushaoxiang on 2018/10/12 4:33 PM.
     * @description：清理缓存的dialog处理
     */
    private fun clearDialog() {
        val selectDialog = selectDialog("确认清除吗？", false)
        selectDialog.findViewById<TextView>(R.id.dialog_save).setOnClickListener {
            SystemUtil.clearAllCache(this)
            tv_setting_clear_size.text = SystemUtil.getTotalCacheSize(this)
            showToast("清除完成")
            selectDialog.dismiss()
        }
    }

    /**
     * @methodName： created by liushaoxiang on 2018/10/22 3:10 PM.
     * @description：退出账号
     */
    private fun exit() {
        val selectDialog = selectDialog("确认退出账号吗？", false)
        selectDialog.findViewById<TextView>(R.id.dialog_save).setOnClickListener {
            NIMClient.getService(AuthService::class.java).logout()
            App.EDIT.putString(Constants.SP_TOKEN, "")?.commit()
            App.EDIT.putString(Constants.SP_NAME, "")?.commit()
            App.EDIT.putString(Constants.SP_LAWOFFICE, "")?.commit()
            App.EDIT.putString(Constants.SP_CERTIFICATIONSTATUS, "")?.commit()

            /****** 清除缓存 ******/
            SystemUtil.clearAllCache(this)

            NIMClient.getService(AuthService::class.java).logout()

            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            selectDialog.dismiss()
        }
    }

    private fun updateApp() {
        RetrofitClientPublic.getInstance(this).mApi.updateApp()
                .subscribeOn(Schedulers.io())
                ?.unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<UpdateInfo>(this) {
                    override fun success(t: UpdateInfo) {
                        val versionCode = ObtainVersion.getVersionCode(App.mContext)
                        if (t.number!! > versionCode) {
                            showDownloadDialog(t)
                        } else {
                            TsDialog("已是最新版本", true)
                        }
                    }

                    override fun onError(e: Throwable?) {
                        if (e is HttpException && e.code() == 404) {
                            loadDialog?.dismiss()
                            /****** 不处理 ******/
                        } else {
                            super.onError(e)
                        }
                    }

                })
    }
}

