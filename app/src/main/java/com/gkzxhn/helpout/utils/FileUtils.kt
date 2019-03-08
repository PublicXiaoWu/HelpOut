package com.gkzxhn.helpout.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream


object FileUtils{

    fun getFileByUri(uri: Uri, context: Context): File? {

        var imagePath: String? = null

        if (DocumentsContract.isDocumentUri(context, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri.authority) {
                //Log.d(TAG, uri.toString());
                val id = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                //Log.d(TAG, uri.toString());
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(docId))
                imagePath = getImagePath(context, contentUri, null)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            //Log.d(TAG, "content: " + uri.toString());
            imagePath = getImagePath(context, uri, null)
        }
        return File(imagePath)
    }

    private fun getImagePath(context: Context, uri: Uri, selection: String?): String? {
        var path: String? = null
        val cursor = context.contentResolver.query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                path = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Images.Media.DATA))
            }

            cursor!!.close()
        }
        return path
    }

    /**
     * 根据Uri返回文件绝对路径
     * 兼容了file:///开头的 和 content://开头的情况
     */
    private fun getRealFilePathFromUri(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null) {
            data = uri.path
        } else if (ContentResolver.SCHEME_FILE.equals(scheme, ignoreCase = true)) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme, ignoreCase = true)) {
            val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }

    /**
     * 检查文件是否存在
     */
    fun checkDirPath(dirPath: String): String {
        if (TextUtils.isEmpty(dirPath)) {
            return ""
        }
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dirPath
    }

    // 将输入流解析成字节数组
    @Throws(IOException::class)
    fun input2byte(inStream: InputStream): ByteArray {
        val swapStream = ByteArrayOutputStream()
        val buff = ByteArray(100)
        var rc = inStream.read(buff, 0, 100)
        while (rc > 0) {
            swapStream.write(buff, 0, rc)
            rc = inStream.read(buff, 0, 100)
        }
        return swapStream.toByteArray()
    }


    /**
     * 获取指定文件夹的大小
     *
     * @param f
     * @return
     * @throws Exception
     */
    fun getFileSizes(f: File): Long {
        var size: Long = 0
        val flist = f.listFiles()
                ?: //4.2的模拟器空指针。
                return 0//文件夹目录下的所有文件
        if (flist != null) {
            for (i in flist.indices) {
                if (flist[i].isDirectory) {//判断是否父目录下还有子目录
                    size = size + getFileSizes(flist[i])
                } else {
                    size = size + getFileSize(flist[i])
                }
            }
        }
        return size
    }

    /**
     * 获取指定文件的大小
     *
     * @return
     * @throws Exception
     */
    fun getFileSize(file: File): Long {

        var size: Long = 0
        if (file.exists()) {
            size = file.length()

        } else {
        }
        return size
    }

    /**
     * 获取手机内部空间总大小
     *
     * @return 大小，字节为单位
     */
    fun getTotalInternalMemorySize(): Long {
        //获取内部存储根目录
        val path = Environment.getDataDirectory()
        //系统的空间描述类
        val stat = StatFs(path.path)
        //每个区块占字节数
        val blockSize = stat.blockSize.toLong()
        //区块总数
        val totalBlocks = stat.blockCount.toLong()
        return totalBlocks * blockSize
    }


    //删除指定文件夹下所有文件
    //param path 文件夹完整绝对路径
    fun delAllFile(path: String): Boolean {
        var flag = false
        val file = File(path)
        if (!file.exists()) {
            return flag
        }
        if (!file.isDirectory) {
            return flag
        }
        val tempList = file.list()
        var temp: File? = null
        for (i in tempList!!.indices) {
            if (path.endsWith(File.separator)) {
                temp = File(path + tempList[i])
            } else {
                temp = File(path + File.separator + tempList[i])
            }
            if (temp.isFile) {
                temp.delete()
            }
            if (temp.isDirectory) {
                delAllFile(path + "/" + tempList[i])//先删除文件夹里面的文件
                flag = true
            }
        }
        return flag
    }


}