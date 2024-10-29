package com.example.medicin_app_v2.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
class UsageWorker(
    ctx: Context,
    params: WorkerParameters,
    private val usageRepository: UsageRepository,
    private val scheduleRepository : ScheduleRepository,
    val medicinRepository: MedicinRepository,
    val scheduleTermRepository: ScheduleTermRepository
) : CoroutineWorker(ctx, params)  {
    override suspend fun doWork(): Result {
        val allSchedules = scheduleRepository.getAllSchedules().first()
            .let { PatientScheduleInfo(it) }// Poczekaj na pierwszy wynik z Flow

        val scheduleDetails = PatientScheduleDetailsInfo(
            allSchedules.scheduleList.map{
                it.toScheduleDetails(
                    medicinDetails = medicinRepository.getMedicineStream(it.Medicine_id)
                        .filterNotNull()
                        .first()
                        .toMedicinDetails(),
                    scheduleTermList = scheduleTermRepository.getAllsSchedulesTerms(it.id)
                        .filterNotNull().first()
                )
            }
        )

        return try {

            genereteUsagesForNextPeriodOfTime(
                scheduleDetailsList =  scheduleDetails.scheduleDetailsList,
                days =7
            )
            Result.success()
        }catch(throwable: Throwable) {
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
                        if(eventDate > scheduleDetail.startDate && eventDate<= scheduleDetail.endDate) {
                            if (isValidEventDay(scheduleTerm, calendar)) {
                                calendar.set(Calendar.HOUR_OF_DAY, scheduleTerm.hour)
                                calendar.set(Calendar.MINUTE, scheduleTerm.minute)
                                val eventTime = calendar.time
                                Log.i("usageeee", "id scheduleTerm = ${scheduleTerm.id}")
                                val usage = Usage(
                                    id = 0,
                                    ScheduleTerm_id = scheduleTerm.id,
                                    confirmed = false,
                                    date = eventTime
                                )
                                Log.i(
                                    "usageeee",
                                    " Create usage scheudle Term = ${usage.ScheduleTerm_id}"
                                )
                                val id = usageRepository.insert(usage)
                                Log.i("usageeee", " id created = $id")
                            }
                        }
                    }
                }

        }
    }

    private fun isValidEventDay(scheduleTerm: ScheduleTermDetails, calendar: Calendar): Boolean {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)  // Zwraca dzień tygodnia: 1 = Niedziela, 7 = Sobota
        Log.i("usageeee", "isValidDay  == scheudleDay: $(${scheduleTerm.day.weekDay}) ==? ${dayOfWeek}")
        return scheduleTerm.day.weekDay == dayOfWeek  // Załóżmy, że 'dayOfWeek' w harmonogramie jest zgodne z `Calendar`
    }


}