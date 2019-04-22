package com.gkzxhn.helpout.model

import android.content.Context
import com.gkzxhn.helpout.entity.LivesCircle
import rx.Observable


/**
 * Explanation:
 * @author LSX
 *    -----2018/9/6
 */

interface ILivesCircleModel : IBaseModel {
    fun getLivesCircle(context: Context,page :String,size:String): Observable<LivesCircle>
}