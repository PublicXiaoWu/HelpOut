package com.gkzxhn.helpout.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.gkzxhn.helpout.R
import com.gkzxhn.helpout.common.App
import com.gkzxhn.helpout.common.Constants
import com.gkzxhn.helpout.customview.ClipViewLayout
import com.gkzxhn.helpout.net.HttpObserver
import com.gkzxhn.helpout.net.RetrofitClientLogin
import com.gkzxhn.helpout.utils.*
import com.google.gson.Gson
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_accountinfoup.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.util.*

/**
 * @classname：
 * @author：liushaoxiang
 * @date：2019/2/22 11:12 AM
 * @description：账号的信息上传
 */
class AccountInfoUpActivity : BaseActivity() {

    private val TAKE_PHOTO_IMAGE = 101       //拍头像
    private val CHOOSE_PHOTO_IMAGE = 102      //选择头像
    private val REQUEST_CROP_PHOTO = 104     //简单裁剪头像
    lateinit var photoDir: File
    var mTakePhotoUri: Uri? = null      //拍照uri
    private var photoIsUp = false //头像是否上传

    override fun provideContentViewId(): Int {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        return R.layout.activity_accountinfoup
    }

    override fun init() {
        /****** 进入这个界面表示没有完成头像和昵称的上传 ******/
        App.EDIT.putBoolean(Constants.SP_ACCOUNT_COMPLETE, false).commit()

        photoDir = File(externalCacheDir, "photo")
        if (!photoDir.exists()) {
            photoDir.mkdirs()
        }
    }


    /****** 上传用户头像 ******/
    fun upUserImage(view: View) {
        showListDialog("account_info.jpg", false)
    }

    /****** 上传数据 ******/
    fun sendUpload(view: View) {
        val nickName = ev_account_info_nickname.text.toString().trim()
        if (photoIsUp && nickName.isNotEmpty()) {
            modifyNickname(nickName)
        } else {
            showToast("请完成上传和填写昵称")
        }
    }


    /**
     * @methodName： created by liushaoxiang on 2018/10/26 2:07 PM.
     * @description：修改头像
     */
    private fun modifyAvatar(file: File) {
        val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        RetrofitClientLogin.getInstance(this).mApi?.modifyAvatar(body)
                ?.subscribeOn(Schedulers.io())
                ?.unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<Response<Void>>(this) {
                    override fun success(t: Response<Void>) {
                        if (t.code() == 204) {
                            showToast("上传成功")
                            App.EDIT.putString(Constants.SP_MY_ICON, System.currentTimeMillis().toString()).commit()
                            ProjectUtils.loadMyIcon(this@AccountInfoUpActivity, iv_account_info_image)
                            photoIsUp = true
                        } else {
                            showToast("上传失败")
                        }
                    }
                })
    }


    /**
     * @methodName： created by liushaoxiang on 2019/2/22 2:51 PM.
     * @description：修改昵称
     */
    private fun modifyNickname(nickname: String) {
        var map = LinkedHashMap<String, String>()
        map["name"] = intent.getStringExtra("name")
        map["phoneNumber"] = intent.getStringExtra("phoneNumber")
        map["nickname"] = nickname
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                Gson().toJson(map))

        RetrofitClientLogin.getInstance(this).mApi?.modifyAccountInfo(body)
                ?.subscribeOn(Schedulers.io())
                ?.unsubscribeOn(AndroidSchedulers.mainThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : HttpObserver<Response<Void>>(this) {
                    override fun success(t: Response<Void>) {
                        if (t.code() == 204) {
                            /****** 这里已经完成头像和昵称的上传 ******/
                            App.EDIT.putBoolean(Constants.SP_ACCOUNT_COMPLETE, true).commit()
                            startActivity(Intent(this@AccountInfoUpActivity, MainActivity::class.java))
                            finish()
                        } else {
                            showToast("修改昵称失败")
                        }
                    }
                })

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

    /**
     * 相册选择图片
     */
    private fun chooseAlbum() {
        val openAlbumIntent = Intent(Intent.ACTION_PICK)
        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果大于等于7.0使用FileProvider
            val mGalleryFile = File(File(externalCacheDir, "photo"), "12345.jpg")
            val uriForFile = FileProvider.getUriForFile(this, "$packageName.fileprovider", mGalleryFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivityForResult(Intent.createChooser(openAlbumIntent, "File Browser"), CHOOSE_PHOTO_IMAGE)
    }

    /****** 获取拍照权限  ******/
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

    /**
     * 相机拍照
     */
    private fun takePhotoFromCamera(fileName: String, requestCode: Int, front: Boolean) {
        val openCameraIntent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE)
        if (!photoDir.exists()) {
            photoDir.mkdirs()
        }
        val file = File(photoDir, fileName)
        if (Build.VERSION.SDK_INT >= 24) {
            mTakePhotoUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
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

    /**
     * @methodName： created by liushaoxiang on 2018/10/31 10:42 AM.
     * @description：获取相册选择图片权限
     */
    fun requestPermission() {
        var storageFlag = 0
        RxPermissions(this)
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe({ permission: Permission ->
                    when {
                        permission.granted -> {
                            // 用户已经同意该权限
                            if (++storageFlag == 2) {
                                chooseAlbum()
                            }
                            Log.d(javaClass.simpleName, permission.name + " is granted.")
                        }
                        permission.shouldShowRequestPermissionRationale -> // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.d(javaClass.simpleName, permission.name + " is denied. More info should be provided.")
                        else -> // 用户拒绝了该权限，并且选中『不再询问』
                            Log.d(javaClass.simpleName, permission.name + " is denied.")
                    }
                }, {
                    it.message.toString().logE(this)
                })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            mTakePhotoUri.toString().logE(this)
            when (requestCode) {
            /****** 拍照返回 ******/
                TAKE_PHOTO_IMAGE -> {
                    val file = uri2File(File(externalCacheDir, "photo"), mTakePhotoUri!!)
                    val bitmap = ImageUtils.decodeSampledBitmapFromFilePath(file.absolutePath, 720, 720)
                    ImageUtils.compressImage(bitmap, file, 2000)!!
                    /****** 部分机型会自动旋转 这里旋转恢复 ******/
                    val readPictureDegree = SystemUtil.readPictureDegree(file.absolutePath)
                    SystemUtil.rotateBitmap(bitmap, readPictureDegree)
                    gotoClipActivity(Uri.fromFile(file))
                }
            /****** 选择图片返回 ******/
                CHOOSE_PHOTO_IMAGE -> {
                    val file = FileUtils.getFileByUri(data!!.data, this)
                    if (file?.exists()!!) {
                        val bitmap = ImageUtils.decodeSampledBitmapFromFilePath(file.absolutePath, 720, 720)
                        ImageUtils.compressImage(bitmap, file, 2000)!!
                    }
                    gotoClipActivity(data.data)

                }
            /****** 裁剪后返回 ******/
                REQUEST_CROP_PHOTO -> {
                    if (resultCode == Activity.RESULT_OK) {
                        if (data?.data == null) {
                            return
                        }
                        var uri = data.data
                        val cropImagePath = ClipViewLayout.getRealFilePathFromUri(applicationContext, uri)
                        //此处后面可以将bitMap转为二进制上传后台网络
                        modifyAvatar(File(cropImagePath))
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

    /**
     * @methodName： created by liushaoxiang on 2018/10/24 1:58 PM.
     * @description：跳转到普通裁剪页面
     */
    private fun gotoClipActivity(uri: Uri?) {
        if (uri == null) {
            return
        }
        val intent = Intent()
        intent.setClass(this, ClipImageActivity::class.java)

        /****** 一为圆形 二为方形 ******/
        intent.putExtra("type", 2)
        intent.data = uri
        startActivityForResult(intent, REQUEST_CROP_PHOTO)
    }

}

