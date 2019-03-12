package com.gkzxhn.helpout.entity

/**
 * @classname：更新的信息类
 * @author：liushaoxiang
 * @date：2018/10/31 3:34 PM
 * @description：
 */
data class UpdateInfo(

//{"id":"41b477a4-0ced-4dc5-bba5-0becd8155990",
// "platform":"PLATFORM_ADMIN","number":1,
// "name":"v1.0.0","description":"优化界面设计",
// "deviceType":"ANDROID","forced":true,
// "packageFile":"t2b47ced4dc4bba50be5cd8155990","packageSize":123453,"createdTime":"2019-03-12T10:46:48"}

        val name: String?,
        val number: Int?,
        val fileId: String?,
        val packageFile: String?,
        val description: String?,
        val forced: Boolean?
)
