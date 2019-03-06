package com.gkzxhn.helpout.activity

import android.content.Intent
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.RetrofitClientPublic
import com.gkzxhn.helpout.utils.showToast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_edit_nikename.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Response
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * @classname：昵称修改
 * @author：liushaoxiang
 * @date：2019/2/27 10:14 AM
 * @description：
 */

class EditNicknameActivity : BaseActivity() {

    override fun provideContentViewId(): Int {
        return R.layout.activity_edit_nikename
    }


    override fun init() {
        tv_edit_save.setOnClickListener {
            val nickname = et_edit_nickname.text.trim().toString()
            if (nickname.isNotEmpty()) {
                modifyNickname(nickname)

            } else {
                showToast("还未填写完成！")
            }
        }
        iv_edit_nickname_back.setOnClickListener { onBackPressed() }


    }


    /**
     * @methodName： created by liushaoxiang on 2019/2/22 2:51 PM.
     * @description：修改昵称
     */
    private fun modifyNickname(nickname: String) {
        var map = LinkedHashMap<String, String>()
        map["phoneNumber"] = intent.getStringExtra("phoneNumber")
        map["nickname"] = nickname
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                Gson().toJson(map))

        RetrofitClientPublic.getInstance(this).mApi?.modifyAccountInfo(body)
                ?.subscribeOn(Schedulers.io())
                ?.unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<Response<Void>>(this) {
                    override fun success(t: Response<Void>) {
                        if (t.code() == 204) {
                            val intent = Intent()
                            intent.putExtra(Constants.RESULT_EDIT_NICKNAME, nickname)
                            setResult(Constants.RESULTCODE_EDIT_NICKNAME, intent)
                            finish()
                        } else {
                            showToast("修改昵称失败")
                        }
                    }
                })

    }

}
