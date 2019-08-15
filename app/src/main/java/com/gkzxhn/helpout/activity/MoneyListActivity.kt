package com.gkzxhn.helpout.activity

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.adapter.MoneyListAdapter
import com.gkzxhn.helpout.customview.LoadMoreWrapper
import com.gkzxhn.helpout.customview.PullToRefreshLayout
import com.gkzxhn.helpout.entity.MoneyList
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.RetrofitClient
import kotlinx.android.synthetic.main.activity_money_list.*
import kotlinx.android.synthetic.main.default_top.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @classname：AllOrderActivity
 * @author：liushaoxiang
 * @date：2018/10/12 11:55 AM
 * @description：账单
 */

class MoneyListActivity : BaseActivity() {

    lateinit var mAdapter: MoneyListAdapter

    var loadMore = false
    var page = 0

    override fun provideContentViewId(): Int {
        return R.layout.activity_money_list
    }

    override fun init() {
        initTopTitle()
        mAdapter = MoneyListAdapter(this)
        rcl_money_list.layoutManager = LinearLayoutManager(this, 1, false)
        rcl_money_list.adapter = mAdapter
        getMoneyList("0")


        //加载更多
        loading_more.setOnLoadMoreListener(object : LoadMoreWrapper.OnLoadMoreListener {
            override fun onLoadMore() {
                if (loadMore) {
                    getMoneyList((page + 1).toString())
                } else {
                    offLoadMore()
                }
            }
        })

        //下啦刷新
        loading_refresh.setOnRefreshListener(object : PullToRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                getMoneyList("0")
                loading_refresh?.finishRefreshing()
            }
        }, 1)
    }

    private fun initTopTitle() {
        tv_default_top_title.text = "账单"
        iv_default_top_back.setOnClickListener {
            finish()
        }
    }

    fun getMoneyList(p: String) {
        RetrofitClient.Companion.getInstance(this).mApi
                .getTransaction(p, "10")
                .subscribeOn(Schedulers.io())
                ?.unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<MoneyList>(this) {
                    override fun success(t: MoneyList) {
                        offLoadMore()
                        loadMore = !t.last
                        page = t.number
                        showNullView(t.content!!.isEmpty())
                        mAdapter.updateItems(t.first, t.content)
                    }

                    override fun onError(t: Throwable?) {
                        super.onError(t)
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
}