package com.gkzxhn.helpout.fragment

import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.entity.LawyersInfo
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.RetrofitClient
import com.gkzxhn.helpout.utils.ProjectUtils
import kotlinx.android.synthetic.main.lawyer_fragment.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Explanation: 律师抢单页面
 * @author LSX
 *    -----2018/9/7
 */

class LawyerFragment : BaseFragment() {

    override fun provideContentViewId(): Int {
        return R.layout.lawyer_fragment
    }

    override fun init() {
        val beginTransaction = fragmentManager?.beginTransaction()
        //提交事务
        beginTransaction?.add(R.id.fl_lawyer, OrderReceivingFragment())?.commit();

        getLawyersInfo()
    }

    private fun loadTopUI(t: LawyersInfo) {
        val categories = t.categories
        tv_main_name.text = t.name
        tv_home_address.text = "执业律所：" + t.lawOffice
        if (categories != null && categories.isNotEmpty() && ProjectUtils.certificationStatus()) {
            tv_home_type1.visibility = View.VISIBLE
            tv_home_type1.text = ProjectUtils.categoriesConversion(categories[0])
            if (categories.size > 1) {
                tv_home_type2.visibility = View.VISIBLE
                tv_home_type2.text = ProjectUtils.categoriesConversion(categories[1])
            }
            if (categories.size > 2) {
                tv_home_type3.visibility = View.VISIBLE
                tv_home_type3.text = ProjectUtils.categoriesConversion(categories[2])
            }
        } else {
            tv_home_type1.visibility = View.INVISIBLE
            tv_home_type2.visibility = View.INVISIBLE
            tv_home_type3.visibility = View.INVISIBLE
        }

        ProjectUtils.loadMyIcon(context, iv_main_icon)

    }


    override fun initListener() {
    }

    /**
     * @methodName： created by liushaoxiang on 2018/10/22 3:31 PM.
     * @description：获取律师信息
     */
    private fun getLawyersInfo() {
        context?.let {
            RetrofitClient.getInstance(it).mApi.getLawyersInfo()
                    .subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserver<LawyersInfo>(it) {
                        override fun success(t: LawyersInfo) {
                            App.EDIT.putString(Constants.SP_CERTIFICATIONSTATUS, t.certificationStatus)?.commit()
                            loadTopUI(t)

                        }

                    })
        }
    }


    override fun onResume() {
        super.onResume()
        getLawyersInfo()
    }


}