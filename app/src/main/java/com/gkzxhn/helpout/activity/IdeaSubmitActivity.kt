package com.gkzxhn.helpout.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ScrollView
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.adapter.FeedbackTypesAdapter
import com.gkzxhn.helpout.common.IntentConstants
import com.gkzxhn.helpout.entity.FeedBackRequestInfo
import com.gkzxhn.helpout.entity.UIInfo.NormalListItem
import com.gkzxhn.helpout.extensions.filterEmoji
import com.gkzxhn.helpout.presenter.FeedBackPresenter
import com.gkzxhn.helpout.utils.*
import com.gkzxhn.helpout.view.FeedBackView
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_write_message.*
import kotlinx.android.synthetic.main.default_top.*
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

/**
 * @classname：IdeaSubmitActivity
 * @author：liushaoxiang
 * @date：2018/10/12 11:55 AM
 * @description：意见 反馈
 */
class IdeaSubmitActivity : BaseActivity(), FeedBackView {

    var evidenceFile1: File? = null
    var evidenceFile2: File? = null
    var evidenceFile3: File? = null
    var evidenceFile4: File? = null

    var evidenceUrl1: String? = null
    var evidenceUrl2: String? = null
    var evidenceUrl3: String? = null
    var evidenceUrl4: String? = null

    var disposables = hashMapOf<Int, Subscription>()
    private lateinit var mPresenter: FeedBackPresenter

    companion object {
        val WRITE_MESSAGE_CODE = 1
        fun launch(context: Activity, type: Int = 0) {
            val intent = Intent(context, IdeaSubmitActivity::class.java)
            intent.putExtra(IntentConstants.ID, type)
            context.startActivityForResult(intent, WRITE_MESSAGE_CODE)
        }
    }

    override fun provideContentViewId(): Int {
        return R.layout.activity_write_message
    }

    override fun init() {
        mPresenter = FeedBackPresenter(this, this)
        initToolbar()
        initRecyclerView()
        setListeners()
        setOnclick()
//        presenter.init(type)
    }

    private fun initToolbar() {
        tv_default_top_title.text = getString(R.string.feed_back)
        iv_default_top_back.setOnClickListener { finish() }
    }

    private lateinit var mAdapter: FeedbackTypesAdapter

    private var problem: String? = null

    private fun initRecyclerView() {
        val reasons = resources.getStringArray(R.array.feedback_category)
                .mapIndexed { index, it ->
                    NormalListItem(text = it, isCheck = index == 0, id = index.toLong())
                }
        problem = try {
            reasons[0].text
        } catch (e: Exception) {
            null
        }
        rv_feedback_types.layoutManager = LinearLayoutManager(this)
        mAdapter = FeedbackTypesAdapter(null)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (position != mAdapter.checkedPosition) {
                problem = mAdapter.data[position].text
                mAdapter.data[position].isCheck = true
                mAdapter.data[mAdapter.checkedPosition].isCheck = false
                mAdapter.notifyItemChanged(position + mAdapter.headerLayoutCount)
                mAdapter.notifyItemChanged(mAdapter.checkedPosition + mAdapter.headerLayoutCount)
            }
        }
        val headerView = LayoutInflater.from(this).inflate(R.layout.layout_title_view, null, false)
        mAdapter.addHeaderView(headerView)
        rv_feedback_types.adapter = mAdapter
        mAdapter.setNewData(reasons)
    }

    var hasScrollDown = false

    private fun setListeners() {
        et_content_advice.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && !hasScrollDown) {
                scroll_view.postDelayed({
                    scroll_view.fullScroll(ScrollView.FOCUS_DOWN)
                    hasScrollDown = true
                    et_content_advice.requestFocus()
                }, 100)
            } else {
            }
        }
        et_content_advice.setOnClickListener {
            if (!hasScrollDown) {
                scroll_view.postDelayed({
                    scroll_view.fullScroll(ScrollView.FOCUS_DOWN)
                    hasScrollDown = true
                    et_content_advice.requestFocus()
                }, 100)
            } else {
            }
        }
        et_content_advice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s)) {
                    if (s?.length ?: 0 > 300) {
                        et_content_advice.setText(s?.subSequence(0, 300))
                        et_content_advice.setSelection(300)
                    } else {
                        tv_text_count.text = "${s?.length}/300"
                    }
                }
            }
        })
    }

    private fun setOnclick() {
        tv_upload_evidence.setOnClickListener {
            showListDialog("evidence.jpg", false)
        }

        iv_evidence_pic1.setOnClickListener {
            val uri = file2Uri(evidenceFile1)
            if ((uri != null)) {
                ImageActivity.launch(this, uri, imageView = iv_evidence_pic1)
            }
        }
        iv_evidence_pic2.setOnClickListener {
            val uri = file2Uri(evidenceFile2)
            if ((uri != null)) {
                ImageActivity.launch(this, uri, imageView = iv_evidence_pic2)
            }
        }
        iv_evidence_pic3.setOnClickListener {
            val uri = file2Uri(evidenceFile3)
            if ((uri != null)) {
                ImageActivity.launch(this, uri, imageView = iv_evidence_pic3)
            }
        }
        iv_evidence_pic4.setOnClickListener {
            val uri = file2Uri(evidenceFile4)
            if ((uri != null)) {
                ImageActivity.launch(this, uri, imageView = iv_evidence_pic4)
            }
        }

        iv_close_evidence1.setOnClickListener {
            evidenceUrl1?.let { it1 -> mPresenter.deleteImg(it1) }
            evidenceFile1 = null
            evidenceUrl1 = null
            fl_evidence_pic1.visibility = View.GONE
            tv_upload_evidence.visibility = View.VISIBLE
            if (disposables[0]?.isUnsubscribed == false) {
                disposables[0]?.unsubscribe()
            }
        }
        iv_close_evidence2.setOnClickListener {
            evidenceUrl2?.let { it1 -> mPresenter.deleteImg(it1) }
            evidenceFile2 = null
            evidenceUrl2 = null
            fl_evidence_pic2.visibility = View.GONE
            tv_upload_evidence.visibility = View.VISIBLE
            if (disposables[1]?.isUnsubscribed == false) {
                disposables[1]?.unsubscribe()
            }
        }
        iv_close_evidence3.setOnClickListener {
            evidenceUrl3?.let { it1 -> mPresenter.deleteImg(it1) }
            evidenceFile3 = null
            evidenceUrl3 = null
            fl_evidence_pic3.visibility = View.GONE
            tv_upload_evidence.visibility = View.VISIBLE
            if (disposables[2]?.isUnsubscribed == false) {
                disposables[2]?.unsubscribe()
            }
        }
        iv_close_evidence4.setOnClickListener {
            evidenceUrl4?.let { it1 -> mPresenter.deleteImg(it1) }
            evidenceFile4 = null
            evidenceUrl4 = null
            fl_evidence_pic4.visibility = View.GONE
            tv_upload_evidence.visibility = View.VISIBLE
            if (disposables[3]?.isUnsubscribed == false) {
                disposables[3]?.unsubscribe()
            }
        }

        bt_commit.setOnClickListener {
            val content = et_content_advice.text.toString().filterEmoji()
            et_content_advice.setText(content)
            if (TextUtils.isEmpty(content) || content.length < 10) {
                showToast(getString(R.string.please_enter_content))
                return@setOnClickListener
            }

            disposables.forEach {
                if (!it.value.isUnsubscribed) {
                    showToast("还有图片正在上传,请稍后")
                    return@setOnClickListener
                }
            }

            val feedBackRequestInfo = FeedBackRequestInfo()
            feedBackRequestInfo.problem = problem
            feedBackRequestInfo.detail = content
            val attachments = arrayListOf<String>()
            evidenceUrl1?.let { it1 -> attachments.add(it1) }
            evidenceUrl2?.let { it1 -> attachments.add(it1) }
            evidenceUrl3?.let { it1 -> attachments.add(it1) }
            evidenceUrl4?.let { it1 -> attachments.add(it1) }
            feedBackRequestInfo.attachments = attachments

            mPresenter.postFeedBack(feedBackRequestInfo)
        }
    }

    /**
     * 弹出图片来源选择窗
     */
    private fun showListDialog(fileName: String, front: Boolean) {
        MaterialDialog.Builder(this)
                .title(getString(R.string.please_choice_photo_from))
                .items(getString(R.string.take_photo), getString(R.string.photo_album))
                .itemsGravity(GravityEnum.START)
                .itemsCallback { dialog, itemView, position, text ->
                    when (position) {
                        0 -> {
                            //拍照
                            requestPermission(fileName, TAKE_PHOTO_IMAGE, front)
                        }
                        1 -> {
                            //相册选择图片
                            requestPermission()
                        }
                        else -> {
                        }
                    }
                }
                .show()
    }

    fun requestPermission() {
        var storageFlag = 0
        RxPermissions(this)
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe({ permission: Permission ->
                    if (permission.granted) {
                        // 用户已经同意该权限
                        if (++storageFlag == 2) {
                            chooseAlbum()
                        }
                        Log.d(javaClass.simpleName, permission.name + " is granted.")
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        Log.d(javaClass.simpleName, permission.name + " is denied. More info should be provided.");
                        showToast(getString(R.string.please_agree_permission))
                    } else {
                        // 用户拒绝了该权限，并且选中『不再询问』
                        Log.d(javaClass.simpleName, permission.name + " is denied.")
                        showToast(getString(R.string.please_agree_permission))
                    }
                }, {
                    it.message.toString().logE(this)
                })
    }

    private val CHOOSE_PHOTO_CODE = 1
    private val TAKE_PHOTO_IMAGE = 2

    /**
     * 相册选择图片
     */
    private fun chooseAlbum() {
        val openAlbumIntent = Intent(Intent.ACTION_PICK)
        openAlbumIntent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果大于等于7.0使用FileProvider
            val mGalleryFile = File(File(externalCacheDir, "photo"), "12345.jpg")
            val uriForFile = FileProvider.getUriForFile(this, "${packageName}.fileprovider", mGalleryFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivityForResult(Intent.createChooser(openAlbumIntent, "File Browser"), CHOOSE_PHOTO_CODE)
    }

    private fun file2Uri(file: File?): Uri? {
        val uri = if (Build.VERSION.SDK_INT >= 24) {
            file?.let { it1 -> FileProvider.getUriForFile(this, "${packageName}.fileprovider", it1) }
        } else {
            Uri.fromFile(file)
        }
        return uri
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CHOOSE_PHOTO_CODE, TAKE_PHOTO_IMAGE -> {
//                    if (data == null || data.data == null) {
//                        return
//                    }
                    var uri: Uri? = null
                    var file: File? = null
                    if (requestCode == CHOOSE_PHOTO_CODE) {
                        uri = data?.data
                        file = FileUtils.getFileByUri(uri!!, this)
                    } else {
                        file =  uri2File(File(externalCacheDir, "photo"), mTakePhotoUri!!)
                    }
                    var bitmap = ImageUtils.decodeSampledBitmapFromFilePath(file!!.absolutePath, 720, 720)
                    // 部分手机会对图片做旋转，这里检测旋转角度
                    val degree = FaceUtil.readPictureDegree(file!!.absolutePath)
                    if (degree != 0) {
                        // 把图片旋转为正的方向
                        bitmap = FaceUtil.rotateImage(degree, bitmap)
                    }
                    val dir = File(externalCacheDir, "photo")
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }
                    var cacheFile: File? = null
                    var tempPosition: Int = -1
                    if (evidenceFile1 == null) {
                        tempPosition = 0
                        evidenceFile1 = File(dir, "evidence_pic_$tempPosition.jpg")
                        cacheFile = evidenceFile1
                        fl_evidence_pic1.visibility = View.VISIBLE
                        tv_upload_status1.visibility = View.VISIBLE
                        ProjectUtils.loadRoundCorner(this, file, iv_evidence_pic1)
//                        iv_evidence_pic1.setImageBitmap(bitmap)
                    } else if (evidenceFile2 == null) {
                        tempPosition = 1
                        evidenceFile2 = File(dir, "evidence_pic_$tempPosition.jpg")
                        cacheFile = evidenceFile2
                        fl_evidence_pic2.visibility = View.VISIBLE
                        tv_upload_status2.visibility = View.VISIBLE
                        ProjectUtils.loadRoundCorner(this, file, iv_evidence_pic2)
                    } else if (evidenceFile3 == null) {
                        tempPosition = 2
                        evidenceFile3 = File(dir, "evidence_pic_$tempPosition.jpg")
                        cacheFile = evidenceFile3
                        fl_evidence_pic3.visibility = View.VISIBLE
                        tv_upload_status3.visibility = View.VISIBLE
                        ProjectUtils.loadRoundCorner(this, file, iv_evidence_pic3)
                    } else if (evidenceFile4 == null) {
                        tempPosition = 3
                        evidenceFile4 = File(dir, "evidence_pic_$tempPosition.jpg")
                        cacheFile = evidenceFile4
                        fl_evidence_pic4.visibility = View.VISIBLE
                        tv_upload_status4.visibility = View.VISIBLE
                        tv_upload_evidence.visibility = View.GONE
                        ProjectUtils.loadRoundCorner(this, file, iv_evidence_pic4)
                    }
                    Observable.create<File> {
                        it.onNext(cacheFile?.let { it1 -> bitmap.compressImage(it1, 300) }!!)
                    }/*.bindToLifecycle(this)*/
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                mPresenter.uploadImg(tempPosition, it)?.let { disposables[tempPosition] = it }
                            }
                }
                else -> {
                }
            }
        }
    }

    private fun uri2File(cacheDir: File, uri: Uri): File {
        val uriFile = File(uri.path)
        return File(cacheDir, uriFile.name)
    }


    //上传成功
    override fun showUploadSuccess(position: Int, url: String) {
        when (position) {
            0 -> {
                tv_upload_status1.visibility = View.GONE
                evidenceUrl1 = url
            }
            1 -> {
                tv_upload_status2.visibility = View.GONE
                evidenceUrl2 = url
            }
            2 -> {
                tv_upload_status3.visibility = View.GONE
                evidenceUrl3 = url
            }
            3 -> {
                tv_upload_status4.visibility = View.GONE
                evidenceUrl4 = url
            }
            else -> {
            }
        }
    }

    //图片上传失败
    override fun showUploadError(position: Int, e: Throwable) {
        when (position) {
            0 -> {
                tv_upload_status1
            }
            1 -> {
                tv_upload_status2
            }
            2 -> {
                tv_upload_status3
            }
            3 -> {
                tv_upload_status4
            }
            else -> {
                null
            }
        }?.let { it.text = getString(R.string.upload_failed) }
    }

    override fun uploadSuccess() {
        evidenceUrl1 = null
        evidenceUrl2 = null
        evidenceUrl3 = null
        evidenceUrl4 = null
        showToast(getString(R.string.commit_success))
        finish()
    }

    var mTakePhotoUri: Uri? = null      //拍照临时uri

    fun requestPermission(fileName: String, requestCode: Int, front: Boolean) {
        if (Build.VERSION.SDK_INT >= 23) {
            RxPermissions(this)
                    .requestEach(Manifest.permission.CAMERA)
                    .subscribe { permission: Permission ->
                        if (permission.granted) {
                            // 用户已经同意该权限
                            takePhotoFromCamera(fileName, requestCode, front)
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.d(javaClass.simpleName, permission.name + " is denied. More info should be provided.");
                            showToast(getString(R.string.please_agree_permission))
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.d(javaClass.simpleName, permission.name + " is denied.")
                            showToast(getString(R.string.please_agree_permission))
                        }
                    }
        } else {
            takePhotoFromCamera(fileName, requestCode, front)
        }
    }

    lateinit var photoDir: File

    /**
     * 相机拍照
     */
    private fun takePhotoFromCamera(fileName: String, requestCode: Int, front: Boolean) {
        val openCameraIntent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE)
        photoDir = File(externalCacheDir, "photo")
        if (!photoDir.exists()) {
            photoDir.mkdirs()
        }
        val file = File(photoDir, fileName)
        if (Build.VERSION.SDK_INT >= 24) {
            mTakePhotoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            mTakePhotoUri = Uri.fromFile(file)
        }
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTakePhotoUri)
        try {
            if (front) {
                //打开前置摄像头
                openCameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1); // 调用前置摄像头
            } else {
                openCameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 2); //
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        startActivityForResult(openCameraIntent, requestCode)
    }

    override fun onDestroy() {
        super.onDestroy()
        evidenceUrl1?.let { mPresenter.deleteImg(it) }
        evidenceUrl2?.let { mPresenter.deleteImg(it) }
        evidenceUrl3?.let { mPresenter.deleteImg(it) }
        evidenceUrl4?.let { mPresenter.deleteImg(it) }
    }
}