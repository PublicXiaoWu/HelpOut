package com.gkzxhn.helpout.entity

/**
 * @classname：LivesCircleDetails
 * @author：liushaoxiang
 * @date：2019/4/23 4:14 PM
 * @description：
 */
class LivesCircleDetails{

    /**
     * id : cc9938f6-6af7-49a5-8d38-6471b21ad859
     * content : 今天心情不错
     * commentNum : 1
     * praiseNum : 1
     * createdTime : 2019-04-22T09:56:19.199+0000
     * customer : {"id":"72724c0e-f1df-4feb-9280-a3ee4473f9a9","name":"test"}
     * circleoffriendsComments : [{"id":"cddf9bbb-3637-4ded-88af-abc5d635936a","content":"评论内容","customerName":"评论者名称","createdTime":"2019-04-22T09:56:19.200+0000"}]
     * circleoffriendsPicture : [{"fileId":"a174a561-1262-4ea6-8f40-7e93f105a727","thumbFileId":null}]
     */

    var id: String? = null
    var content: String? = null
    var praisesCircleoffriends: Boolean=false
    var username: String=""
    var commentNum: Int = 0
    var praiseNum: Int = 0
    var createdTime: String? = null
    var customer: CustomerBean? = null
    var circleoffriendsComments: List<CircleoffriendsCommentsBean>? = null
    var circleoffriendsPicture: List<CircleoffriendsPictureBean>? = null

    class CustomerBean {
        /**
         * id : 72724c0e-f1df-4feb-9280-a3ee4473f9a9
         * name : test
         */

        var id: String? = null
        var name: String? = null
    }

    class CircleoffriendsCommentsBean {
        /**
         * id : cddf9bbb-3637-4ded-88af-abc5d635936a
         * content : 评论内容
         * customerName : 评论者名称
         * createdTime : 2019-04-22T09:56:19.200+0000
         */

        var id: String? = null
        var content: String? = null
        var username: String? = null
        var customerName: String? = null
        var createdTime: String? = null
    }

    class CircleoffriendsPictureBean {
        /**
         * fileId : a174a561-1262-4ea6-8f40-7e93f105a727
         * thumbFileId : null
         */

        var fileId: String? = null
        var thumbFileId: String? = null
    }
}
