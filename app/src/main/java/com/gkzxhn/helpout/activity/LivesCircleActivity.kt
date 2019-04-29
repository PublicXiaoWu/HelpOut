package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.adapter.LivesCircleAdapter
import com.gkzxhn.helpout.common.IntentConstants
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.LivesCircle
import com.gkzxhn.helpout.entity.LivesCircleDetails
import com.gkzxhn.helpout.entity.rxbus.RxBusBean
import com.gkzxhn.helpout.presenter.LivesCirclePresenter
import com.gkzxhn.helpout.utils.StatusBarUtils
import com.gkzxhn.helpout.utils.StringUtils
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.LivesCircleView
import com.luck.picture.lib.entity.LocalMedia
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import com.scwang.smartrefresh.layout.util.DensityUtil
import kotlinx.android.synthetic.main.activity_lives_circle.*
import rx.android.schedulers.AndroidSchedulers
import java.util.*


/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/16 10:13 AM
 * @description：生活圈
 */
class LivesCircleActivity : BaseActivity(), LivesCircleView {

    lateinit var mPresenter: LivesCirclePresenter
    lateinit var mAdapter: LivesCircleAdapter

    private var mOffset = 0
    private var mScrollY = 0

    var loadMore = false
    var page = 0

    var livesCircleType = 1

    companion object {
        val PUBLISH_REQUEST_CODE = 1
    }

    override fun setLastPage(lastPage: Boolean, page: Int) {
        this.loadMore = !lastPage
        this.page = page
    }

    override fun offLoadMore() {
        mAdapter.loadMoreComplete()
    }

    override fun endLoadMore() {
        mAdapter.loadMoreEnd()
    }

    override fun updateData(data: List<LivesCircle.ContentBean>, isFirst: Boolean) {
        if (isFirst) {
            mAdapter.setNewData(data)
            mAdapter.setEnableLoadMore(true)//这里的作用是放开 可以上拉加载

        } else {
            mAdapter.addData(data)
        }
    }

    override fun provideContentViewId(): Int {
        return R.layout.activity_lives_circle
    }


    override fun init() {
        mPresenter = LivesCirclePresenter(this, this)
        //状态栏透明和间距处理
        StatusBarUtils.immersive(this)
        StatusBarUtils.setPaddingSmart(this, toolbar)

        initRecyclerView()

//        buttonBarLayout.alpha = 0f
//        toolbar.setBackgroundColor(0)

        livesCircleType = intent.getIntExtra("LivesCircleType", 1)
        getDataWithType(livesCircleType)

        iv_lives_back.setOnClickListener {
            finish()
        }

        iv_take_picture.setOnClickListener {
            PublishLifeCircleActivity.launch4Result(this, PUBLISH_REQUEST_CODE)
        }

        //下啦刷新
        loading_refresh.setOnRefreshListener {
            mAdapter.setEnableLoadMore(false)//这里的作用是防止下拉刷新的时候还可以上拉加载
            getDataWithType(livesCircleType)
            loading_refresh.finishRefresh(500)
        }

        loading_refresh.setOnMultiPurposeListener(object : SimpleMultiPurposeListener() {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                refreshLayout.finishRefresh(3000)
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                refreshLayout.finishLoadMore(2000)
            }

            override fun onHeaderMoving(header: RefreshHeader?, isDragging: Boolean, percent: Float, offset: Int, headerHeight: Int, maxDragHeight: Int) {
                mOffset = offset / 2
                parallax.translationY = (mOffset - mScrollY).toFloat()
//                toolbar.alpha = 1 - Math.min(percent, 1f)
            }
        })
        subscribe()
    }

    private fun getDataWithType(livesCircleType: Int, page: Int = 0) {
        when (livesCircleType) {
            /****** 所有人的生活圈 ******/
            1 -> mPresenter.getLivesCircle(page)
            /****** 我的生活圈 ******/
            2 -> {
                mPresenter.getMyLivesCircle(page)
            }
            /****** 某个人的生活圈 ******/
            3 -> {
                val userName = intent.getStringExtra("userName")
                mPresenter.getLivesCircleByUserName(userName, page)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun initRecyclerView() {
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcv_lives_circle.layoutManager = linearLayoutManager
        mAdapter = LivesCircleAdapter(null)
        mAdapter.setOnLoadMoreListener(requestLoadMoreListener, rcv_lives_circle)
        mAdapter.setHasStableIds(true)
        mAdapter.openLoadAnimation()
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val contentBean = adapter.data[position] as LivesCircle.ContentBean
            val intent = Intent(this, LivesCircleDetailsActivity::class.java)
            contentBean.id?.let {
                intent.putExtra("LivesCircleId", it)
                startActivity(intent)
            }
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
        val headView = View.inflate(this, R.layout.lives_circle_head_view, null)
        mAdapter.setHeaderView(headView)
        rcv_lives_circle.adapter = mAdapter

        rcv_lives_circle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                mHandler.sendEmptyMessage(1)
            }
        })

    }

    var lastScrollY = 0
    val h = DensityUtil.dp2px(180f)

    internal var mHandler = Handler(Handler.Callback {
        val scollYDistance = getScollYDistance()
        Log.e("xiaowuSSS", scollYDistance.toString()+"--h:"+h)
        val color = ContextCompat.getColor(this, R.color.main_gary_bg) and 0x00ffffff
        var scrollY = scollYDistance
        if (lastScrollY < h) {
            scrollY = Math.min(h, scrollY)
            mScrollY = if (scrollY > h) h else scrollY
//            buttonBarLayout.alpha = 1f * mScrollY / h
            buttonBarLayout.alpha = 1f
            toolbar.setBackgroundColor(Color.argb(0, 242, 242, 242))

//            toolbar.setBackgroundColor(255 * mScrollY / h shl 24 or color)
            parallax.translationY = (mOffset - mScrollY).toFloat()

            iv_take_picture.isSelected=false
            iv_lives_back.isSelected=false
            tv_lives_title.alpha=0f
        }else{
            iv_take_picture.isSelected=true
            iv_lives_back.isSelected=true
            tv_lives_title.alpha=1f
            buttonBarLayout.alpha = 1f
            toolbar.setBackgroundColor(Color.argb(255, 242, 242, 242))
        }
        lastScrollY = scrollY
        true
    })

    lateinit var linearLayoutManager: LinearLayoutManager
    /**
     * 不同Item VERTICAL
     */
    fun getScollYDistance(): Int {
        val position = linearLayoutManager.findFirstVisibleItemPosition()
        val firstVisiableChildView = linearLayoutManager.findViewByPosition(position)
        val itemHeight = firstVisiableChildView?.height
        return position * itemHeight!! - firstVisiableChildView.getTop()
    }


    /****** 设置上拉加载的监听  ******/
    val requestLoadMoreListener = BaseQuickAdapter.RequestLoadMoreListener {
        Log.e("xiaowu666", "-----------开始加载更多")
        if (loadMore) {
            getDataWithType(livesCircleType, page + 1)
        } else {
            endLoadMore()
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PUBLISH_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val content = data?.getStringExtra(IntentConstants.CONTENT)
                    val selectList = data?.getParcelableArrayListExtra<LocalMedia>(IntentConstants.IMAGES)
                    addLocalData(content, selectList)
                }
            }
            else -> {
            }
        }
    }

    fun addLocalData(content: String?, selectList: ArrayList<LocalMedia>?) {
        val currentTime = StringUtils.parseTime2TString(System.currentTimeMillis())
        val contentBean = LivesCircle.ContentBean()
        contentBean.content = content
        contentBean.localImages = selectList
        contentBean.createdTime = currentTime
        mAdapter.addData(0, contentBean)
    }

    fun subscribe() {
        RxBus.instance.toObserverable(RxBusBean.PublishEntity::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.code == 0) {
                        getDataWithType(livesCircleType)
                    } else {
                        getDataWithType(livesCircleType)
                    }
                }
    }

}