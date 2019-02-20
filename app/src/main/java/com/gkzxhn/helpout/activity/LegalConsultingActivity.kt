package com.gkzxhn.helpout.activity

import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.fragment.MainFragment

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


        val beginTransaction = getSupportFragmentManager().beginTransaction()
        //提交事务
        beginTransaction.add(R.id.fl_legal_consulting, MainFragment()).commit();

    }

}