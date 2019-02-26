package com.gkzxhn.helpout.activity

import android.content.Intent
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.showToast
import kotlinx.android.synthetic.main.default_top.*
import kotlinx.android.synthetic.main.expert_activity.*

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

            if (ProjectUtils.certificationStatus()) {
                startActivity(Intent(this, QualificationAuthenticationShowActivity::class.java))
            } else {
                startActivity(Intent(this, QualificationAuthenticationActivity::class.java))
            }
        }
    }

    private fun initTopTitle() {
        tv_default_top_title.text = "专家入驻"
        iv_default_top_back.setOnClickListener {
            finish()
        }
    }


}