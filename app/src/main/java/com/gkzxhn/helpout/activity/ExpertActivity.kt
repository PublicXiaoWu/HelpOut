package com.gkzxhn.helpout.activity

import android.content.Intent
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.entity.LawyersInfo
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.RetrofitClient
import com.gkzxhn.helpout.utils.showToast
import kotlinx.android.synthetic.main.default_top.*
import kotlinx.android.synthetic.main.expert_activity.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @classname：ExpertActivity
 * @author：liushaoxiang
 * @date：2019/2/26 4:53 PM
 * @description：专家入驻
 */

class ExpertActivity : BaseActivity() {

    override fun provideContentViewId(): Int {
        return R.layout.expert_activity
    }

    override fun init() {
        initTopTitle()
        v_expert_bg_1.setOnClickListener {
            showToast("敬请期待")
        }
        v_expert_bg_2.setOnClickListener {
            when (App.SP.getString(Constants.SP_CERTIFICATIONSTATUS, "")) {
            /****** 待认证 ******/
                Constants.PENDING_CERTIFIED -> {
                    val intent = Intent(this, QualificationAuthenticationEditActivity::class.java)
                    intent.putExtra("again_Authentication", false)
                    startActivity(intent)
                }
            /****** 已认证 ******/
                Constants.CERTIFIED -> {
                    startActivity(Intent(this, QualificationAuthenticationShowActivity::class.java))
                }
                else -> {
                    startActivity(Intent(this, QualificationAuthenticationActivity::class.java))
                }
            }
        }
    }

    private fun initTopTitle() {
        tv_default_top_title.text = "专家入驻"
        iv_default_top_back.setOnClickListener {
            finish()
        }
    }

    /**
     * @methodName： created by liushaoxiang on 2018/10/22 3:31 PM.
     * @description：更新律师状态
     */
    private fun getLawyersInfo() {
        RetrofitClient.getInstance(this).mApi.getLawyersInfo()
                .subscribeOn(Schedulers.io())
                ?.unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<LawyersInfo>(this) {
                    override fun success(t: LawyersInfo) {
                        App.EDIT.putString(Constants.SP_CERTIFICATIONSTATUS, t.certificationStatus)?.commit()
                    }
                })
    }

    override fun onResume() {
        super.onResume()
        getLawyersInfo()
    }


}