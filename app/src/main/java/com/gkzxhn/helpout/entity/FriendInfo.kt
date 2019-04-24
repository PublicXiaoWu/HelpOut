package com.gkzxhn.helpout.entity

/**
 * @classname：dd
 * @author：liushaoxiang
 * @date：2019/4/24 2:46 PM
 * @description：
 */
class FriendInfo {


    /**
     * id : f5ed7555f3e749e0a9d747af5155183a
     * name : 名称
     * username : f5ec7545f3e749e0a9d747af5155183a
     * account : aaaa
     * phoneNumber : 13812345678
     * friendinfo : 1
     * circleoffriendsPicture : null
     */

    var id: String? = null
    var name: String? = null
    var username: String? = null
    var account: String? = null
    var phoneNumber: String? = null
    var curUsername: String? = null
    var friendinfo: String? = null
    var circleoffriendsPicture: List<CircleoffriendsPictureBean>? = null

    class CircleoffriendsPictureBean {
        /**
         * fileId : a174a561-1262-4ea6-8f40-7e93f105a727
         * thumbFileId : null
         */

        var fileId: String? = null

    }
}
