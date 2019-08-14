package com.gkzxhn.helpout.fragment

import android.content.Intent
import android.view.View
import com.gkzxhn.helpout.BuildConfig
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.LawsServiceActivity
import com.gkzxhn.helpout.activity.LegalConsultingActivity
import com.gkzxhn.helpout.activity.NotificationActivity
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.rxbus.RxBusBean
import com.gkzxhn.helpout.net.NetWorkCodeInfo
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.logE
import com.gkzxhn.helpout.utils.showToast
import kotlinx.android.synthetic.main.home_fragment.*
import rx.android.schedulers.AndroidSchedulers


/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/2/20 4:59 PM
 * @description：首页
 */
class HomeFragment : BaseFragment() {

    override fun provideContentViewId(): Int {
        return R.layout.home_fragment
    }

    override fun init() {
        val isDebug = BuildConfig.DEBUG
        if (isDebug) {
            tv_home_top_environment.visibility = View.VISIBLE
            when (NetWorkCodeInfo.environment) {
                NetWorkCodeInfo.ENVIRONMENT_TEST -> tv_home_top_environment.text = "TESTING ENVIRONMENT"
                NetWorkCodeInfo.ENVIRONMENT_DEV -> tv_home_top_environment.text = "DEV ENVIRONMENT"
                NetWorkCodeInfo.ENVIRONMENT_FORMAL -> tv_home_top_environment.text = "FORMAL ENVIRONMENT"
            }
        } else {
            tv_home_top_environment.visibility = View.GONE
        }

        /****** 接受控件小红点的消息 ******/
        RxBus.instance.toObserverable(RxBusBean.HomeTopRedPoint::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    v_top_red_point.visibility = if (it.show) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }, {
                    it.message.toString().logE(this)
                })
    }

    override fun initListener() {
        v_home_psychological.setOnClickListener {
            context?.showToast(getString(R.string.please_expectantly))
        }
        v_home_legal_advice.setOnClickListener {
            if (ProjectUtils.certificationStatus()) {
                startActivity(Intent(context, LegalConsultingActivity::class.java))
            } else {
                activity?.let { it1 -> LawsServiceActivity.launch(it1) }
            }
        }
        v_home_business.setOnClickListener {
            context?.showToast(getString(R.string.please_expectantly))
        }
        iv_home_have_a_tree.setOnClickListener {
            context?.showToast(getString(R.string.please_expectantly))
        }

        iv_main_message_top.setOnClickListener {
            startActivity(Intent(context, NotificationActivity::class.java))
        }

    }


}
