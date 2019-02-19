package com.gkzxhn.helpout.net


 /**
 * Explanation:
 * @author LSX
 *    -----2018/9/6
 */

interface OnRequestListener<in T> {
    fun success(t : T)
}