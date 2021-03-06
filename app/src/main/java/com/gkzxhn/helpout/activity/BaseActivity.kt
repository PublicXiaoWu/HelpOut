package com.gkzxhn.helpout.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.common.RxBus
import com.gkzxhn.helpout.entity.UpdateInfo
import com.gkzxhn.helpout.entity.rxbus.RxBusBean
import com.gkzxhn.helpout.net.NetWorkCodeInfo
import com.gkzxhn.helpout.utils.SystemUtil
import com.gkzxhn.helpout.utils.TsClickDialog
import com.gkzxhn.helpout.utils.download.HttpDownManager
import com.gkzxhn.helpout.utils.download.HttpProgressOnNextListener
import com.gkzxhn.helpout.utils.download.entity.DownInfo
import com.gkzxhn.helpout.utils.download.entity.DownState
import com.gkzxhn.helpout.utils.logE
import com.gkzxhn.helpout.utils.showToast
import com.gkzxhn.helpout.view.BaseView
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.friend.model.AddFriendNotify
import com.netease.nimlib.sdk.msg.SystemMessageObserver
import com.netease.nimlib.sdk.msg.constant.SystemMessageType
import com.netease.nimlib.sdk.msg.model.SystemMessage
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.dialog_ts.*
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import java.io.File


/**
 * Explanation:
 * @author LSX
 *    -----2018/9/6
 */
abstract class BaseActivity : AppCompatActivity(), BaseView {
    var open = 0x00001
    var rxPermissions: RxPermissions? = null
    lateinit var mCompositeSubscription: CompositeSubscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //设置竖屏锁死
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        initBefore()
        setContentView(provideContentViewId())
        mCompositeSubscription = CompositeSubscription()
        init()
        //初始化权限管理
        rxPermissions = RxPermissions(this)

        RxBus.instance.toObserverable(DownInfo::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    updateProgress(it)
                }, {
                    it.message.toString().logE(this)
                })

        /******  在其它地方登录 ******/
        RxBus.instance.toObserverable(RxBusBean.LoginOut::class.java)
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    App.EDIT.putString(Constants.SP_TOKEN, "")?.commit()
                    NIMClient.getService(AuthService::class.java).logout()
                    if (javaClass.simpleName == "LoginActivity" || javaClass.simpleName == "SplashActivity") {
                        /****** 已经在登录 闪屏 页面就不要弹出来了 ******/
                    } else {
                        TsClickDialog(getString(R.string.account_logout), false).dialog_save.setOnClickListener {
                            App.EDIT.putString(Constants.SP_TOKEN, "")?.commit()
                            App.EDIT.putString(Constants.SP_NAME, "")?.commit()
                            App.EDIT.putString(Constants.SP_LAWOFFICE, "")?.commit()
                            App.EDIT.putString(Constants.SP_CERTIFICATIONSTATUS, "")?.commit()

                            /****** 清除缓存 ******/
                            SystemUtil.clearAllCache(this)

                            val intent = Intent(this, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }
                    }

                }, {
                    it.message.toString().logE(this)
                })

        /****** 注册添加好友相关的监听 ******/
        NIMClient.getService<SystemMessageObserver>(SystemMessageObserver::class.java).observeReceiveSystemMsg(systemMessageObserver, true)

    }

    private val systemMessageObserver = Observer<SystemMessage> { systemMessage ->
        if (systemMessage.type === SystemMessageType.AddFriend) {
            val attachData = systemMessage.attachObject as AddFriendNotify
            when (attachData.event) {
                AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT -> {
                    // 对方直接添加你为好友
                }
                AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND -> {
                    // 对方通过了你的好友验证请求
                }
                AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND -> {
                    // 对方拒绝了你的好友验证请求
                }
                AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST -> {
                    RxBus.instance.post(RxBusBean.AddPoint(true))
                    // 对方请求添加好友，一般场景会让用户选择同意或拒绝对方的好友请求。
                    // 通过message.getContent()获取好友验证请求的附言
                }
            }
        }
    }

    /**
     * Explanation: 在加载布局前的方法，需要使用可以重写
     * @author LSX
     *    -----2018/9/11
     */
    open fun initBefore() {}

    /**
     * @methodName： created by PrivateXiaoWu on 2018/9/11 9:12.
     * @description：
     */
    abstract fun init()

    /**
     * Explanation: 返回布局文件
     * @author LSX
     *    -----2018/9/11
     */
    abstract fun provideContentViewId(): Int


    /**
     * Explanation: 单个权限申请
     * @author LSX
     *    -----2018/9/11
     */
    open fun singlePermissions(permission: String) {
        rxPermissions?.requestEach(permission)
                ?.subscribe { permission ->
                    when (permission.granted) {
                        // 用户已经同意该权限
                        true -> consent()
                        // 用户拒绝了该权限，并且选中『不再询问』
                        permission.shouldShowRequestPermissionRationale -> Dialog()
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        else -> showToast(permission.name)
                    }
                }
    }

    /**
     * Explanation: 当用户权限被调用，且同意，所要走的方法 子类需要自己去实现
     * @author LSX
     *    -----2018/9/11
     */
    open fun consent() {}

    //一次获取所有权限,
    open fun multiPermissions() {
        open = 0x00001
        RxPermissions(this)
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe({ permission: Permission ->
                    when {
                        permission.granted -> {
                            // 用户已经同意该权限
                            Log.d(javaClass.simpleName, permission.name + " is granted.")
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.d(javaClass.simpleName, permission.name + " is denied. More info should be provided.")
                        }
                        else -> {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            multiPermissions()
                            Log.d(javaClass.simpleName, permission.name + " is denied.")

                        }
                    }
                }, {
                    it.message.toString().logE(this)
                })
    }

    private fun judge() {
        when (open) {
            0x00001 -> Dialog()
        }
    }

    private fun Dialog() {
        open = 0x0002
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.permission_is_not_open))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.cancel)) { dialog, id -> dialog.cancel() }
                .setNegativeButton(getString(R.string.go_set)) { dialog, id -> getAppDetailSettingIntent() }.show()
        builder.create()
    }

    //当权限被拒绝前往设置界面
    @SuppressLint("ObsoleteSdkInt")
    private fun getAppDetailSettingIntent() {
        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT > 16) {
            localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            localIntent.data = Uri.fromParts("package", packageName, null)
        } else if (Build.VERSION.SDK_INT <= 16) {
            localIntent.action = Intent.ACTION_VIEW
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
            localIntent.putExtra("com.android.settings.ApplicationPkgName", packageName)
        }
        startActivity(localIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        when {
            mCompositeSubscription.hasSubscriptions() -> mCompositeSubscription.unsubscribe()
        }
    }


    var downloadDialog: MaterialDialog? = null
    lateinit var mpbProgress: ProgressBar
    lateinit var tvCancel: TextView
    lateinit var tvConfirm: TextView
    lateinit var v_line: View
    var downloadInfo: DownInfo? = null

    /**
     * 显示更新弹窗
     */
    @SuppressLint("SetTextI18n")
    fun showDownloadDialog(updateInfo: UpdateInfo) {
        downloadDialog = downloadDialog ?: MaterialDialog.Builder(this)
                .customView(R.layout.dialog_seeting_update, false)
                .cancelable(!updateInfo.forced!!)
                .show()

        if (!downloadDialog?.isShowing!!) {
            downloadDialog?.show()
        }
        val tvVersionNumber = downloadDialog?.customView!!.findViewById<TextView>(R.id.tv_update_dialog_version)
        val tvDesc = downloadDialog?.customView!!.findViewById<TextView>(R.id.tv_update_dialog_context)
        val desc: CharSequence = updateInfo.description ?: ""
        if (TextUtils.isEmpty(desc)) {
            tvDesc.visibility = View.GONE
        } else {
            tvDesc.visibility = View.VISIBLE
            tvDesc.text = desc.toString().replace("\\n", "\n")
        }
        mpbProgress = downloadDialog?.customView!!.findViewById(R.id.pb_update_dialog_progress_bar)
        tvCancel = downloadDialog?.customView!!.findViewById<TextView>(R.id.tv_update_dialog_cancel)
        tvConfirm = downloadDialog?.customView!!.findViewById<TextView>(R.id.tv_update_dialog_update)
        v_line = downloadDialog?.customView!!.findViewById<TextView>(R.id.v_dialog_update)
        tvCancel.visibility = if (updateInfo.forced!!) {
            v_line.visibility = View.GONE
            View.GONE
        } else View.VISIBLE

        val filters = HttpDownManager.getInstance().downInfos.filter { updateInfo.packageFileName == it.url }
        if (filters.isNotEmpty()) {
            downloadInfo = filters[0]
        }
        if (downloadInfo != null) {
            var status = getString(R.string.update_now)
            when (downloadInfo?.state) {
                DownState.DOWN -> {
                    //停止状态
                    status = getString(R.string.go_on)
                }
                DownState.START -> {
                    status = getString(R.string.pause)
                }
                DownState.FINISH -> {
                    status = getString(R.string.install)
                }
                DownState.PAUSE, DownState.STOP -> {
                    //暂停, 点击继续下载
                    status = getString(R.string.go_on)
                }
                DownState.ERROR -> {
                    status = getString(R.string.retry)
                }
                else -> {
                }
            }
            tvConfirm.text = status
        }

        tvVersionNumber.text = "V${updateInfo.name}"
        setDownloadDialogClick(tvCancel, tvConfirm, updateInfo)
    }

    private fun setDownloadDialogClick(tvCancel: TextView, tvConfirm: TextView, updateInfo: UpdateInfo?) {
        tvCancel.setOnClickListener {
            downloadDialog?.dismiss()
        }

        tvConfirm.setOnClickListener {
            downloadInfo?.state.toString().logE(this@BaseActivity)
            tvCancel.text = getString(R.string.download_background)
            if (downloadInfo == null) {
                downloadInfo = DownInfo()

                val externalFilesDir = getExternalFilesDir("download")
                if (!externalFilesDir.exists()) {
                    externalFilesDir.mkdirs()
                }

                downloadInfo?.baseUrl = NetWorkCodeInfo.BASE_URL
                downloadInfo?.url = updateInfo?.packageFileName
                downloadInfo?.savePath = File(externalFilesDir, Constants.APK_ADRESS).absolutePath

                downloadInfo?.listener = object : HttpProgressOnNextListener<DownInfo>() {
                    override fun onNext(t: DownInfo) {
                        "download on next ---- ${t.state?.toString()}".logE(this)
                    }

                    override fun onStart() {
                        "download onStart ----".logE(this)
                        tvConfirm.text = getString(R.string.pause)
                    }

                    override fun onComplete() {
                        "download onComplete ----".logE(this)
                        tvConfirm.text = getString(R.string.install)
                        installApk()
                    }

                    override fun updateProgress(readLength: Long, countLength: Long) {
                        "download updateProgress ---- $readLength".logE(this)
                        mpbProgress.max = countLength.toInt()
                        mpbProgress.progress = readLength.toInt()
                    }
                }

                HttpDownManager.getInstance().startDown(downloadInfo)
                tvConfirm.setTextColor(ContextCompat.getColor(App.mContext, R.color.text_gray))
            } else {
                "download updateProgress ---- ${downloadInfo?.readLength}>>>>${downloadInfo?.countLength}".logE(this)
                when (downloadInfo?.state) {
                    DownState.DOWN -> {
                        //停止状态, 点击继续下载
                        HttpDownManager.getInstance().startDown(downloadInfo)
                        /****** 暂时不做断点下载  所以文字方面先不显示 功能 未完成 等待下版本更新 ******/
//                        tvConfirm.text = getString(R.string.pause)
                    }
                    DownState.START -> {
                        //正在下载中, 暂停下载
                        HttpDownManager.getInstance().pause(downloadInfo)
                    }
                    DownState.FINISH -> {
                        installApk()
                    }
                    DownState.PAUSE, DownState.STOP -> {
                        //暂停, 点击继续下载
                        HttpDownManager.getInstance().startDown(downloadInfo)
                    }
                    DownState.ERROR -> {
                        HttpDownManager.getInstance().startDown(downloadInfo)
                    }
                    else -> {
                        HttpDownManager.getInstance().startDown(downloadInfo)
                    }
                }
            }
        }
    }

    private fun installApk() {
        val intent = Intent("android.intent.action.VIEW")
        val externalFilesDir = getExternalFilesDir("download")
        val file = File(externalFilesDir, Constants.APK_ADRESS)
        if (file.exists() && file.length() == downloadInfo?.countLength) {
            var uri: Uri? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(App.mContext, "$packageName.fileprovider",
                        file)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                uri = Uri.fromFile(file)
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            startActivity(intent)
        } else {
            showToast(getString(R.string.file_is_damaged))
            HttpDownManager.getInstance().let {
                it.subMap.remove(downloadInfo?.url)
                it.downInfos.remove(downloadInfo)
                downloadInfo?.state = DownState.STOP
                downloadInfo?.readLength = 0
//                tvConfirm.text = "暂停"
                it.startDown(downloadInfo)
            }
        }
    }

    fun updateProgress(it: DownInfo) {
        "reading>>>>${it.countLength}>>>>>>>>>>${it.readLength}----".logE(this)
        mpbProgress.max = it.countLength.toInt()
        mpbProgress.progress = it.readLength.toInt()
    }
}


