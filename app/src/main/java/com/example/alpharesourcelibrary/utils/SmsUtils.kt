package com.movieplayer.android.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import com.example.alpharesourcelibrary.utils.AlertService

class SmsUtils(val mContext: Context) {

    private val SENT = "${mContext.applicationContext.packageName}SMS_SENT"
    private val DELIVERED = "${mContext.applicationContext.packageName}SMS_DELIVERED"
    private val alertService = AlertService(mContext)

    fun sendSMS(
        mContext: Context,
        phoneNo: String,
        message: String
    ) {
        try {
            // Creating Pending Intent
            val sentPI = PendingIntent.getBroadcast(mContext, 0, Intent(SENT), 0)
            val deliveredPI = PendingIntent.getBroadcast(mContext, 0, Intent(DELIVERED), 0)

            // Sending Sms
            val smsManager: SmsManager = SmsManager.getDefault()
            //smsManager.sendTextMessage(phoneNo, null, messageInfo, sentPI, deliveredPI)
            val arrSMS = smsManager.divideMessage(message)
            smsManager.sendMultipartTextMessage(phoneNo, null, arrSMS, arrayListOf(sentPI), arrayListOf(deliveredPI))
        } catch (ex: Exception) {
            //
        }
    }


    // Call this method in onStart of the Activity..
    fun registerSentDeliveredReceiver() {
        mContext.registerReceiver(sentBroadCastReceiver, IntentFilter(SENT))
        mContext.registerReceiver(deliveredBroadCastReceiver, IntentFilter(DELIVERED))
    }

    // Call this method in onStop of the activity...
    fun unRegisterSentDeliveredReceiver() {
        mContext.unregisterReceiver(sentBroadCastReceiver)
        mContext.unregisterReceiver(deliveredBroadCastReceiver)
    }


    private val sentBroadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    // Show success Message for sending sms
                }
                SmsManager.RESULT_ERROR_GENERIC_FAILURE -> mContext.showToast("FAILED !")
                SmsManager.RESULT_ERROR_RADIO_OFF -> mContext.showToast("FAILED !")
                SmsManager.RESULT_ERROR_NULL_PDU -> mContext.showToast("FAILED !")
                SmsManager.RESULT_ERROR_NO_SERVICE -> mContext.showToast("NO SERVICE")
            }
        }
    }

    private val deliveredBroadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (resultCode) {
                // Activity.RESULT_OK -> mContext.showToast("DELIVERED")
                // Activity.RESULT_CANCELED -> mContext.showToast("FAILED TO DELIVER !")
            }
        }
    }
}
/**
 *  SendSms is only for English message
 *  SendMultiPart Sms is using here because it can send both english and bengali sms...
 * */