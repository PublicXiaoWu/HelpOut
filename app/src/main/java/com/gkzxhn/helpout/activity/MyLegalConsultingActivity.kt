package com.gkzxhn.helpout.activity

import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.fragment.MyConsultFragment
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：我的咨询
 * @author：liushaoxiang
 * @date：2019/2/20 5:06 PM
 * @description：
 */
class MyLegalConsultingActivity : BaseActivity() {

    override fun provideContentViewId(): Int {
        return R.layout.activity_legalconsulting
    }

    override fun init() {
        initTitle()

        val beginTransaction = supportFragmentManager.beginTransaction()
        //提交事务
        beginTransaction.add(R.id.fl_legal_consulting, MyConsultFragment()).commit();

    }

    private fun initTitle() {
        tv_default_top_title.text = "我的咨询"
        iv_default_top_back.setOnClickListener { finish() }
    }

}