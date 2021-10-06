package com.movieplayer.android.utils

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.PendingIntent
import android.app.Service
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.text.Html
import android.text.Spanned
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.pm.PackageInfoCompat
import com.example.alpharesourcelibrary.BuildConfig
import com.example.alpharesourcelibrary.utils.AlertService

import java.util.*

class AppUtils private constructor() {

    companion object {
        val sharedInstance by lazy {
            AppUtils()
        }
    }

    val isDebug: Boolean
        get() = BuildConfig.DEBUG


    fun getCurentAppVersion(@NonNull context: Context): Int {
        var currentAppVersion = 0
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val longVersionCode = PackageInfoCompat.getLongVersionCode(pInfo)
            currentAppVersion = longVersionCode.toInt()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return currentAppVersion
    }

    fun getCurrentAppVersionName(context: Context): String {
        return context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }


    fun openAppOnPlayStore(@NonNull context: Context, packageName: String = context.applicationContext.packageName) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    @SuppressLint("HardwareIds")
    fun getDeviceUniqueId(@NonNull context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }


    /**
     * You'll need the following permission in your AndroidManifest.xml:
     *
     *         <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     *
     * From Android 10: Its is restricted for user apps to get non resettable hardware identifiers like IMEI
     *
     * **/
    @SuppressLint("HardwareIds")
    fun getIMEIorDeviceUniqueId(mContext: Context): String {
        var imei: String? = null
        val telephonyManager = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            imei = when {
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> telephonyManager.deviceId
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P -> telephonyManager.imei // From Android 10, IMEI is restricted.
                else -> getDeviceUniqueId(mContext) // If Imei not found then we are using Device Id
            }
        }
        return imei ?: getDeviceUniqueId(mContext) // If imei not found then we are using device Unique Id
    }


    fun clearAllFilesFromCache(mContext: Context) {
        val cacheDir = mContext.cacheDir
        val files = cacheDir.listFiles()
        if (files != null) {
            for (file in files)
                file.delete()
        }
    }

    fun getAppLauncherActivity(@NonNull context: Context): String {
        val intent = context.packageManager.getLaunchIntentForPackage(context.applicationContext.packageName)
        val activityList = context.packageManager.queryIntentActivities(intent!!, 0)
        return activityList[0].activityInfo.name
    }


    @SuppressWarnings("deprecation")
    fun <S : Service> isServiceRunning(
        context: Context,
        serviceClass: Class<S>
    ): Boolean {
        val activityManager = context.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Integer.MAX_VALUE)

        for (runningServiceInfo in runningServices) {
            if (serviceClass.name == runningServiceInfo.service.className) return true
        }
        return false
    }

    @SuppressWarnings("deprecation")
    fun setLocale(context: Context, languageCode: String = "en") {
        val locale = Locale(languageCode)
        val config = Configuration()
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) config.setLocale(locale) else config.locale = locale
    }


    fun dial(context: Context, phoneNumber: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:$phoneNumber")
        context.startActivity(dialIntent)
    }

    @SuppressLint("MissingPermission")
    fun call(context: Context, phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        context.startActivity(callIntent)
    }

    fun openCamera(context: Context, REQUEST_CODE: Int) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(context.packageManager) != null) {
            (context as AppCompatActivity).startActivityForResult(cameraIntent, REQUEST_CODE)
        }
    }

    fun openCamera(context: Context, imageUri: Uri, REQUEST_CODE: Int) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        if (cameraIntent.resolveActivity(context.packageManager) != null) {
            (context as AppCompatActivity).startActivityForResult(cameraIntent, REQUEST_CODE)
        }
    }

    fun openGallery(context: Context, REQUEST_CODE: Int) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        (context as AppCompatActivity).startActivityForResult(galleryIntent, REQUEST_CODE)
    }

    fun goToSettings(context: Context) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.parse("package:" + context.applicationContext.packageName)
        context.startActivity(intent)
    }

    fun openUrlOnExternalBrowser(context: Context, link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        context.startActivity(browserIntent)
    }

    fun openFacebookPage(context: Context, FB_PAGE_URL: String) {
        try {
            context.packageManager.getPackageInfo("com.facebook.katana", 0)
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=$FB_PAGE_URL")))
        } catch (e: Exception) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(FB_PAGE_URL)))
        }
    }


    @SuppressWarnings("deprecation")
    fun fromHtml(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
            // we are using this flag to give a consistent behaviour
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }

    fun showIntegerValueWithAnim(textView: TextView, value: Int) {
        val animator: ValueAnimator = ValueAnimator.ofInt(0, value)
        animator.duration = 500
        animator.addUpdateListener {
            textView.text = "${animator.animatedValue}"
        }
        animator.start()
    }


    fun sendSMS(
        mContext: Context,
        phoneNo: String,
        message: String
    ) {
        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"
        try {
            val sentPI = PendingIntent.getBroadcast(mContext, 0, Intent(SENT), 0)
            val deliveredPI = PendingIntent.getBroadcast(mContext, 0, Intent(DELIVERED), 0)

            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNo, null, message, sentPI, deliveredPI)
//            AlertService(mContext).showAlert(mContext, null, "আপনার জরুরী ম্যাসেজটি পাঠানো হয়েছে।")
        } catch (ex: Exception) {
            mContext.showToast("${ex.message}")
        }
    }


    fun getInBangla(string: String): String {
        val banglaNumber = arrayOf('১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯', '০')
        val engNumber = arrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0')
        var values = ""
        val character = string.toCharArray()
        for (i in character.indices) {
            var c: Char? = ' '
            for (j in engNumber.indices) {
                if (character[i] == engNumber[j]) {
                    c = banglaNumber[j]
                    break
                } else {
                    c = character[i]
                }
            }
            values += c!!
        }
        return values
    }
}

/**
 * 1.  No permission needed for ACTION_DIAL but permission needed for ACTION_CALL
 *
 * */