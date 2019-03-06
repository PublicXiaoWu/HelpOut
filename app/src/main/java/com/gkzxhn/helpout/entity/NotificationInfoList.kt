package com.gkzxhn.helpout.entity

/**
 * @classname：NotificationInfoList
 * @author：liushaoxiang
 * @date：2019/2/28 3:48 PM
 * @description： 通知列表
 */
class NotificationInfoList {


    /**
     * content : [{"id":"41b477a4-0ced-4dc5-bba5-0becd8155990","from":null,"to":"3df7422-e211t009-2321-45sg5795h","content":"温馨提示，您有待办事宜","createdTime":"2019-02-25T16:45:52","lastUpdatedTime":"2019-02-25T16:45:52"}]
     * totalElements : 1
     * last : true
     * totalPages : 1
     * number : 0
     * size : 10
     * sort : [{"direction":"ASC","property":"createdTime","ignoreCase":false,"nullHandling":"NATIVE","ascending":true,"descending":false}]
     * numberOfElements : 1
     * first : true
     */

    var totalElements: Int = 0
    var last: Boolean = false
    var totalPages: Int = 0
    var number: Int = 0
    var size: Int = 0
    var numberOfElements: Int = 0
    var first: Boolean = false
    var content: List<ContentBean>? = null
    var sort: List<SortBean>? = null

    class ContentBean {
        /**
         * id : 41b477a4-0ced-4dc5-bba5-0becd8155990
         * from : null
         * to : 3df7422-e211t009-2321-45sg5795h
         * content : 温馨提示，您有待办事宜
         * createdTime : 2019-02-25T16:45:52
         * lastUpdatedTime : 2019-02-25T16:45:52
         */

        var id: String? = null
        var from: Any? = null
        var to: String? = null
        var content: String? = null
        var createdTime: String? = null
        var lastUpdatedTime: String? = null
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
