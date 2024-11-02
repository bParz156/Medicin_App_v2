package com.example.medicin_app_v2.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.medicin_app_v2.data.AppDatabase
import com.example.medicin_app_v2.data.medicine.MedicinRepository
import com.example.medicin_app_v2.data.schedule.ScheduleRepository
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTermRepository
import com.example.medicin_app_v2.data.usage.Usage
import com.example.medicin_app_v2.data.usage.UsageRepository
import com.example.medicin_app_v2.ui.home.PatientScheduleDetailsInfo
import com.example.medicin_app_v2.ui.home.PatientScheduleInfo
import com.example.medicin_app_v2.ui.home.ScheduleDetails
import com.example.medicin_app_v2.ui.home.ScheduleTermDetails
import com.example.medicin_app_v2.ui.home.toMedicinDetails
import com.example.medicin_app_v2.ui.home.toScheduleDetails
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import java.util.Calendar


private const val TAG = "UsageWorker"
class UsageWorker (
ctx: Context,
    params: WorkerParameters,
//    private val usageRepository: UsageRepository,
//    private val scheduleRepository : ScheduleRepository,
//    val medicinRepository: MedicinRepository,
//    val scheduleTermRepository: ScheduleTermRepository
) : CoroutineWorker(ctx, params)  {

    private val usageDao  = AppDatabase.getDatabase(ctx).usageDao()
    private val scheduleDao = AppDatabase.getDatabase(ctx).scheduleDao()
    val medicinDao= AppDatabase.getDatabase(ctx).medicineDao()
    val scheduleTermDao = AppDatabase.getDatabase(ctx).scheduleTermDao()

    override suspend fun doWork(): Result {
        val allSchedules = scheduleDao.getAllSchedules().first()
            .let { PatientScheduleInfo(it) }// Poczekaj na pierwszy wynik z Flow

        val scheduleDetails = PatientScheduleDetailsInfo(
            allSchedules.scheduleList.map{
                it.toScheduleDetails(
                    medicinDetails =  medicinDao.getmedicine(it.Medicine_id)
//                    medicinDetails = medicinRepository.getMedicineStream(it.Medicine_id)
                        .filterNotNull()
                        .first()
                        .toMedicinDetails(),
                    scheduleTermList =  scheduleTermDao.getScheduleTermBySchedule(it.id)
//                    scheduleTermList = scheduleTermRepository.getAllsSchedulesTerms(it.id)
                        .filterNotNull()
                        .first()
                )
            }
        )

        return try {

            genereteUsagesForNextPeriodOfTime(
                scheduleDetailsList =  scheduleDetails.scheduleDetailsList,
                days =7
            )
            Result.success()
        }catch(e: Exception) {
            Log.e(TAG, "failed due to :", e)
            Result.failure()
        }
    }


    suspend fun genereteUsagesForNextPeriodOfTime(
        scheduleDetailsList: List<ScheduleDetails>,
        days: Int
    ) {
        val calendar = Calendar.getInstance() // Bieżąca data
        val currentDate = calendar.time

            for (scheduleDetail in scheduleDetailsList) {
                for (scheduleTerm in scheduleDetail.scheduleTermDetailsList) {
                    for (dayOffset in 0..days) {
                        calendar.time = currentDate
                        calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
                        val eventDate = calendar.time
                        if(eventDate > scheduleDetail.startDate && (scheduleDetail.endDate ==null || eventDate<= scheduleDetail.endDate)) {
                            if (isValidEventDay(scheduleTerm, calendar)) {
                                calendar.set(Calendar.HOUR_OF_DAY, scheduleTerm.hour)
                                calendar.set(Calendar.MINUTE, scheduleTerm.minute)
                                val eventTime = calendar.time
                               val usage = Usage(
                                    id = 0,
                                    ScheduleTerm_id = scheduleTerm.id,
                                    confirmed = false,
                                    date = eventTime
                                )
                                Log.i(TAG, "bylo?")
                                if(usageDao.getUsage(scheduleTerm_id = scheduleTerm.id, useDate = eventTime).first() ==null) {
                                    Log.i(TAG, "nie bylo")
                                    val id = usageDao.insert(usage)
                                }
                            }
                        }
                    }
                }

        }
    }

    private fun isValidEventDay(scheduleTerm: ScheduleTermDetails, calendar: Calendar): Boolean {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)  // Zwraca dzień tygodnia: 1 = Niedziela, 7 = Sobota
       // Log.i(TAG, "isValidDay  == scheudleDay: $(${scheduleTerm.day.weekDay}) ==? ${dayOfWeek}")
        return scheduleTerm.day.weekDay == dayOfWeek  // Załóżmy, że 'dayOfWeek' w harmonogramie jest zgodne z `Calendar`
    }


}