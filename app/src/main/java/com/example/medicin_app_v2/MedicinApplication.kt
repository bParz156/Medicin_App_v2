package com.example.medicin_app_v2

import android.app.Application
import com.example.medicin_app_v2.data.AppContainer
import com.example.medicin_app_v2.data.AppDataContainer

class MedicinApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
