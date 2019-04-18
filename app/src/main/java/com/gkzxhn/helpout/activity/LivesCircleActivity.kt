package com.gkzxhn.helpout.activity

import android.content.Intent
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.ViewTreeObserver
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.adapter.LivesCircleAdapter
import com.gkzxhn.helpout.customview.RecyclerSpace
import com.gkzxhn.helpout.entity.LivesCircle
import com.gkzxhn.helpout.extensions.dp2px
import com.gkzxhn.helpout.utils.StatusBarUtil
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.ObservableAlphaScrollView
import kotlinx.android.synthetic.main.activity_lives_circle.*

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/16 10:13 AM
 * @description：生活圈
 */
class LivesCircleActivity : BaseActivity(), ObservableAlphaScrollView.OnAlphaScrollChangeListener {
    private var mTitleHeight: Int = 0
    private var mHeadHeight: Int = 0
    private var mDistance: Int = 0
    private val mDistanceY = 30// 设置一个临界值吧

    override fun provideContentViewId(): Int {
        /****** 状态栏相关设置 ******/
        setStatus()
        return R.layout.activity_lives_circle
    }

    private fun setStatus() {
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }

    override fun init() {
        val viewTreeObserver = ll_lives_head?.viewTreeObserver
        viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                ll_lives_head.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mTitleHeight = ll_lives_title.measuredHeight
                mHeadHeight = ll_lives_head.measuredHeight
                mDistance = mHeadHeight - mTitleHeight
                Log.e("result  mTitleHead = ", mTitleHeight.toString() + "")
                Log.e("result  mHeadHeight = ", mHeadHeight.toString() + "")
                Log.e("result  mDistance = ", mDistance.toString() + "")
                s_lives_scrollView.setOnAlphaScrollChangeListener(this@LivesCircleActivity)
            }
        })
        val items = ArrayList<LivesCircle>()

        for (s in 1..50) {
            val element = LivesCircle()
            element.name = "刘德华" + s
            element.context = "苹果树已经开始丰收结果啦！现在可以接受预订哟，喜欢的朋友快来吧。" + s
            val i = s % 10
            element.imageNumber = i
            element.time = "2019-01-05 16:45:01 "
            element.like = s % 7
            element.commentnumber = s % 5

            val list = ArrayList<String>()
            for (number in 1..i) {
                when (number) {
                    1 -> list.add("http://img2.imgtn.bdimg.com/it/u=1782007428,3628702860&fm=27&gp=0.jpg")
                    2 -> list.add("http://img0.imgtn.bdimg.com/it/u=2473592401,1264512939&fm=27&gp=0.jpg")
                    3 -> list.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3176289000,2370082435&fm=27&gp=0.jpg")
                    else -> list.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4071270602,4187261958&fm=27&gp=0.jpg")
                }

            }
            element.imageList = list
            items.add(element)
        }

        rcv_lives_circle.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val mAdapter = LivesCircleAdapter(items)
        mAdapter.openLoadAnimation()
        rcv_lives_circle.addItemDecoration(RecyclerSpace(1f.dp2px().toInt(), ContextCompat.getColor(this, R.color.gray_line)))
        mAdapter.setOnItemClickListener { adapter, view, position ->
            startActivity(Intent(this, LivesCircleDetailsActivity::class.java))
        }

        rcv_lives_circle.adapter = mAdapter


        iv_lives_back.setOnClickListener {
            finish()
        }

        iv_take_picture.setOnClickListener {
            showToast("拍照")
        }
    }


    override fun onVerticalScrollChanged(t: Int) {
        if (t <= mDistance - mDistanceY) {
            tv_lives_title.alpha = 0f
            iv_lives_back.isSelected = false
            iv_take_picture.isSelected = false
            ll_lives_title.setBackgroundColor(Color.argb(0, 242, 242, 242))
        } else if (t <= mDistance) {
            tv_lives_title.alpha = 0f
            iv_lives_back.isSelected = false
            iv_take_picture.isSelected = false
        } else if (t <= mDistance + mDistanceY) {
            tv_lives_title.alpha = 1f
            iv_lives_back.isSelected = true
            iv_take_picture.isSelected = true
            ll_lives_title.setBackgroundColor(Color.argb(0, 242, 242, 242))
        } else {
            tv_lives_title.alpha = 1f
            iv_lives_back.isSelected = true
            iv_take_picture.isSelected = true
            ll_lives_title.setBackgroundColor(Color.argb(255, 242, 242, 242))
        }
    }


}