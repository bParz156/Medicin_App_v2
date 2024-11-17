package com.example.medicin_app_v2.data

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.medicin_app_v2.ui.home.DateAsLongSerializer
import com.example.medicin_app_v2.ui.home.UsageDetails
import com.example.medicin_app_v2.workers.DeleteUsageWorker
import com.example.medicin_app_v2.workers.NotificationWorker
import com.example.medicin_app_v2.workers.UsageWorker
import com.google.firebase.dataconnect.serializers.DateSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import com.google.gson.Gson

//private const val TAG = "UsageWorker"
private const val TAG = "NotificationWorker"
const val USAGE_DETAILS_LIST_KEY = "usageDetailsList"
class WorkManagerRepository(context: Context,
    ) : WorkerRepository {

    private val workManager = WorkManager.getInstance(context)
    //override val outputWorkInfo: Flow<WorkInfo?> = MutableStateFlow(null)

    override fun generateUsages() {
        Log.i(TAG, "in repositori")
       val usageBuilder = PeriodicWorkRequestBuilder<UsageWorker>(2, TimeUnit.MINUTES).build()
     //  val usageBuilder = OneTimeWorkRequestBuilder<UsageWorker>()

//     workManager.enqueue(usageBuilder.build())
        workManager.enqueueUniquePeriodicWork(
            "UsageWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            usageBuilder
        )
    }

    override fun deleteAncient() {
        val deleteBuilder = OneTimeWorkRequestBuilder<DeleteUsageWorker>()
        workManager.enqueue(deleteBuilder.build())

        val deleteBuilderPeriodic = PeriodicWorkRequestBuilder<DeleteUsageWorker>(1, TimeUnit.DAYS).build()
        workManager.enqueueUniquePeriodicWork(
            "DeleteUsageWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            deleteBuilderPeriodic
        )

    }

    override fun generateNotifications(usageDetailsList: List<UsageDetails>) {
        Log.i(TAG, "generateNotifications")
        val notificationBuilder = OneTimeWorkRequestBuilder<NotificationWorker>()
        Log.i(TAG, "przed setInpit")
        notificationBuilder.setInputData(createInputDataForNotificationWorker(usageDetailsList))
        Log.i(TAG, "po setInpit")
        workManager.enqueue(notificationBuilder.build())
    }

    private fun createInputDataForNotificationWorker(usageDetailsList: List<UsageDetails>) : Data {
        val builder = Data.Builder()

        Log.i(TAG, "przed zamiana na json: $usageDetailsList")
       // Log.i(TAG, "zamian daty: ${Json.encodeToString(Date(1029,5,1))}")

//        val df = SimpleDateFormat("yyyy-MM-ddX")
//        val data = ProgrammingLanguage("Kotlin", listOf(df.parse("2023-07-06+00"), df.parse("2023-04-25+00"), df.parse("2022-12-28+00")))
//        println(Json.encodeToString(data))
        //Log.i(TAG, "opierwszy: ${Json.encodeToString(usageDetailsList[0])}")
        val gson = Gson()

        //val jsonList = usageDetailsList.map { Json.encodeToString(it) }
        val jsonList = usageDetailsList.map { gson.toJson(it) }
        Log.i(TAG, "po json")

        builder.putStringArray(USAGE_DETAILS_LIST_KEY, jsonList.toTypedArray())
        Log.i(TAG, "ustawienie stringArray")
        return builder.build()
    }
}

//@kotlinx.serialization.Serializable
//class ProgrammingLanguage(
//    val name: String,
//    val releaseDates: List<@Serializable(DateAsLongSerializer::class) Date>
//)
