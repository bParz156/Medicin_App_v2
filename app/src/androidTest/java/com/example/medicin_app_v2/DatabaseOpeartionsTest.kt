package com.example.medicin_app_v2

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medicin_app_v2.data.AppDatabase
import com.example.medicin_app_v2.data.DayWeek
import com.example.medicin_app_v2.data.MealRelation
import com.example.medicin_app_v2.data.MedicinForm
import com.example.medicin_app_v2.data.examination.ExaminationDao
import com.example.medicin_app_v2.data.firstAidKit.FirstAidKitDao
import com.example.medicin_app_v2.data.medicine.Medicine
import com.example.medicin_app_v2.data.medicine.MedicineDao
import com.example.medicin_app_v2.data.notification.NotificationDao
import com.example.medicin_app_v2.data.patient.Patient
import com.example.medicin_app_v2.data.patient.PatientDao
import com.example.medicin_app_v2.data.schedule.ScheduleDao
import com.example.medicin_app_v2.data.scheduleTerms.ScheduleTermDao
import com.example.medicin_app_v2.data.storage.Storage
import com.example.medicin_app_v2.data.storage.StorageDao
import com.example.medicin_app_v2.data.usage.UsageDao
import com.example.medicin_app_v2.ui.home.MedicinDetails
import com.example.medicin_app_v2.ui.home.ScheduleDetails
import com.example.medicin_app_v2.ui.home.ScheduleTermDetails
import com.example.medicin_app_v2.ui.home.toMedicin
import com.example.medicin_app_v2.ui.home.toSchedule
import com.example.medicin_app_v2.ui.patients.PatientDetails
import com.example.medicin_app_v2.ui.patients.toPatient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

@RunWith(AndroidJUnit4::class)
class DatabaseOpeartionsTest {

    private lateinit var db: AppDatabase
    private lateinit var patientDao: PatientDao
    private lateinit var medicineDao: MedicineDao
    private lateinit var storageDao: StorageDao
    private lateinit var firstAidKitDao: FirstAidKitDao
    private lateinit var scheduleDao: ScheduleDao
    private lateinit var usageDao: UsageDao
    private lateinit var notificationDao: NotificationDao
    private lateinit var scheduleTermDao: ScheduleTermDao
    private lateinit var examinationDao : ExaminationDao


    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).setJournalMode(RoomDatabase.JournalMode.TRUNCATE).build()
        usageDao = db.usageDao()
        patientDao = db.patientDao()
        medicineDao=db.medicineDao()
        storageDao=db.storageDao()
        firstAidKitDao=db.firstAidKitDao()
        scheduleDao=db.scheduleDao()
        notificationDao=db.notificationDao()
        scheduleTermDao=db.scheduleTermDao()
        examinationDao=db.examinationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }


    @Test
    @Throws(Exception::class)
     fun patientDAOperations() = runTest {
         val name = "Imie Pacjenta"
        val patient : Patient = PatientDetails(name = name).toPatient()
        val patientdId = patientDao.insert(patient)
        var byName = patientDao.getPatientByName(name = name).first()
        assertNotNull(byName)
        assert(patientdId.toInt() == byName.id)
        var byId = patientDao.getpatient(patientdId.toInt()).first()
        assertNotNull(byId)
        assert(name == byId.name)
        patientDao.delete(byName)
        byId = patientDao.getpatient(patientdId.toInt()).first()
        byName = patientDao.getPatientByName(name = name).first()
        assertNull(byId)
        assertNull(byName)
    }

    @Test
    fun medicineANDstorageDAOperations() = runTest {
        val name = "Name"
        val relation = MealRelation.Nie
        val medicinForm = MedicinForm.CIECZ
        val quantity = 15
        val id = medicineDao.insert(MedicinDetails(name = name, form = medicinForm, relation = relation).toMedicin()).toInt()
        val storageId = storageDao.insert(Storage(Medicineid = id, quantity = quantity)).toInt()
        val medicineById = medicineDao.getmedicine(id).first()
        val medicineByGet = medicineDao.getmedicine(name= name, form = medicinForm).first()
        assertEquals(medicineById, medicineByGet)
        val storagesList = storageDao.getAllMedicinesStorages(id).first()
        val storage = storageDao.getStorage(storageId).first()
        assert(storagesList.isNotEmpty())
        assert(storagesList.contains(storage))
        medicineDao.delete(Medicine(id =id, name = name, form = medicinForm, mealRelation = relation))
        assertNull(medicineDao.getmedicine(id).first())
        assert(storageDao.getAllMedicinesStorages(id).first().isEmpty())
    }

    @Test
    fun scheduleTermDAO() = runTest {
        val name = "Name"
        val relation = MealRelation.Nie
        val medicinForm = MedicinForm.CIECZ

        val medicinId = medicineDao.insert(MedicinDetails(name = name, form = medicinForm, relation = relation).toMedicin()).toInt()

        var patient : Patient = PatientDetails(name = name).toPatient()
        val patientdId = patientDao.insert(patient).toInt()
        patient = patient.copy(id = patientdId)

        var term1 = ScheduleTermDetails(
            day = DayWeek.CZ, hour = 7, minute =0, dose = 1
        )
        var term2 = ScheduleTermDetails(
            day = DayWeek.PT, hour = 7, minute =0, dose = 1
        )

        var scheduleDetails = ScheduleDetails(
            medicinDetails = MedicinDetails(id = medicinId, name = name, form = medicinForm, relation = relation),
            startDate = Date(),
            patiendId = patientdId,
            scheduleTermDetailsList = listOf(term1, term2)
        )

        val scheduleId = scheduleDao.insert(scheduleDetails.toSchedule(patientdId)).toInt()
        scheduleDetails = scheduleDetails.copy(id = scheduleId)
        var termId = scheduleTermDao.insert(term1.toScheduleTerm(scheduleId)).toInt()
        term1=term1.copy(id = termId)
        termId = scheduleTermDao.insert(term2.toScheduleTerm(scheduleId)).toInt()
        term2=term2.copy(id = termId)

        var scheduleFromDB = scheduleDao.getPatientMedcicineSchedule(patient_id = patientdId, medicine_id = medicinId).first()
        assertEquals(scheduleFromDB, scheduleDetails.toSchedule(patientdId))

        var listSchedulesTerm = scheduleTermDao.getScheduleTermBySchedule(scheduleId).first()
        assertEquals(2, listSchedulesTerm.size)
        assert(listSchedulesTerm.contains(term1.toScheduleTerm(scheduleId)) && listSchedulesTerm.contains(term2.toScheduleTerm(scheduleId)))
        scheduleTermDao.delete(term1.toScheduleTerm(scheduleId))
        listSchedulesTerm = scheduleTermDao.getScheduleTermBySchedule(scheduleId).first()
        assertEquals(1, listSchedulesTerm.size)

        scheduleDetails = scheduleDetails.copy(endDate = Date(2024- 1900,12,25))

        scheduleDao.update(scheduleDetails.toSchedule(patientdId))
//update schedule nie ma wpływu na tabelę scheduleTerm
        listSchedulesTerm = scheduleTermDao.getScheduleTermBySchedule(scheduleId).first()
        assertEquals(1, listSchedulesTerm.size)
        assert(listSchedulesTerm.contains(term2.toScheduleTerm(scheduleId)))


        patientDao.delete(patient)
        scheduleFromDB = scheduleDao.getPatientMedcicineSchedule(patient_id = patientdId, medicine_id = medicinId).first()
        assertNull(scheduleFromDB)
        listSchedulesTerm = scheduleTermDao.getScheduleTermBySchedule(scheduleId).first()
        assert(listSchedulesTerm.isEmpty())
    }

}