package com.gkzxhn.helpout.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Explanation:
 * @author LSX
 *    -----2018/9/7
 */

class MainAdapter(  fm: FragmentManager, private var mFragments: List<Fragment>?) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        return mFragments!![position]
    }

    override fun getCount(): Int {
        return mFragments?.size ?: 0
    }

}