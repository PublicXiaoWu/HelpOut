package com.gkzxhn.helpout.activity

import android.content.Intent
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.utils.showToast
import kotlinx.android.synthetic.main.activity_choose_majors.*
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：ChooseMajorsActivity
 * @author：liushaoxiang
 * @date：2018/10/15 3:51 PM
 * @description：专业领域 选择
 */

class ChooseMajorsActivity : BaseActivity() {

    var selectNumber = 0
    var selectString: ArrayList<String>? = arrayListOf()
    override fun init() {
        initTopTitle()
        initSelect()
    }

    private fun initSelect() {
        val intentSelectString = intent.getStringArrayListExtra(Constants.INTENT_SELECTSTRING) ?: return
        for (str: String in intentSelectString) {
            initSelect(str)
        }
    }

    override fun provideContentViewId(): Int {
        return R.layout.activity_choose_majors
    }

    private fun initTopTitle() {
        tv_default_top_title.text = getString(R.string.choose_professional)
        tv_default_top_end.visibility = View.VISIBLE
        tv_default_top_end.text = getString(R.string.ok)
        iv_default_top_back.setOnClickListener {
            onBackPressed()
        }
        tv_default_top_end.setOnClickListener {
            onBackPressed()
        }
    }

    fun onClickChooseMajors(view: View) {
        when (view.id) {
            R.id.v_choose_majors_bg_1 -> {
                changeViewVisibility(iv_choose_majors_1, 1)
            }
            R.id.v_choose_majors_bg_2 -> {
                changeViewVisibility(iv_choose_majors_2, 2)
            }
            R.id.v_choose_majors_bg_3 -> {
                changeViewVisibility(iv_choose_majors_3, 3)
            }
            R.id.v_choose_majors_bg_4 -> {
                changeViewVisibility(iv_choose_majors_4, 4)
            }
            R.id.v_choose_majors_bg_5 -> {
                changeViewVisibility(iv_choose_majors_5, 5)
            }
            R.id.v_choose_majors_bg_6 -> {
                changeViewVisibility(iv_choose_majors_6, 6)
            }
            R.id.v_choose_majors_bg_7 -> {
                changeViewVisibility(iv_choose_majors_7, 7)
            }
            R.id.v_choose_majors_bg_8 -> {
                changeViewVisibility(iv_choose_majors_8, 8)
            }
        }
    }

    private fun changeViewVisibility(view: View, number: Int) {

        if (view.visibility == View.VISIBLE) {
            view.visibility = View.GONE
            selectString?.remove(findMajors(number))
            selectNumber--
        } else if (selectNumber < 3) {
            view.visibility = View.VISIBLE
            selectString?.add(findMajors(number))
            selectNumber++
        } else {
            showToast(getString(R.string.at_more_three))
        }
    }

    private fun findMajors(number: Int): String {

        return when (number) {
            1 -> getString(R.string.property_dispute)
            2 -> getString(R.string.msttishr_family)
            3 -> getString(R.string.traffic_accident)
            4 -> getString(R.string.work_cover)
            5 -> getString(R.string.icon_contract_dispute)
            6 -> getString(R.string.criminal_defense)
            7 -> getString(R.string.property_disputes)
            8 -> getString(R.string.labour_employment)
            else -> {
                "unknown"
            }
        }
    }

    private fun initSelect(string: String) {
        when (string) {
            getString(R.string.property_dispute) -> {
                changeViewVisibility(iv_choose_majors_1, 1)
            }
            getString(R.string.msttishr_family) -> {
                changeViewVisibility(iv_choose_majors_2, 2)
            }
            getString(R.string.traffic_accident) -> {
                changeViewVisibility(iv_choose_majors_3, 3)
            }
            getString(R.string.work_cover) -> {
                changeViewVisibility(iv_choose_majors_4, 4)
            }
           getString(R.string.icon_contract_dispute) -> {
                changeViewVisibility(iv_choose_majors_5, 5)
            }
           getString(R.string.criminal_defense) -> {
                changeViewVisibility(iv_choose_majors_6, 6)
            }
            getString(R.string.property_disputes) -> {
                changeViewVisibility(iv_choose_majors_7, 7)
            }
            getString(R.string.labour_employment) -> {
                changeViewVisibility(iv_choose_majors_8, 8)
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putStringArrayListExtra(Constants.RESULT_CHOOSE_MAJORS, selectString)
        setResult(Constants.RESULTCODE_CHOOSE_MAJORS, intent)
        finish()
    }

}