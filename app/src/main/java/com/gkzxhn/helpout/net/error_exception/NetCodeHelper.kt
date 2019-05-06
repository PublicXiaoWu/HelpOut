package com.gkzxhn.helpout.net.error_exception

import android.content.Context
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.utils.TsDialog
import org.json.JSONObject

/**
 * @classname：NetCodeHelper
 * @author：liushaoxiang
 * @date：2019/5/5 4:54 PM
 * @description：
 */
object NetCodeHelper {

    fun handleCommonCode(context: Context, errorBody: String): String {
        /****** 预留需要单独处理的code返回 ******/
        var separateCode=""

        val code = try {
            JSONObject(errorBody).getString("code")
        } catch (e: Exception) {
            ""
        }
        val message = try {
            JSONObject(errorBody).getString("message")
        } catch (e: Exception) {
            ""
        }
        when (code) {
            "user.SmsVerificationCodeNotMatched" -> {
                context.TsDialog(context.getString(R.string.verify_number_error).toString(), false)
            }
            "user.PhoneNumberExisted" -> {
                separateCode= "user.PhoneNumberExisted"
            }
            "user.password.NotMatched" -> {
                context.TsDialog(context.getString(R.string.password_error).toString(), false)
            }
            "user.GroupNotMatched" -> {
                context.TsDialog(context.getString(R.string.group_error).toString(), false)
            }
            "user.Disabled" -> {
                context.TsDialog(context.getString(R.string.group_error_disable).toString(), false)
            }
            "user.group.NotMatched" -> {
                context.TsDialog(context.getString(R.string.group_error).toString(), false)
            }
            else -> {
                if (message.isEmpty()) {
                    context.TsDialog("服务异常", false)
                } else {
                    context.TsDialog(message, false)
                }
            }
        }
        return separateCode
    }
}
