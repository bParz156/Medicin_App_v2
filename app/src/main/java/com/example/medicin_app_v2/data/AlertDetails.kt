package com.example.medicin_app_v2.data

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView

class AlertDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("AlertDetails", "in here")

        // Utwórz główny układ (np. LinearLayout)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            //padding = 32
        }

        // Dodaj TextView, aby wyświetlić informacje
        val notificationId = intent.getIntExtra("notification_id", -1)
        Log.i("AlertDetails", "id notification: $notificationId")
        val textView = TextView(this).apply {
            text = "Notification ID: $notificationId"
            textSize = 20f
        }

        layout.addView(textView) // Dodaj TextView do głównego układu

        // Ustaw główny układ jako widok aktywności
        setContentView(layout)
    }
}