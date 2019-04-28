package com.gkzxhn.helpout.entity

import com.luck.picture.lib.entity.LocalMedia

/**
 * @classname：生活圈数据
 * @author：liushaoxiang
 * @date：2019/4/17 4:26 PM
 * @description：
 */
class LivesCircle {
    /**
     * content : [{"id":"6a9356f7-de06-4d5f-9939-3430074e4fb1","content":"内容","customer":{"id":"9372a2da-9f80-4fa4-910a-4980fe77d6d1","name":"发布者名称"},"commentNum":2,"praiseNum":3,"createdTime":"2019-04-19T03:37:28.491+0000","circleoffriendsPicture":[{"fileId":"a174a561-1262-4ea6-8f40-7e93f105a727","thumbFileId":null}]}]
     * last : true
     * totalElements : 1
     * totalPages : 1
     * number : 0
     * size : 10
     * sort : [{"direction":"ASC","property":"createdTime","ignoreCase":false,"nullHandling":"NATIVE","ascending":true,"descending":false}]
     * numberOfElements : 1
     * first : true
     */

    var last: Boolean = false
    var totalElements: Int = 0
    var totalPages: Int = 0
    var number: Int = 0
    var size: Int = 0
    var numberOfElements: Int = 0
    var first: Boolean = false
    var content: List<ContentBean>? = null
    var sort: List<SortBean>? = null

    class ContentBean {
        /**
         * id : 6a9356f7-de06-4d5f-9939-3430074e4fb1
         * content : 内容
         * customer : {"id":"9372a2da-9f80-4fa4-910a-4980fe77d6d1","name":"发布者名称"}
         * commentNum : 2
         * praiseNum : 3
         * createdTime : 2019-04-19T03:37:28.491+0000
         * circleoffriendsPicture : [{"fileId":"a174a561-1262-4ea6-8f40-7e93f105a727","thumbFileId":null}]
         */

        var id: String? = null
        var content: String? = null
        var praisesCircleoffriends: Boolean=false
        var username: String=""
        var customer: CustomerBean? = null
        var commentNum: Int = 0
        var praiseNum: Int = 0
        var createdTime: String? = null
        var circleoffriendsPicture: List<CircleoffriendsPictureBean>? = null
        var localImages : List<LocalMedia>? = null  //本地图片列表

        class CustomerBean {
            /**
             * id : 9372a2da-9f80-4fa4-910a-4980fe77d6d1
             * name : 发布者名称
             */

            var id: String? = null
            var name: String? = null
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

    class SortBean {
        /**
         * direction : ASC
         * property : createdTime
         * ignoreCase : false
         * nullHandling : NATIVE
         * ascending : true
         * descending : false
         */

        var direction: String? = null
        var property: String? = null
        var isIgnoreCase: Boolean = false
        var nullHandling: String? = null
        var isAscending: Boolean = false
        var isDescending: Boolean = false
    }

}
