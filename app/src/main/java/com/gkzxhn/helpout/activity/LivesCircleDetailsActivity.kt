package com.gkzxhn.helpout.activity

import android.app.Activity
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.adapter.LivesCircleCommentAdapter
import com.gkzxhn.helpout.common.IntentConstants
import com.gkzxhn.helpout.customview.PullToRefreshLayout
import com.gkzxhn.helpout.customview.RecyclerSpace
import com.gkzxhn.helpout.entity.LivesCircle
import com.gkzxhn.helpout.entity.LivesCircleDetails
import com.gkzxhn.helpout.extensions.dp2px
import com.gkzxhn.helpout.presenter.LivesCirclePresenter
import com.gkzxhn.helpout.utils.SoftKeyBoardListener
import com.gkzxhn.helpout.utils.SystemUtil.hideKeyBoard
import com.gkzxhn.helpout.utils.SystemUtil.showKeyBoard
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.LivesCircleView
import kotlinx.android.synthetic.main.activity_lives_circle_details.*
import kotlinx.android.synthetic.main.default_top.*


/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/16 10:13 AM
 * @description：生活圈详情
 */
class LivesCircleDetailsActivity : BaseActivity(), LivesCircleView {

    override fun updateData(data: List<LivesCircle.ContentBean>, isFirst: Boolean) {
    }

    override fun endLoadMore() {
    }

    override fun finishActivity() {
        finish()
    }

    override fun offLoadMore() {
    }


    override fun setLastPage(lastPage: Boolean, page: Int) {
    }

    override fun praiseSuccess(position: Int) {
        mPresenter.getLivesCircleDetails(livesCircleId)
    }

    lateinit var livesCircleId: String
    var position: Int=0
    var praisesCircleoffriends = false
    var mLivesCircleDetails: LivesCircleDetails?=null
    lateinit var mAdapter: LivesCircleCommentAdapter
    lateinit var mPresenter: LivesCirclePresenter


    /**
     * 软键盘弹出收起监听
     */
    private val onSoftKeyBoardChangeListener = object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
        override fun keyBoardShow(height: Int) {
        }

        override fun keyBoardHide(height: Int) {
            onBackPressed()
        }
    }


    override fun provideContentViewId(): Int {
        return R.layout.activity_lives_circle_details
    }

    override fun init() {
        initTitle()
        mPresenter = LivesCirclePresenter(this, this)
        initRecyclerView()
        livesCircleId = intent.getStringExtra("LivesCircleId")
        position = intent.getIntExtra("position",0)

        /****** 注册软键盘监听 ******/
        SoftKeyBoardListener.setListener(this, onSoftKeyBoardChangeListener)
        mPresenter.getLivesCircleDetails(livesCircleId)

        /****** 评论 ******/
        v_lives_circle_comment.setOnClickListener {
            v_lives_circle_bottom_edit.visibility = View.VISIBLE
            et_lives_circle_bottom_comment.visibility = View.VISIBLE
            /****** 弹出键盘 ******/
            showKeyBoard(this, et_lives_circle_bottom_comment)
        }
        /****** 分享 ******/
        v_lives_circle_share.setOnClickListener {
            showToast("敬请期待")
        }
        /****** 点赞 ******/
        v_lives_circle_like.setOnClickListener {
            if (praisesCircleoffriends) {
                showToast(getString(R.string.liked))
            } else {
                mPresenter.praise(livesCircleId, 0)
            }
        }

        //下啦刷新
        loading_refresh.setOnRefreshListener(object : PullToRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                mPresenter.getLivesCircleDetails(livesCircleId)
                loading_refresh?.finishRefreshing()
            }
        }, 1)

        /****** 将回车键设置成发送 ******/
        et_lives_circle_bottom_comment.imeOptions = EditorInfo.IME_ACTION_SEND;
        /****** 回车监听 ******/
        et_lives_circle_bottom_comment.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER&& event.action == KeyEvent.ACTION_DOWN) {
                val content = et_lives_circle_bottom_comment.text.toString().trim()
                if (!content.isEmpty()) {
                    mPresenter.comment(content, livesCircleId)
                    hideKeyBoard(this, et_lives_circle_bottom_comment)
                } else {
                    showToast("评论不能为空")
                }
                true;
            } else
                false
        }
    }

    private fun initTitle() {
        tv_default_top_title.text = "生活圈"
        iv_default_top_back.setOnClickListener {

            onBackPressed()

        }
    }


    override fun loadLivesCircleDetailsUI(t: LivesCircleDetails) {
        praisesCircleoffriends = t.praisesCircleoffriends
        mLivesCircleDetails=t
        tv_lives_circle_comment_number.text = t.commentNum.toString()
        tv_lives_circle_like_number.text = t.praiseNum.toString()

        if (t.praisesCircleoffriends) {
            iv_lives_circle_like_number.setImageResource(R.mipmap.ic_lives_liked)
        } else {
            iv_lives_circle_like_number.setImageResource(R.mipmap.ic_lives_like)
        }

        mAdapter.updateItems(t)

    }

    private fun initRecyclerView() {
        rcv_lives_circle_details.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = LivesCircleCommentAdapter(this)
        mAdapter.setHasStableIds(true)
        rcv_lives_circle_details.addItemDecoration(RecyclerSpace(1.2f.dp2px().toInt(), ContextCompat.getColor(this, R.color.gray_line)))
        rcv_lives_circle_details.adapter = mAdapter

    }

    override fun onBackPressed() {
        if (v_lives_circle_bottom_edit.visibility == View.VISIBLE) {
            v_lives_circle_bottom_edit.visibility = View.GONE
            et_lives_circle_bottom_comment.visibility = View.GONE
        } else {
            val data = Intent()
            data.putExtra(IntentConstants.position, position)
            data.putExtra(IntentConstants.praiseNum, mLivesCircleDetails?.praiseNum)
            data.putExtra(IntentConstants.commentNum,mLivesCircleDetails?.commentNum)
            data.putExtra(IntentConstants.praisesCircleoffriends,mLivesCircleDetails?.praisesCircleoffriends)
            setResult(Activity.RESULT_OK, data)
            super.onBackPressed()


        }


    }

}