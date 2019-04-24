package com.gkzxhn.helpout.activity

import android.content.Intent
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewTreeObserver
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.adapter.LivesCircleAdapter
import com.gkzxhn.helpout.customview.PullToRefreshLayout
import com.gkzxhn.helpout.customview.RecyclerSpace
import com.gkzxhn.helpout.entity.LivesCircle
import com.gkzxhn.helpout.entity.LivesCircleDetails
import com.gkzxhn.helpout.extensions.dp2px
import com.gkzxhn.helpout.presenter.LivesCirclePresenter
import com.gkzxhn.helpout.utils.StatusBarUtil
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.LivesCircleView
import com.gkzxhn.helpout.view.ObservableAlphaScrollView
import kotlinx.android.synthetic.main.activity_lives_circle.*


/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/16 10:13 AM
 * @description：生活圈
 */
class LivesCircleActivity : BaseActivity(), LivesCircleView, ObservableAlphaScrollView.OnAlphaScrollChangeListener {


    lateinit var mPresenter: LivesCirclePresenter
    lateinit var mAdapter: LivesCircleAdapter

    private var mTitleHeight: Int = 0
    private var mHeadHeight: Int = 0
    private var mDistance: Int = 0
    private val mDistanceY = 30// 设置一个临界值吧

    var loadMore = false
    var page = 0

    override fun setLastPage(lastPage: Boolean, page: Int) {
        this.loadMore = !lastPage
        this.page = page
    }

    override fun offLoadMore() {
        //加载更多取消
        if (loading_more.isLoading) {
            loading_more?.finishLoading()
        }
    }

    override fun updateData(data: List<LivesCircle.ContentBean>) {
        mAdapter.setNewData(data)
    }

    override fun provideContentViewId(): Int {
        /****** 状态栏相关设置 ******/
        setStatus()
        return R.layout.activity_lives_circle
    }

    /****** 状态栏相关设置 ******/
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
        mPresenter = LivesCirclePresenter(this, this)
        val viewTreeObserver = ll_lives_head?.viewTreeObserver
        viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                ll_lives_head.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mTitleHeight = ll_lives_title.measuredHeight
                mHeadHeight = ll_lives_head.measuredHeight
                mDistance = mHeadHeight - mTitleHeight
                s_lives_scrollView.setOnAlphaScrollChangeListener(this@LivesCircleActivity)
            }
        })

        initRecyclerView()

        val livesCircleType = intent.getIntExtra("LivesCircleType", 1)
        when (livesCircleType) {
            /****** 所有人的生活圈 ******/
            1 -> mPresenter.getLivesCircle("0", "10")
            /****** 我的生活圈 ******/
            2 -> mPresenter.getMyLivesCircle("0", "10")
            /****** 某个人的生活圈 ******/
            3 -> {

                val userName = intent.getStringExtra("userName")
                mPresenter.getLivesCircleByUserName(userName,"0", "10")

            }


        }

        iv_lives_back.setOnClickListener {
            finish()
        }

        iv_take_picture.setOnClickListener {
            PublishLifeCircleActivity.launch(this)
        }

        //加载更多
        loading_more.setOnLoadMoreListener(object : com.gkzxhn.helpout.customview.LoadMoreWrapper.OnLoadMoreListener {
            override fun onLoadMore() {
                if (loadMore) {
                    mPresenter.getLivesCircle((page + 1).toString(), "10")
                } else {
                    offLoadMore()
                }
            }
        })

        //下啦刷新
        loading_refresh.setOnRefreshListener(object : PullToRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                mPresenter.getLivesCircle("0", "10")
                loading_refresh?.finishRefreshing()
            }
        }, 1)
    }

    private fun initRecyclerView() {
        rcv_lives_circle.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = LivesCircleAdapter(null)
        mAdapter.openLoadAnimation()
        rcv_lives_circle.addItemDecoration(RecyclerSpace(1f.dp2px().toInt(), ContextCompat.getColor(this, R.color.gray_line)))
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val contentBean = adapter.data[position] as LivesCircle.ContentBean
            val intent = Intent(this, LivesCircleDetailsActivity::class.java)
            intent.putExtra("LivesCircleId", contentBean.id)
            startActivity(intent)
        }
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                /****** 点赞 ******/
                R.id.v_item_lives_circle_like_number -> {

                    val contentBean = adapter.data[position] as LivesCircle.ContentBean
                    if (contentBean.praisesCircleoffriends) {
                        showToast(getString(R.string.liked))
                    } else {
                        mPresenter.praise(contentBean.id!!, position)

                    }
                }
            }
        }
        rcv_lives_circle.adapter = mAdapter
        mAdapter.setEmptyView(R.layout.empty_default, rcv_lives_circle)
    }

    override fun finishActivity() {
        finish()
    }

    override fun loadLivesCircleDetailsUI(t: LivesCircleDetails) {
    }

    /****** 点赞成功 ******/
    override fun praiseSuccess(position: Int) {
        val contentBean = mAdapter.getItem(position) as LivesCircle.ContentBean
        contentBean.praiseNum = contentBean.praiseNum + 1
        contentBean.praisesCircleoffriends = true
        mAdapter.setDataChange(position, contentBean)
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