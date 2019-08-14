package com.gkzxhn.helpout.fragment

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.widget.TextView
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.AddFriendActivity
import com.gkzxhn.helpout.extensions.dp2px
import com.gkzxhn.helpout.view.ConversationView
import kotlinx.android.synthetic.main.conversation_fragment.*
import kotlin.math.roundToInt

/**
 * Explanation: 网易会话列表页面
 * @author LSX
 *    -----2018/9/7
 */

class ConversationFragment : BaseFragment(), ConversationView {

    override fun init() {
    }

    override fun initListener() {
        tv_user_title.setOnClickListener {
            //            NimUIKit.startP2PSession(mContext, "9f9469948f76456d850b9f3bed1ddc10")
        }

        iv_conversation_menu.setOnClickListener {
            showPop()
        }
    }

    private val mPopupWindow = PopupWindow()

    private fun showPop() {
        // 设置布局文件
        val contentView = LayoutInflater.from(context).inflate(R.layout.pop_window_msg_add, null)
        mPopupWindow.contentView = contentView
        contentView.findViewById<TextView>(R.id.tv_add_friend).setOnClickListener {
            context?.startActivity(Intent(context, AddFriendActivity::class.java))
            if (mPopupWindow.isShowing) {
                mPopupWindow.dismiss()
            }
        }
        // 为了避免部分机型不显示，我们需要重新设置一下宽高
        mPopupWindow.width = 114f.dp2px().toInt()
        mPopupWindow.height = 49f.dp2px().toInt()
        // 设置pop透明效果
        mPopupWindow.setBackgroundDrawable(ColorDrawable(0x0000))
        // 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
        mPopupWindow.isFocusable = true
        // 设置pop可点击，为false点击事件无效，默认为true
        mPopupWindow.isTouchable = true
        // 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
        mPopupWindow.isOutsideTouchable = true
        // 相对于 + 号正下面，同时可以设置偏移量
        mPopupWindow.showAsDropDown(iv_conversation_menu, -85f.dp2px().roundToInt(), 0)
    }

    override fun provideContentViewId(): Int {
        return R.layout.conversation_fragment
    }

}
