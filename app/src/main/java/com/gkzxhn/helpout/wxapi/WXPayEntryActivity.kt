package com.gkzxhn.helpout.wxapi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.activity.BaseActivity
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.modelpay.PayResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.entry.*

class WXPayEntryActivity : BaseActivity(), IWXAPIEventHandler {

    // IWXAPI �ǵ�����app��΢��ͨ�ŵ�openapi�ӿ�
    private var api: IWXAPI? = null

    private var imageRes: Int = 0
    private var statusRes: Int = 0
    private var textColorRes: Int = 0

    private var isSuccess: Boolean = false
    private var code = 1    // 1 表示 微信  else 表示支付宝

    private var payCode: Int = 1
    private var payTypeRes: Int = R.string.weixin
    private var money: String? = ""
    private var prisonerName: String? = ""

    private var appId: String = ""

    override fun provideContentViewId(): Int {
        return R.layout.entry
    }

    override fun init() {
//        code = intent.getIntExtra(IntentConstants.PAY_CODE, 1)
//        if (code != 1) {
//            //表示支付宝
//            payCode = code
//            payTypeRes = R.string.alipay
//            money = intent.getStringExtra(IntentConstants.MONEY)?.let { it }
//            prisonerName = intent.getStringExtra(IntentConstants.NAME)
//        }
        // ͨ��WXAPIFactory��������ȡIWXAPI��ʵ��
        api = WXAPIFactory.createWXAPI(this, appId)
        try {
            api?.handleIntent(intent, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        remitPayEvent()
        initContent()
        setOnClick()
    }

    @SuppressLint("SetTextI18n")
    private fun initContent() {
        when (payCode) {
            BaseResp.ErrCode.ERR_OK -> {
                //支付成功
                imageRes = R.mipmap.pay_success
                statusRes = R.string.pay_success
                textColorRes = R.color.dark_blue
            }
            BaseResp.ErrCode.ERR_USER_CANCEL -> {
                //用户取消支付
                imageRes = R.mipmap.pay_failure
                statusRes = R.string.pay_canceled
                textColorRes = R.color.red_3a
            }
            else -> {
                //支付失败
                imageRes = R.mipmap.pay_failure
                statusRes = R.string.pay_failure
                textColorRes = R.color.red_3a
            }
        }
        iv_pay_status.setImageResource(imageRes)
        tv_pay_status.setText(statusRes)
        tv_pay_status.setTextColor(resources.getColor(textColorRes))
        tv_pay_type.setText(payTypeRes)
        tv_pay_money.text = "¥$money"
        if (TextUtils.isEmpty(prisonerName)) {
            fl_prisoner_name.visibility = View.GONE
        } else {
            tv_pay_prisoner_name.text = prisonerName
        }
    }

    private fun setOnClick() {
        tv_down!!.setOnClickListener {
//            val payStatus = PayStatus()
//            payStatus.payType = if (code == 1) 2 else 1
//            when (payCode) {
//                BaseResp.ErrCode.ERR_OK -> {
//                    payStatus.payStatus = 1
//                }
//                BaseResp.ErrCode.ERR_USER_CANCEL -> {
//                    payStatus.payStatus = 2
//                }
//                else -> {
//                    payStatus.payStatus = 0
//                }
//            }
//            RxBus.instance.post(payStatus)
//            close()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        api!!.handleIntent(intent, this)
    }


    // ΢�ŷ������󵽵�����Ӧ��ʱ����ص����÷���
    override fun onReq(req: BaseReq) {
        if (req is PayReq) {
            appId = req.appId
        }
        when (req.type) {
            ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX -> {
            }
            ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX -> {
            }
            else -> {
            }
        }
    }

    // ������Ӧ�÷��͵�΢�ŵ�����������Ӧ�������ص����÷���
    override fun onResp(resp: BaseResp) {
        //        Toast.makeText(this, "baseresp.getType = " + resp.getType(), Toast.LENGTH_SHORT).show();

        payCode = resp.errCode
        if (resp is PayResp && resp.extData != null) {
            val datas = resp.extData.split("&")
            try {
                money = datas[0]
                prisonerName = datas[1]
            } catch (e: Exception) {
            }
        }
    }

    companion object {

        private val TIMELINE_SUPPORTED_VERSION = 0x21020001

        fun launch(context: Context) {
            context.startActivity(Intent(context, WXPayEntryActivity::class.java))
        }
    }

    fun remitPayEvent() {

    }

    /*private void goToGetMsg() {
		Intent intent = new Intent(this, GetFromWXActivity.class);
		intent.putExtras(getIntent());
		startActivity(intent);
		finish();
	}
	
	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
		WXMediaMessage wxMsg = showReq.message;
		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
		
		StringBuffer msg = new StringBuffer(); // ��֯һ������ʾ����Ϣ����
		msg.append("description: ");
		msg.append(wxMsg.description);
		msg.append("\n");
		msg.append("extInfo: ");
		msg.append(obj.extInfo);
		msg.append("\n");
		msg.append("filePath: ");
		msg.append(obj.filePath);
		
		Intent intent = new Intent(this, ShowFromWXActivity.class);
		intent.putExtra(Constants.ShowMsgActivity.STitle, wxMsg.title);
		intent.putExtra(Constants.ShowMsgActivity.SMessage, msg.toString());
		intent.putExtra(Constants.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
		startActivity(intent);
		finish();
	}*/
}