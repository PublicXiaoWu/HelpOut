package com.gkzxhn.helpout.common

/**
 * @classname：Constants
 * @author：liushaoxiang
 * @date：2018/10/11 10:54 AM
 * @description：
 */

object Constants {

    /****** 页面跳转相关 ******/
    val ORDER_GET_STATE = "getOrderState"

    //    专业领域选择 页面间传值
    val RESULT_CHOOSE_MAJORS = "Result_Choose_majors"
    val INTENT_SELECTSTRING = "INTENT_SELECTSTRING"
    val REQUESTCODE_CHOOSE_MAJORS = 0x111
    val RESULTCODE_CHOOSE_MAJORS = 0x112
    /****** 昵称返回 ******/
    val RESULTCODE_EDIT_NICK = 0x113
    //地址填写返回值
    val RESULT_EDIT_ADDRESS = "RESULT_eidt_address"
    val RESULTCODE_EDIT_ADDRESS = 23
    val RESULTCODE_EDIT_NICKNAME = 24
    /****** SharedPreferences 相关******/

//    存储项目验证Token的状态
    val SP_TOKEN = "SP_Token"
    val SP_REFRESH_TOKEN = "refreshToken"
    val SP_PHONE = "SP_PHONE"
    val SP_NAME = "sp_user_name"
    val SP_REMEMBER_PHONE = "SP_remember_phone"
    val SP_LAWOFFICE = "sp_user_lawOffice"
    val SP_IM_ACCOUNT = "SP_IM_ACCOUNT"
    val SP_IM_TOKEN = "SP_IM_TOKEN"
    /****** 头像和昵称已完成上传 ******/
    val SP_ACCOUNT_COMPLETE = "SP_ACCOUNT_COMPLETE"

    /******  自己头像的变更时间  ******/
    val SP_MY_ICON = "SP_MY_ICON"

    /**
     *PENDING_CERTIFIED("待认证"),
    PENDING_APPROVAL("待审核"),
    APPROVAL_FAILURE("审核失败"),
    CERTIFIED("已认证");
     */
    val SP_CERTIFICATIONSTATUS = "SP_CERTIFICATIONSTATUS"

    /**
     * 头像裁剪URI
     */
    val INTENT_CROP_IMAGE_URI = "intent_crop_image_uri"

    /**
     * 裁剪过后的绝对路径
     */
    val CROP_PATH = "crop_path"
    /****** 字符常量 ******/

    /****** 待认证 ******/
    val PENDING_CERTIFIED = "PENDING_CERTIFIED"
    /****** 待审核 ******/
    val PENDING_APPROVAL = "PENDING_APPROVAL"
    /****** 审核失败 ******/
    val APPROVAL_FAILURE = "APPROVAL_FAILURE"
    /****** 已认证 ******/
    val CERTIFIED = "CERTIFIED"

    // ("待付款")
    val ORDER_STATE_PENDING_PAYMENT = "PENDING_PAYMENT"
    // ("待审核")
    val ORDER_STATE_PENDING_APPROVAL = "PENDING_APPROVAL"
    // ("待接单")
    val ORDER_STATE_PENDING_RECEIVING = "PENDING_ACCEPT"
    // ("已接单")
    val ORDER_STATE_ACCEPTED = "ACCEPTED"
    // ("处理中")
    val ORDER_STATE_PROCESSING = "PROCESSING"
    // ("已完成")
    val ORDER_STATE_COMPLETE = "COMPLETE"
    // ("已关闭")
    val ORDER_STATE_REFUSED = "CLOSED"
    // ("已取消")
    val ORDER_STATE_CANCELLED = "CANCELLED"

    //PENDING AUTHORIZED PAID REFUNDED VOIDED
    //待支付
    val PAYMENT_STATE_PENDING = "PENDING"
    //已授权
    val PAYMENT_STATE_AUTHORIZED = "AUTHORIZED"
    //已支付
    val PAYMENT_STATE_PAID = "PAID"
    //已退款
    val PAYMENT_STATE_REFUNDED = "REFUNDED"
    //已取消
    val PAYMENT_STATE_VOIDED = "VOIDED"

    /****** 与后台约定的前缀 ******/
    val BASE_64_START = "data:image/png;base64,"
    //    val APK_ADRESS="LegalConsulting.apk"
    val APK_ADRESS = "legal.apk"

    val RESULT_EDIT_ADDRESS_PROVINCECODE = "RESULT_EDIT_ADDRESS_PROVINCECODE"
    val RESULT_EDIT_ADDRESS_PROVINCENAME = "RESULT_EDIT_ADDRESS_PROVINCENAME"
    val RESULT_EDIT_ADDRESS_CITYNAME = "RESULT_EDIT_ADDRESS_CITYNAME"
    val RESULT_EDIT_ADDRESS_CITYCODE = "RESULT_EDIT_ADDRESS_CITYCODE"
    val RESULT_EDIT_ADDRESS_COUNTYCODE = "RESULT_EDIT_ADDRESS_COUNTYCODE"
    val RESULT_EDIT_ADDRESS_COUNTYNAME = "RESULT_EDIT_ADDRESS_COUNTYNAME"

    /****** 昵称返回 ******/
    val RESULT_EDIT_NICKNAME = "RESULT_EDIT_NICKNAME"

    //微信APP_ID
    val WX_APPID = "wx21aed62551567801"

    /****** 正在处理中的订单号 ******/
    val PROCESSING_ORDER_ID="PROCESSING_ORDER_ID"
}