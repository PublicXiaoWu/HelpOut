package com.gkzxhn.helpout.net

/**
 * @author LSX
 */

interface NetWorkCodeInfo {

    companion object {
        /****** 开发环境 ******/
        val ENVIRONMENT_DEV = 0x11
        /****** 测试环境 ******/
        val ENVIRONMENT_TEST = 0x12
        /****** 正式环境 ******/
        val ENVIRONMENT_FORMAL = 0x13

        /****** 更改这个 切换环境 ******/
        val environment = ENVIRONMENT_TEST

        /****** 公共平台 ******/
        val BASE_URL = when (environment) {
            /****** 开发环境 ******/
            ENVIRONMENT_DEV -> "http://192.168.0.230:8081"
            /****** 测试环境 ******/
            ENVIRONMENT_TEST -> "http://qa.api.auth.prisonpublic.com"
            /****** 正式环境 ******/
            ENVIRONMENT_FORMAL -> "http://api.auth.prisonpublic.com"
            else -> ""
        }
        /****** 帮帮忙 ******/
        val BASE_URL_PROJECT = when (environment) {
            /****** 开发环境 ******/
            ENVIRONMENT_DEV -> "http://192.168.0.230:8086"
            /****** 测试环境 ******/
            ENVIRONMENT_TEST -> "http://qa.api.legal.prisonpublic.com"
            /****** 正式环境 ******/
            ENVIRONMENT_FORMAL -> "http://api.legal.prisonpublic.com"
            else -> ""
        }
    }


}
