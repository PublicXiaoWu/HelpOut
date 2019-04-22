package com.gkzxhn.helpout.activity

import android.content.Context
import android.content.Intent
import com.gkzxhn.helpout.R

class LifeCircleActivity: BaseActivity() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, LifeCircleActivity::class.java))
        }
    }

    override fun init() {

    }

    override fun provideContentViewId(): Int {
        return R.layout.activity_life_circle
    }
}