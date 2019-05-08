package com.gkzxhn.helpout.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Handler
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

    lateinit var linearLayoutManager: LinearLayoutManager


    companion object {
        val PUBLISH_REQUEST_CODE = 1
        val PUBLISH_REQUEST_DETAIL_CODE = 2
    }

    var lastScrollY = 0
    val h = DensityUtil.dp2px(180f)

    internal var mHandler = Handler(Handler.Callback {
        val scollYDistance = getScollYDistance()
        var scrollY = scollYDistance
        if (lastScrollY < h) {
            scrollY = Math.min(h, scrollY)
            mScrollY = if (scrollY > h) h else scrollY
            buttonBarLayout.alpha = 1f
            toolbar.setBackgroundColor(Color.argb(0, 242, 242, 242))
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

    override fun finishRefresh() {
        loading_refresh.finishRefresh()
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

        livesCircleType = intent.getIntExtra("LivesCircleType", 1)

        /****** 根据不同的生活圈加载不同的UI ******/
        getOtherUIByLivesCircleType(livesCircleType)

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
            }
        })
        subscribe()
    }


    /****** 根据不同的生活圈加载不同的UI ******/
    private fun getOtherUIByLivesCircleType(livesCircleType: Int) {

        when (livesCircleType) {
            /****** 所有人的生活圈 ******/
            1 -> iv_take_picture.visibility=View.VISIBLE
            /****** 我的生活圈 ******/
            2 -> {
                iv_take_picture.visibility=View.GONE
            }
            /****** 某个人的生活圈 ******/
            3 -> {
                iv_take_picture.visibility=View.GONE
            }
        }
    }

    /****** 根据不同的生活圈加载不同的数据 ******/
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
        val headView = View.inflate(this, R.layout.lives_circle_head_view, null)
        val emptyView = View.inflate(this, R.layout.lives_circle_empty_view, null)
        mAdapter.setHeaderView(headView)
        mAdapter.emptyView = emptyView
        rcv_lives_circle.adapter = mAdapter

        mAdapter.setOnItemClickListener { adapter, view, position ->
            val contentBean = adapter.data[position] as LivesCircle.ContentBean
            val intent = Intent(this, LivesCircleDetailsActivity::class.java)
            contentBean.id?.let {
                intent.putExtra("LivesCircleId", it)
                intent.putExtra("position", position)
                startActivityForResult(intent,PUBLISH_REQUEST_DETAIL_CODE)
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
        rcv_lives_circle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                mHandler.sendEmptyMessage(1)
            }
        })

    }

    /**
     * 获得recyclerview滑动高度
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
            /****** 从详情返回 ******/
            PUBLISH_REQUEST_DETAIL_CODE ->{
                if (resultCode == Activity.RESULT_OK) {
                    val commentNum = data?.getIntExtra(IntentConstants.commentNum,0)
                    val praiseNum = data?.getIntExtra(IntentConstants.praiseNum,0)
                    val position = data?.getIntExtra(IntentConstants.position,0)
                    val praisesCircleoffriends = data?.getBooleanExtra(IntentConstants.praisesCircleoffriends,false)
                    val contentBean = mAdapter.getItem(position!!) as LivesCircle.ContentBean
                    contentBean.praiseNum = praiseNum!!
                    contentBean.commentNum = commentNum!!
                    contentBean.praisesCircleoffriends = praisesCircleoffriends!!
                    mAdapter.setDataChange(position, contentBean)

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
        val rxSubscription = RxBus.instance.toObserverable(RxBusBean.PublishEntity::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.code == 0) {
                        getDataWithType(livesCircleType)
                    } else {
                        getDataWithType(livesCircleType)
                    }
                }
        mCompositeSubscription.add(rxSubscription)
    }
}