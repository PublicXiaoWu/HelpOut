package com.gkzxhn.helpout.activity

import com.gkzxhn.helpout.R
import kotlinx.android.synthetic.main.activity_lives_circle_details.*
import kotlinx.android.synthetic.main.default_top.*

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/4/16 10:13 AM
 * @description：生活圈详情
 */
class LivesCircleDetailsActivity : BaseActivity() {

    override fun provideContentViewId(): Int {
        return R.layout.activity_lives_circle_details
    }

    override fun init() {
        initTitle()


        val list = ArrayList<String>()
        for (number in 1..4) {
            when (number) {
                1 -> list.add("http://img2.imgtn.bdimg.com/it/u=1782007428,3628702860&fm=27&gp=0.jpg")
                2 -> list.add("http://img0.imgtn.bdimg.com/it/u=2473592401,1264512939&fm=27&gp=0.jpg")
                3 -> list.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3176289000,2370082435&fm=27&gp=0.jpg")
                else -> list.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4071270602,4187261958&fm=27&gp=0.jpg")
            }

        }

        ll_lives_circle_image.setIsShowAll(false)
        ll_lives_circle_image.setUrlList(list)

    }


    private fun initTitle() {
        tv_default_top_title.text = "生活圈"
        iv_default_top_back.setOnClickListener { finish() }
    }
}