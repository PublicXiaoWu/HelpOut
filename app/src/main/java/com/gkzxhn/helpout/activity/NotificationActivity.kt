package com.gkzxhn.helpout.activity

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.adapter.NotificationInfoAdapter
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.customview.PullToRefreshLayout
import com.gkzxhn.helpout.entity.NotificationInfoList
import com.gkzxhn.helpout.entity.RxBusBean
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.RetrofitClientLogin
import com.gkzxhn.helpout.utils.DisplayUtils
import com.gkzxhn.helpout.utils.ItemDecorationHelper
import kotlinx.android.synthetic.main.activity_money_list.*
import kotlinx.android.synthetic.main.default_top.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @classname：消息
 * @author：liushaoxiang
 * @date：2018/12/4 5:52 PM
 * @description：
 */

class NotificationActivity : BaseActivity() {
    var loadMore = false
    var page = 0
    lateinit var mAdapter: NotificationInfoAdapter
    override fun provideContentViewId(): Int {
        return R.layout.activity_notification_list
    }

    override fun init() {
        initTopTitle()
        mAdapter = NotificationInfoAdapter(this)
        val linearLayoutManager = LinearLayoutManager(this, 1, false)
        rcl_money_list.layoutManager = linearLayoutManager
        rcl_money_list.adapter = mAdapter
        val decoration = DisplayUtils.dp2px(App.mContext, 0f)
        rcl_money_list.addItemDecoration(ItemDecorationHelper(decoration, decoration, decoration, 0, decoration))
        getData("0")
        //加载更多
        loading_more.setOnLoadMoreListener(object : com.gkzxhn.helpout.customview.LoadMoreWrapper.OnLoadMoreListener {
            override fun onLoadMore() {
                if (loadMore) {
                    getData((page + 1).toString())
                } else {
                    offLoadMore()
                }
            }
        })

        //下啦刷新
        loading_refresh.setOnRefreshListener(object : PullToRefreshLayout.OnRefreshListener {
                override fun onRefresh() {
                    getData("0")
                    loading_refresh?.finishRefreshing()
                }
        }, 1)
    }

    private fun initTopTitle() {
        tv_default_top_title.text = "消息"
        iv_default_top_back.setOnClickListener {
            finish()
        }
    }

    fun getData(p: String) {
        RetrofitClientLogin.Companion.getInstance(this).mApi
                ?.getNotifications(p, "15")
                ?.subscribeOn(Schedulers.io())
                ?.unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<NotificationInfoList>(this) {
                    override fun success(t: NotificationInfoList) {
                        offLoadMore()
                        loadMore = !t.last
                        page = t.number
                        showNullView(t.content!!.isEmpty())
                        mAdapter.updateItems(t.first, t.content)
                    }

                    override fun onError(t: Throwable?) {
                        super.onError(t)
                        showNullView(true)
                        offLoadMore()
                    }
                })
    }


    fun offLoadMore() {
        //加载更多取消
        if (loading_more!!.isLoading) {
            loading_more?.finishLoading()
        }
    }

    fun showNullView(show: Boolean) {
        if (show) {
            tv_null.visibility = View.VISIBLE
        } else {
            tv_null.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.instance.post(RxBusBean.HomeTopRedPoint(false))
    }

}