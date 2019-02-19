package com.gkzxhn.helpout.model.iml

import com.gkzxhn.helpout.model.IBaseModel


import java.util.*


/**
 * Explanation:
 * @author LSX
 *    -----2018/9/7
 */
open class BaseModel : IBaseModel {
    val REQUEST_TAG = UUID.randomUUID().toString().replace("-", "")
}
