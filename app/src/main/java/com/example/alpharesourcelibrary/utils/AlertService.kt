package com.example.alpharesourcelibrary.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.widget.Toast

class AlertService(private val context: Context) {

    private var mToast: Toast? = null
    private var mAlert: AlertDialog? = null
    private var mConfirmationAlert: AlertDialog? = null

//    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
//        showToast(context, message, duration)
//    }
//
//    fun showToast(context: Context, resId: Int, duration: Int = Toast.LENGTH_SHORT) {
//        showToast(context, context.getString(resId), duration)
//    }

//    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
//        if (mToast != null) {
//            if (mToast!!.view?.isShown) mToast?.setText(message) else createToast(context, message, duration)
//        } else
//            createToast(context, message, duration)
//    }

    private fun createToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        mToast = Toast.makeText(context, message, duration)
        mToast?.show()
    }

//    fun showAlert(
//        context: Context,
//        title: String? = null,
//        message: String
//    ) {
//        if (mAlert != null && mAlert!!.isShowing) {
//            mAlert!!.dismiss()
//        }
//        mAlert = AlertDialog.Builder(context)
//            .setTitle(title)
//            .setMessage(message)
//            .setCancelable(false)
//            .setPositiveButton(context.getString(R.string.okay), null)
//            .create()
//        mAlert!!.show()
//    }

    fun showConfirmationAlert(
        context: Context,
        title: String? = null,
        message: String,
        negativeBtn: String? = null,
        positiveBtn: String,
        alertListener: AlertListener
    ) {
        if (mConfirmationAlert != null && mConfirmationAlert!!.isShowing) {
            mConfirmationAlert!!.dismiss()
        }

        mConfirmationAlert = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setNegativeButton(negativeBtn) { _, _ ->
                alertListener.negativeBtnDidTapped()
            }
            .setPositiveButton(positiveBtn) { _, _ ->
                alertListener.positiveBtnDidTapped()
            }
            .create()
        mConfirmationAlert!!.show()
    }

    interface AlertListener {

        fun negativeBtnDidTapped()

        fun positiveBtnDidTapped()
    }
}