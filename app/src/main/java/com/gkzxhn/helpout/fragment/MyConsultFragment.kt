package com.gkzxhn.helpout.fragment

import android.support.v4.view.ViewPager
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.adapter.MainAdapter
import com.gkzxhn.helpout.utils.showToast
import kotlinx.android.synthetic.main.myconsult_fragment.*

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/2/20 5:29 PM
 * @description：我的咨询
 */
class MyConsultFragment : BaseFragment() {

    var tbList: MutableList<BaseFragment>? = null
    private var mainAdapter: MainAdapter? = null
    
    override fun provideContentViewId(): Int {
        return R.layout.myconsult_fragment
    }

    override fun init() {

        tbList = ArrayList()
        tbList?.add(OrderDisposeFragment())
//        tbList?.add(OrderReceivingFragment())
        mainAdapter = MainAdapter(childFragmentManager, tbList)
        vp_consult.adapter = mainAdapter
        vp_consult.offscreenPageLimit = 3
    }

    override fun initListener() {
        v_tab_1.setOnClickListener {
            vp_consult.currentItem = 0
            selectOneItem()
        }
        v_tab_2.setOnClickListener {
//            vp_consult.currentItem = 1
            selectTwoItem()
        }

        vp_consult.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                if (p0 == 0) {
                    selectOneItem()
                } else if (p0 == 1) {
                    selectTwoItem()
                }
            }

            override fun onPageScrollStateChanged(p0: Int) {
            }
        })
    }


    private fun selectTwoItem() {
        context?.showToast("敬请期待")
//        resources.getColor(R.color.main_top_gary).let { it1 -> tv_tab_1.setTextColor(it1) }
//        resources.getColor(R.color.main_top_blue).let { it1 -> tv_tab_2.setTextColor(it1) }
//        v_consult_select_line1.visibility = View.INVISIBLE
//        v_consult_select_line2.visibility = View.VISIBLE
    }

    private fun selectOneItem() {
        resources.getColor(R.color.main_top_blue).let { it1 -> tv_tab_1.setTextColor(it1) }
        resources.getColor(R.color.main_top_gary).let { it1 -> tv_tab_2.setTextColor(it1) }
        v_consult_select_line1.visibility = View.VISIBLE
        v_consult_select_line2.visibility = View.INVISIBLE
    }


}
