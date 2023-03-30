package com.hocel.cvgenie.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager

fun downloadFile(
    mContext: Context,
    fileName: String,
    fileExtension: String,
    destinationDirectory: String,
    uri: Uri
) {
    val downloadManager: DownloadManager =
        mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val request = DownloadManager.Request(uri)
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request.setDestinationInExternalPublicDir(
        destinationDirectory,
        fileName + fileExtension
    )
    val downloadId = downloadManager.enqueue(request)
    val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadId) {
                "Download is complete".toast(mContext, Toast.LENGTH_SHORT)
            }
        }
    }
    LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, filter)
}