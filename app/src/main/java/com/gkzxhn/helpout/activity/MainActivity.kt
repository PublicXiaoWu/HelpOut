package com.gkzxhn.helpout.activity

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.adapter.MainAdapter
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.LawyersInfo
import com.gkzxhn.helpout.entity.LivesCircleNew
import com.gkzxhn.helpout.entity.UpdateInfo
import com.gkzxhn.helpout.entity.rxbus.RxBusBean
import com.gkzxhn.helpout.fragment.ConversationFragment
import com.gkzxhn.helpout.fragment.FindFragment
import com.gkzxhn.helpout.fragment.HomeFragment
import com.gkzxhn.helpout.fragment.UserFragment
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.HttpObserverNoDialog
import com.gkzxhn.helpout.net.RetrofitClientChat
import com.gkzxhn.helpout.net.RetrofitClientPublic
import com.gkzxhn.helpout.net.error_exception.ApiException
import com.gkzxhn.helpout.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_ts.*
import retrofit2.adapter.rxjava.HttpException
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.net.ConnectException
import java.util.*
import kotlinx.android.synthetic.main.activity_main.tv_main_conversation as mainConversation
import kotlinx.android.synthetic.main.activity_main.tv_main_home as mainHome
import kotlinx.android.synthetic.main.activity_main.tv_main_my as mainMy
import kotlinx.android.synthetic.main.activity_main.vp_main as vpMain

/**
 * Explanation: 主页面
 * @author LSX
 *    -----2018/9/11
 */
class MainActivity : BaseActivity() {

    var tbList: MutableList<Fragment>? = null
    private var mainAdapter: MainAdapter? = null
    var lawyersInfo: LawyersInfo? = null


    override fun provideContentViewId(): Int {
        return R.layout.activity_main
    }

    override fun init() {

        tbList = ArrayList()

        tbList?.add(HomeFragment())
        tbList?.add(ConversationFragment())
        tbList?.add(FindFragment())
        tbList?.add(UserFragment())
        mainAdapter = MainAdapter(supportFragmentManager, tbList)
        vpMain.adapter = mainAdapter
        vpMain.offscreenPageLimit = 3

        updateApp()

        findLifeCircle()
    }

    private fun findLifeCircle() {
        getLivesCircleNew()

        /****** 收到 已经加载过生活圈了 通知发现页面刷新新的未读信息 ******/
        val rxSubscription = RxBus.instance.toObserverable(RxBusBean.ChangeFindUnRead::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    getLivesCircleNew()
                }, {
                    it.message.toString().logE(this)
                })
        mCompositeSubscription.add(rxSubscription)
    }

    /**
     * Explanation: 首页的点击方法
     * @author LSX
     *    -----2018/9/11
     */
    fun onClickGoHome(view: View) {
        vpMain.currentItem = 0
        mainHome.setDrawable(resources.getDrawable(R.mipmap.ic_home_purple))
        mainConversation.setDrawable(resources.getDrawable(R.mipmap.ic_conversation_black))
        tv_main_lives_circle.setDrawable(resources.getDrawable(R.mipmap.ic_lives_circle_selelct_no))
        mainMy.setDrawable(resources.getDrawable(R.mipmap.ic_my_black))

        mainHome.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_purple))
        mainConversation.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_black))
        tv_main_lives_circle.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_black))
        mainMy.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_black))

    }

    /**
     * Explanation: 会话页面的点击方法
     * @author LSX
     *    -----2018/9/11
     */
    fun onClickConversation(view: View) {
        vpMain.currentItem = 1
        mainHome.setDrawable(resources.getDrawable(R.mipmap.ic_home_black))
        mainConversation.setDrawable(resources.getDrawable(R.mipmap.ic_conversation_purple))
        tv_main_lives_circle.setDrawable(resources.getDrawable(R.mipmap.ic_lives_circle_selelct_no))
        mainMy.setDrawable(resources.getDrawable(R.mipmap.ic_my_black))

        mainHome.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_black))
        mainConversation.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_purple))
        tv_main_lives_circle.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_black))
        mainMy.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_black))

    }

    /**
     * Explanation: 发现的点击方法
     * @author LSX
     *    -----2018/9/11
     */
    fun onClickLives(view: View) {
        vpMain.currentItem = 2
        mainHome.setDrawable(resources.getDrawable(R.mipmap.ic_home_black))
        mainConversation.setDrawable(resources.getDrawable(R.mipmap.ic_conversation_black))
        tv_main_lives_circle.setDrawable(resources.getDrawable(R.mipmap.ic_lives_circle_selelct))
        mainMy.setDrawable(resources.getDrawable(R.mipmap.ic_my_black))

        mainHome.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_black))
        mainConversation.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_black))
        tv_main_lives_circle.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_purple))
        mainMy.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_black))

        getLivesCircleNew()
    }

    /**
     * Explanation: 我的页面的点击方法
     * @author LSX
     *    -----2018/9/11
     */
    fun onClickGoUser(view: View) {
        vpMain.currentItem = 3
        mainHome.setDrawable(ContextCompat.getDrawable(this,R.mipmap.ic_home_black)!!)
        mainConversation.setDrawable(ContextCompat.getDrawable(this,R.mipmap.ic_conversation_black)!!)
        tv_main_lives_circle.setDrawable(ContextCompat.getDrawable(this,R.mipmap.ic_lives_circle_selelct_no)!!)
        mainMy.setDrawable(ContextCompat.getDrawable(this,R.mipmap.ic_my_purple)!!)
        mainHome.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_black))
        mainConversation.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_black))
        tv_main_lives_circle.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_black))
        mainMy.setTextColor(ContextCompat.getColor(this,R.color.main_bottom_purple))
    }


    /**
     * @methodName： created by liushaoxiang on 2018/11/6 4:09 PM.
     * @description：检查更新
     */
    private fun updateApp() {
        mCompositeSubscription.add(RetrofitClientPublic.getInstance(this).mApi?.updateApp()
                .subscribeOn(rx.schedulers.Schedulers.io())
                ?.unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<UpdateInfo>(this) {
                    override fun success(t: UpdateInfo) {
                        val versionCode = ObtainVersion.getVersionCode(App.mContext)
                        if (t.number!! > versionCode) {
                            showDownloadDialog(t)
                        }
                    }

                    override fun onError(e: Throwable?) {
                        loadDialog?.dismiss()
                        when (e) {
                            is ConnectException -> TsDialog("服务器异常，请重试", false)
                            is HttpException -> {
                                when {
                                    e.code() == 401 -> TsClickDialog("登录已过期", false).dialog_save.setOnClickListener {
                                        App.EDIT.putString(Constants.SP_TOKEN, "")?.commit()
                                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        startActivity(intent)
                                    }
                                    e.code() == 404 -> {
                                        /****** 不处理 ******/
                                    }
                                    else -> TsDialog("服务器异常，请重试", false)
                                }
                            }
                            is IOException -> showToast("网络连接超时，请重试")

                            //后台返回的message
                            is ApiException -> {
                                TsDialog(e.message!!, false)
                                Log.e("ApiErrorHelper", e.message, e)
                            }
                            else -> {
                                showToast("数据异常")
                                Log.e("ApiErrorHelper", e?.message, e)
                            }
                        }
                    }

                }))
    }


    override fun onResume() {
        super.onResume()
        /****** 刷新订单数据 ******/
        RxBus.instance.post(RxBusBean.RefreshOrder(true))

    }


    /**
     * @methodName： created by liushaoxiang on 2019/4/25 9:46 AM.
     * @description：获取最新未看生活圈
     */
    fun getLivesCircleNew() {
        mCompositeSubscription.add(
            RetrofitClientChat
                    .getInstance(this).mApi.getLivesCircleNew()
                    .subscribeOn(Schedulers.io())
                    ?.unsubscribeOn(AndroidSchedulers.mainThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(object : HttpObserverNoDialog<LivesCircleNew>(this) {
                        override fun success(t: LivesCircleNew) {
                            RxBus.instance.post(t)
                            if (!t.username.isNullOrEmpty()) {
                                view_red_point.visibility = View.VISIBLE
                            } else {
                                view_red_point.visibility = View.GONE
                            }
                        }
                    })
        )
    }
}
