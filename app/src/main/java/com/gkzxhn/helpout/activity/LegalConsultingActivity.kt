package com.gkzxhn.helpout.activity

import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.fragment.LawyerFragment
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：法律咨询
 * @author：liushaoxiang
 * @date：2019/2/20 5:06 PM
 * @description：
 */
class LegalConsultingActivity : BaseActivity() {

    override fun provideContentViewId(): Int {
        return R.layout.activity_legalconsulting
    }

    override fun init() {
        initTitle()

        val beginTransaction = getSupportFragmentManager().beginTransaction()
        //提交事务
        beginTransaction.add(R.id.fl_legal_consulting, LawyerFragment()).commit();

    }

    private fun initTitle() {

        tv_default_top_title.text = "法律咨询"
        iv_default_top_back.setOnClickListener { finish() }
    }

}