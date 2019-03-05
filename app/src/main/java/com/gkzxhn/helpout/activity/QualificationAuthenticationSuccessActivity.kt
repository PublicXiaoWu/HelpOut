package com.gkzxhn.helpout.activity

import com.gkzxhn.helpout.R
import kotlinx.android.synthetic.main.activity_qualification_authentication.tv_qualification_authentication_go as qualificationAuthentication
import kotlinx.android.synthetic.main.activity_qualification_authentication.tv_qualification_authentication_message as qualificationAuthenticationMessage
import kotlinx.android.synthetic.main.default_top.iv_default_top_back as back
import kotlinx.android.synthetic.main.default_top.tv_default_top_title as top_title

/**
 * Explanation: 资格认证申请成功页面
 * @author LSX
 *    -----2018/9/6
 */

class QualificationAuthenticationSuccessActivity : BaseActivity() {

    override fun provideContentViewId(): Int {
        return R.layout.activity_qualification_authentication_success
    }

    override fun init() {

        initTitleTop()
    }

    private fun initTitleTop() {
        back.setOnClickListener {
            finish()
        }
        top_title.text = "资格认证"
    }

}
