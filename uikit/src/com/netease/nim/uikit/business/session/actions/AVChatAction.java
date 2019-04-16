package com.netease.nim.uikit.business.session.actions;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.netease.nim.avchatkit.AVChatKit;
import com.netease.nim.avchatkit.activity.AVChatActivity;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.http.NimHttpClient;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzxuwen on 2015/6/12.
 */
public class AVChatAction extends BaseAction {
    private AVChatType avChatType;

    public AVChatAction(AVChatType avChatType) {
        super(avChatType == AVChatType.AUDIO ? R.drawable.message_plus_audio_chat_selector : R.drawable.message_plus_video_chat_selector,
                avChatType == AVChatType.AUDIO ? R.string.input_panel_audio_call : R.string.input_panel_video_call);
        this.avChatType = avChatType;
    }

    @Override
    public void onClick() {
        if (NetworkUtil.isNetAvailable(getActivity())) {
            startAudioVideoCall(avChatType);
        } else {
            ToastHelper.showToast(getActivity(), R.string.network_is_not_available);
        }
    }

    /************************ 音视频通话 ***********************/

    public void startAudioVideoCall(AVChatType avChatType) {
        Toast.makeText(getActivity(), "因付费问题，暂停视频功能", Toast.LENGTH_SHORT).show();
    }

    // 打开视频
    private void openVideo() {
        String OrderID = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE).getString("OrderId", "");
        String token = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE).getString("SP_Token", "");
        Log.e("xiaowu", "extendMessage_id:" + OrderID);
        loadData(token, OrderID);

    }

    private void loadData(String token, final String id) {
        NimHttpClient.getInstance().init(getActivity());
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        NimHttpClient.getInstance().execute("http://qa.api.legal.prisonpublic.com:8086/lawyer/my/legal-advice/" + id + "/video-duration", headers, "", new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable e) {
                Log.e("xiaowu", "inputPanel:" + response);
                if (code == 200) {
                    try {
                        org.json.JSONObject d = new org.json.JSONObject(response);
                        String videoDuration = d.getString("videoDuration");
                        if (Double.parseDouble(videoDuration) > 0) {
                            String extendMessage = "{\"legalAdviceId\":\"" + id + "\",\"videoDuration\":" + videoDuration + "}";
                            AVChatKit.outgoingCall(getActivity(), getAccount(), UserInfoHelper.getUserDisplayName(getAccount()), AVChatType.VIDEO.getValue(), AVChatActivity.FROM_INTERNAL, extendMessage);
                        } else {
                            EasyAlertDialogHelper.showOneButtonDiolag(getActivity(), "", "视频通话时长已用完", "确认", true, null);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }

            }

        });
    }

}
