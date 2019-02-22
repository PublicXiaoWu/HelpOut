package com.gkzxhn.helpout.activity

import android.content.Context
import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.adapter.LawsServiceAdapter
import com.gkzxhn.helpout.customview.RecyclerSpace
import com.gkzxhn.helpout.entity.UIInfo.LawChannel
import com.gkzxhn.helpout.extensions.dp2px
import kotlinx.android.synthetic.main.activity_laws_service.*
import kotlinx.android.synthetic.main.default_top.*

class LawsServiceActivity: BaseActivity() {
    override fun init() {
        initToolbar()
        initRecyclerView()
    }

    override fun provideContentViewId(): Int {
        return R.layout.activity_laws_service
    }

    lateinit var mAdapter: LawsServiceAdapter

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, LawsServiceActivity::class.java))
        }
    }

    private fun initToolbar() {
        tv_default_top_title.text = getString(R.string.laws_counsel)
        iv_default_top_back.setOnClickListener {
            finish()
        }
    }

    private fun initRecyclerView() {
        rv_laws_service.layoutManager = GridLayoutManager(this, 2)
        mAdapter = LawsServiceAdapter(LawChannel.CHANNELS)
        mAdapter.openLoadAnimation()
        rv_laws_service.addItemDecoration(RecyclerSpace(0.5f.dp2px().toInt(), resources.getColor(R.color.gray_line)))
        mAdapter.setOnItemClickListener { adapter, view, position ->
            PublishOrderActivity.launch(this, (adapter.data[position] as LawChannel).category)
        }
        rv_laws_service.adapter = mAdapter
    }
}