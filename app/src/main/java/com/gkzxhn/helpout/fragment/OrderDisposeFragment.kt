package com.gkzxhn.helpout.fragment

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.CustomerOrderDetailActivity
import com.gkzxhn.helpout.activity.OrderActivity
import com.gkzxhn.helpout.adapter.OrderDisposeAdapter
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.customview.PullToRefreshLayout
import com.gkzxhn.helpout.entity.OrderDispose
import com.gkzxhn.helpout.entity.RxBusBean
import com.gkzxhn.helpout.presenter.OrderDisposePresenter
import com.gkzxhn.helpout.utils.DisplayUtils
import com.gkzxhn.helpout.utils.ItemDecorationHelper
import com.gkzxhn.helpout.utils.ProjectUtils
import com.gkzxhn.helpout.utils.logE
import com.gkzxhn.helpout.view.OrderDisposeView
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import kotlinx.android.synthetic.main.order_disposer_fragment.*
import rx.android.schedulers.AndroidSchedulers

/**
 * Explanation：我的咨询
 * @author LSX
 * Created on 2018/9/10.
 */

class OrderDisposeFragment : BaseFragment(), OrderDisposeView {

    private var mAdapter: OrderDisposeAdapter? = null

    var mPresenter: OrderDisposePresenter? = null

    var loadMore = false
    var page = 0

    override fun setLastPage(lastPage: Boolean, page: Int) {
        this.loadMore = !lastPage
        this.page = page
    }

    override fun provideContentViewId(): Int {
        return R.layout.order_disposer_fragment
    }

    override fun init() {
        mPresenter = OrderDisposePresenter(context!!, this)

        initRecyclerView()

        /****** 收到接单成功的消息 ******/
        RxBus.instance.toObserverable(RxBusBean.HomePoint::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mPresenter?.getOrderDispose("0", mCompositeSubscription)
                }, {
                    it.message.toString().logE(this)
                })

        /****** 收到抢单成功的消息 ******/
        RxBus.instance.toObserverable(RxBusBean.AcceptOrder::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mPresenter?.getOrderDispose("0", mCompositeSubscription)

                }, {
                    it.message.toString().logE(this)
                })

        /******  刷新订单数据 ******/
        RxBus.instance.toObserverable(RxBusBean.RefreshOrder::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mPresenter?.getOrderDispose("0", mCompositeSubscription)
                }, {
                    it.message.toString().logE(this)
                })

    }

    private fun initRecyclerView() {
        if (mAdapter == null) {
            mAdapter = context?.let { OrderDisposeAdapter(it, null) }
            rcl_order_disposer.layoutManager = LinearLayoutManager(activity, 1, false)
            rcl_order_disposer.adapter = mAdapter
            val decoration = DisplayUtils.dp2px(App.mContext, 15f)
            rcl_order_disposer.addItemDecoration(ItemDecorationHelper(0, decoration, 0, 0, decoration))
            mPresenter?.getOrderDispose("0", mCompositeSubscription)
        }
        recyclerViewListener()

    }

    override fun initListener() {
        //加载更多
        loading_more.setOnLoadMoreListener(object : com.gkzxhn.helpout.customview.LoadMoreWrapper.OnLoadMoreListener {
            override fun onLoadMore() {
                if (loadMore) {
                    mPresenter?.getOrderDispose((page + 1).toString(), mCompositeSubscription)
                } else {
                    offLoadMore()
                }
            }
        })

        //下啦刷新
        loading_refresh.setOnRefreshListener(object : PullToRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                mPresenter?.getOrderDispose("0", mCompositeSubscription)
                loading_refresh?.finishRefreshing()
            }
        }, 1)
        recyclerViewListener()

    }

    fun recyclerViewListener() {
        mAdapter?.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
            override fun onItemLongClick(view: View?, holder: RecyclerView.ViewHolder?, position: Int): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemClick(view: View?, holder: RecyclerView.ViewHolder?, position: Int) {
                if (ProjectUtils.certificationStatus()) {
                    val intent = Intent(context, OrderActivity::class.java)
                    val data = mAdapter!!.getCurrentItem()
                    intent.putExtra("orderId", data.id)
                    intent.putExtra("orderState", 2)
                    startActivity(intent)
                } else {
                    activity?.let { mAdapter!!.getCurrentItem().id?.let { it1 -> CustomerOrderDetailActivity.launch(it, it1) } }
                }
            }
        })

        mAdapter?.setOnItemOrderListener(object : OrderDisposeAdapter.ItemOrderListener {
            override fun onRefusedListener() {
                val data = mAdapter!!.getCurrentItem()
                var username: String? = null
                username = if (ProjectUtils.certificationStatus()) {
                    data.customer?.username
                } else {
                    data.lawyer?.username
                }
                data.id?.let { username?.let { it1 -> mPresenter?.getVideoDuration(it, it1,mCompositeSubscription) } }
            }

        })
    }

    override fun offLoadMore() {
        //加载更多取消
        if (loading_more.isLoading) {
            loading_more?.finishLoading()
        }
    }

    override fun updateData(clear: Boolean, data: List<OrderDispose.ContentBean>?) {
        mAdapter?.updateItems(clear, data)
    }

    override fun showNullView(show: Boolean, string: String) {
        if (show) {
            tv_order_disposer_null.text = string
            tv_order_disposer_null.visibility = View.VISIBLE
        } else {
            tv_order_disposer_null.visibility = View.GONE
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            mPresenter?.getOrderDispose("0", mCompositeSubscription)
        }
    }

}