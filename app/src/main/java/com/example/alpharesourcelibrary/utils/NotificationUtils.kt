package com.movieplayer.android.utils

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.util.Patterns
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.alpharesourcelibrary.MapsActivity
import com.example.alpharesourcelibrary.R
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class NotificationUtils(private val context: Context) {

    private var mNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null
    private val notificationChannelID: String = context.applicationContext.packageName
    private val notificationChannelName: String = context.applicationContext.packageName

    private val notificationManager: NotificationManager?
        get() {
            if (mNotificationManager == null) {
                mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val mChannel = NotificationChannel(
                        notificationChannelID,
                        notificationChannelName,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    mChannel.enableLights(true)
                    mChannel.enableVibration(true)
                    mNotificationManager!!.createNotificationChannel(mChannel)
                }
            }
            return mNotificationManager
        }

    private val notificationId: Int
        get() = (Date().time / 1000L % Integer.MAX_VALUE).toInt()

    /*--------------------------------------------------
        BEFORE STARTED
    -------------------------------------------------------*/

    private fun buildNotification(NOTIFICATION_ID: Int) {
        notificationManager?.let {
            notificationManager!!.notify(NOTIFICATION_ID, mBuilder!!.build())
        }
    }

    private fun getLaunchIntent(notificationId: Int): PendingIntent {
        val intent = Intent(context, MapsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("notificationId", notificationId)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    /*--------------------------------------------------
        BIG PICTURE STYLE NOTIFICATION
      -------------------------------------------------------*/

    private fun bigPictureStyleNotification(
        title: String?,
        bitmap: Bitmap?
    ) {

        val NOTIFICATION_ID = notificationId
        mBuilder?.let {
            mBuilder!!.setContentTitle(title)
            mBuilder!!.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            mBuilder!!.setContentIntent(getLaunchIntent(NOTIFICATION_ID))
            buildNotification(NOTIFICATION_ID)
        }
    }


    /*--------------------------------------------------
        BIG TEXT STYLE NOTIFICATION
      -------------------------------------------------------*/

    private fun bigTextStyleNotification(
        title: String?,
        message: String?
    ) {

        val NOTIFICATION_ID = notificationId
        mBuilder?.let {

            mBuilder!!.setContentTitle(title)
            mBuilder!!.setStyle(NotificationCompat.BigTextStyle().bigText(message))
            mBuilder!!.setContentIntent(getLaunchIntent(NOTIFICATION_ID))
            buildNotification(NOTIFICATION_ID)
        }
    }

    /*--------------------------------------------------
        INBOX STYLE NOTIFICATION
      -------------------------------------------------------*/

    private fun inboxStyleNotification(
        title: String,
        summeryText: String,
        messageList: ArrayList<String>?
    ) {

        if (messageList == null || messageList.isEmpty())
            return

        val NOTIFICATION_ID = notificationId

        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setSummaryText(summeryText)
        for (singleMsg in messageList) {
            inboxStyle.addLine(singleMsg)
        }
        mBuilder!!.setContentTitle(title)
        mBuilder!!.setStyle(inboxStyle)
        mBuilder!!.setContentIntent(getLaunchIntent(NOTIFICATION_ID))

        buildNotification(NOTIFICATION_ID)
    }

    /*--------------------------------------------------
        SHOW NOTIFICATION PUBLIC
     -------------------------------------------------------*/

    fun showNotification(
        smallIcon: Int,
        title: String?,
        message: String?,
        imageUrl: String?
    ) {
        if (message == null || message.isEmpty())
            return

        mBuilder = NotificationCompat.Builder(context.applicationContext, notificationChannelID)
        mBuilder!!.setSmallIcon(smallIcon)
        mBuilder!!.setAutoCancel(true)
        mBuilder!!.color = ContextCompat.getColor(context, R.color.material_on_primary_emphasis_high_type)
        mBuilder!!.setLargeIcon(
            BitmapFactory.decodeResource(
                context.resources,
                R.mipmap.ic_launcher
            )
        )


        if (imageUrl != null && imageUrl.length > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

            val imageDownloader = ImageDownloader(imageUrl, object : ImageDownloadListener {

                override fun onDownloadedImage(bitmap: Bitmap?) {
                    if (bitmap != null) {
                        bigPictureStyleNotification(title, bitmap)
                    } else {
                        bigTextStyleNotification(title, message)
                    }
                }
            })
            imageDownloader.execute()
        } else {
            bigTextStyleNotification(title, message)
        }
    }

    internal fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            if (componentInfo != null) {
                if (componentInfo.packageName == context.packageName) {
                    isInBackground = false
                }
            }
        }

        return isInBackground
    }


    internal class ImageDownloader(private val imageUrl: String, private val downloadListener: ImageDownloadListener) :
        AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg params: String): Bitmap? {
            try {
                val url = URL(imageUrl)
                val inputStream: InputStream
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                inputStream = connection.inputStream
                return BitmapFactory.decodeStream(inputStream)

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(bitmap: Bitmap) {
            downloadListener.onDownloadedImage(bitmap)
        }
    }

    interface ImageDownloadListener {

        fun onDownloadedImage(bitmap: Bitmap?)
    }

}
