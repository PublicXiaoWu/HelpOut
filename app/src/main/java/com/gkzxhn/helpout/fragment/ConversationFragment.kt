package com.gkzxhn.helpout.fragment

import android.content.Intent
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.AddFriendActivity
import com.gkzxhn.helpout.view.ConversationView
import kotlinx.android.synthetic.main.conversation_fragment.*


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
            context?.startActivity(Intent(context,AddFriendActivity::class.java))
        }


    }

    override fun provideContentViewId(): Int {
        return R.layout.conversation_fragment
    }

}
