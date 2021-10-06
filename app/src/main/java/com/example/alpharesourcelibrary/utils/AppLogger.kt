package com.movieplayer.android.utils

import android.util.Log

class AppLogger {

    private val TAG = "SHISHIR_13"

    /** DEBUG type log */

    fun logD(message: String) {
        logD(TAG, message)
    }

    fun logD(TAG: String, message: String) {
        if (AppUtils.sharedInstance.isDebug) {
            Log.d(TAG, message)
        }
    }

    fun <T> logD(className: Class<T>, message: String) {
        logD(className.simpleName, message)
    }


    /** Verbose type log */

    fun logV(message: String) {
        logV(TAG, message)
    }


    fun logV(TAG: String, message: String) {
        if (AppUtils.sharedInstance.isDebug) {
            Log.v(TAG, message)
        }
    }

    fun <T> logV(className: Class<T>, message: String) {
        logV(className.simpleName, message)
    }


    /** ERROR type log = RED COLOR */

    fun logE(message: String) {
        logE(TAG, message)
    }


    fun logE(TAG: String, message: String) {
        if (AppUtils.sharedInstance.isDebug) {
            Log.e(TAG, message)
        }
    }

    fun <T> logE(className: Class<T>, message: String) {
        logE(className.simpleName, message)
    }

    /** WARNING type log = RED COLOR */

    fun logW(message: String) {
        logW(TAG, message)
    }


    fun logW(TAG: String, message: String) {
        if (AppUtils.sharedInstance.isDebug) {
            Log.w(TAG, message)
        }
    }

    fun <T> logW(className: Class<T>, message: String) {
        logW(className.simpleName, message)
    }

    /** INFO type log = RED COLOR */

    fun logI(message: String) {
        logI(TAG, message)
    }


    fun logI(TAG: String, message: String) {
        if (AppUtils.sharedInstance.isDebug) {
            Log.i(TAG, message)
        }
    }

    fun <T> logI(className: Class<T>, message: String) {
        logI(className.simpleName, message)
    }
}
