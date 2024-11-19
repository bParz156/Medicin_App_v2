package com.example.medicin_app_v2.workers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.work.impl.utils.ForceStopRunnable
import com.example.medicin_app_v2.data.WorkManagerRepository

@SuppressLint("RestrictedApi")
class BootReceiver : ForceStopRunnable.BroadcastReceiver() {
    @SuppressLint("RestrictedApi")
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                // Uruchom ponownie workery
                WorkManagerRepository(context).notificationStorage()
            }
        }
    }
}