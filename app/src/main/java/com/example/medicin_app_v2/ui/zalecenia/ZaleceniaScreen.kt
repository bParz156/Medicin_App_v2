package com.example.medicin_app_v2.ui.zalecenia

import android.app.TimePickerDialog
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.TimePicker
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role.Companion.Checkbox
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.data.DayWeek
import com.example.medicin_app_v2.data.MealRelation
import com.example.medicin_app_v2.data.MedicinForm
import com.example.medicin_app_v2.navigation.Location
import com.example.medicin_app_v2.navigation.NavigationDestination
import com.example.medicin_app_v2.ui.AppViewModelProvider
import com.example.medicin_app_v2.ui.ButtonIconRow
import com.example.medicin_app_v2.ui.CommunUI
import com.example.medicin_app_v2.ui.home.MedicinDetails
import com.example.medicin_app_v2.ui.home.ScheduleDetails
import com.example.medicin_app_v2.ui.home.ScheduleTermDetails
import com.example.medicin_app_v2.ui.magazyn.areYouSureDialog
import com.example.medicin_app_v2.ui.toPatientDetails
import kotlinx.coroutines.launch
import java.util.Date
import android.widget.ArrayAdapter
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExposedDropdownMenuBox
import com.example.medicin_app_v2.ui.ButtonIconColumn
import java.util.Calendar


object ZaleceniaDestination : NavigationDestination {
    override val route = "zalecenia"
    override val titleRes = R.string.zalecenia
    const val patientIdArg = "patientId"
    val routeWithArgs = "$route/{$patientIdArg}"

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZaleceniaScreen(
    viewModel: ZalecenieViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onButtonHomeClick: (Int) -> Unit,
    onButtonMagazynClicked: (Int) ->Unit,
    onButtonZaleceniaClicked: () ->Unit,
    onButtonPowiadomieniaClicked: () ->Unit,
    onButtonUstawieniaClicked: (Int) ->Unit,
    onButtonPatientClicked: (Int) ->Unit,
    modifier: Modifier = Modifier)
{

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {CommunUI(
            location = Location.ZALECENIA,
            onButtonHomeClick = {onButtonHomeClick(viewModel.patientUiState.patientDetails.id)},
            onButtonMagazynClicked = {onButtonMagazynClicked(viewModel.patientUiState.patientDetails.id)},
            onButtonZaleceniaClicked = onButtonZaleceniaClicked,
            onButtonUstawieniaClicked = {onButtonUstawieniaClicked(viewModel.patientUiState.patientDetails.id)},
            onButtonPowiadomieniaClicked = onButtonPowiadomieniaClicked,
            onButtonPatientClicked = {onButtonPatientClicked(viewModel.patientUiState.patientDetails.id)},
            patientsName = viewModel.getPatientsName(),
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        )}
    ) {  innerPadding ->

        ZaleceniaBody(
            viewModel = viewModel,
            contentPadding = innerPadding,
            scrollBehavior = scrollBehavior
        )


    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZaleceniaBody(modifier: Modifier = Modifier,
                  viewModel: ZalecenieViewModel,
                  scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                  contentPadding: PaddingValues = PaddingValues(0.dp)) {

    var openDialog  = remember { mutableStateOf(false)}
    val coroutineScope = rememberCoroutineScope()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(contentPadding),
    ) {

        if (viewModel.getPatientsName().isEmpty()) {
            Text(
                text = stringResource(R.string.no_patient),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )
        } else {

                Text(text="przyjmowane leki",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    )
                medicinRemainders(scheduleList =viewModel.patientsSchedule.scheduleDetailsList,//.toMedicinScheduleInfoList(),
                    onScheduleClick = { viewModel.updatetUiState(it)
                    },
                    onDeleteClick = {
                        coroutineScope.launch {
                            viewModel.deleteZalecenie()
                            viewModel.updateSchedulesInfo()
                        }
                        //TODO
                    },
                    onEditClick = {
//                        coroutineScope.launch {
//                            viewModel.updatetUiState(i)
//                        }
                        //TODO
                    }
                )

            ButtonIconRow(
                onButtonCLick = {openDialog.value = true},
                labelTextId =  R.string.add,
                isSelected = false,
                imageVector = Icons.Filled.Add
            )
            }

            if(openDialog.value)
            {
//                addNewMedicin(
//                    onAdd ={
//                        coroutineScope.launch {
//                            viewModel.createSchedule()
//                        }
//                    },
//                    onDismiss = {openDialog.value=false},
//                    scheduleDetails =  viewModel.scheduleUiState.scheduleDetails,
//                    storageDetails = viewModel.storageUiState.storageDetails,
//                    onValueStorageChange = viewModel::updatestorageState,
//                    onScheduleChange = viewModel::updatetUiState,
//                    )

                zalecenieDialog(
                    onConfirm =  {
                        coroutineScope.launch {
                            viewModel.createSchedule()
                        }
                    },
                    onDismiss = {openDialog.value=false},
                    scheduleDetails =  viewModel.scheduleUiState.scheduleDetails,
                    storageDetails = viewModel.storageUiState.storageDetails,
                    onValueStorageChange = viewModel::updatestorageState,
                    onScheduleChange = viewModel::updatetUiState,
                    viewModel = viewModel
                )
            }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun addNewMedicin(
    onAdd: () -> Unit,
    onDismiss: () -> Unit,
    scheduleDetails: ScheduleDetails,
    storageDetails: StorageDetails,
    onValueStorageChange: (StorageDetails) -> Unit,
    onScheduleChange: (ScheduleDetails) -> Unit,
   ) {

    var medicinName by remember { mutableStateOf(scheduleDetails.medicinDetails.name) }
    var medicinDose by remember { mutableStateOf("") }
    var medicinStore by remember { mutableStateOf(storageDetails.quantity.toString()) }
    var expanded by remember { mutableStateOf(false) }
    var selectedForm by remember { mutableStateOf(scheduleDetails.medicinDetails.form) }

    var selectedRelation by remember { mutableStateOf(scheduleDetails.medicinDetails.relation) }
    var expandedR by remember { mutableStateOf(false) }


    var selectedDays by remember { mutableStateOf(listOf<DayWeek>()) }
    var expandedDays by remember { mutableStateOf(false) }

    var timeADay by remember { mutableStateOf("") }

    val scheduleTermDetailsListPriv = mutableListOf<ScheduleTermDetails>()


    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
    )
    {
        Column(modifier = Modifier
            .wrapContentSize().fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Text(text = stringResource(R.string.add_medicin_title),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_large)))

            Text(text = stringResource(R.string.requiered_to_add_medicin),
                textAlign = TextAlign.Justify,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium)))

            TextField( value = medicinName,
                onValueChange = {medicinName = it
                    onScheduleChange(scheduleDetails.copy(medicinDetails= MedicinDetails(name =it)))}
                ,
                placeholder = { Text("Podaj nazwę leku")})

            Row()
            {
                Text(text="Wybierz formę leku")
                DropdownMenu(expanded = expanded,
                onDismissRequest = {expanded = false}
                )
                {
                    MedicinForm.values().forEach {
                        item ->
                        DropdownMenuItem(
                            text ={ Text(item.name)},
                            onClick = {
                                selectedForm = item
                                onScheduleChange(scheduleDetails.copy(medicinDetails = MedicinDetails( form = item)))
                                expanded = false
                            }
                        )
                    }
                }
                Button(onClick = {expanded=!expanded})
                {
                    Icon(imageVector = if(expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, contentDescription = null)
                }
            }

            TextField( value = medicinDose,
                onValueChange = { input ->
                    // Tylko liczby będą akceptowane
                    if (input.all { it.isDigit() }) {
                        medicinDose = input
                       // scheduleDetails = scheduleDetails.copy(dose = input.toInt())
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("Podaj dawkę leku")})

            TextField( value = medicinStore,
                onValueChange = { input ->
                    // Tylko liczby będą akceptowane
                    if (input.all { it.isDigit() }) {
                        medicinStore = input
                     onValueStorageChange(storageDetails.copy(quantity = input.toInt()))
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("Podaj jakim zapasem leku dysponujesz")})

            Row()
            {
                Text(text="Wybierz zależność leku z posiłkiem")
                DropdownMenu(expanded = expandedR,
                    onDismissRequest = {expandedR = false}
                )
                {
                    MealRelation.values().forEach {
                            item ->
                        DropdownMenuItem(
                            text ={ Text(item.name)},
                            onClick = {
                                selectedRelation = item
                                onScheduleChange(scheduleDetails.copy(medicinDetails = MedicinDetails( relation = item)))
                                expandedR = false
                            }
                        )
                    }
                }
                Button(onClick = {expandedR=!expandedR})
                {
                    Icon(imageVector = if(expandedR) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, contentDescription = null)
                }
            }

            Row()
            {
                Button(onClick = {expandedDays=!expandedDays})
                {
                    Icon(imageVector = if(expandedDays) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, contentDescription = null)
                }
                Text(text="Wybierz dni tygodnia" +
                        ":\n ${if (selectedDays.isEmpty()) "Żaden" else selectedDays.joinToString()}")
                DropdownMenu(expanded = expandedDays,
                    onDismissRequest = {expandedDays = false}
                )
                {
                    DayWeek.values().forEach {
                            option ->
                        val isSelected = selectedDays.contains(option)
                        DropdownMenuItem(
                            text ={
                                    Text(stringResource(option.title))
                            },
                            onClick = {
                                selectedDays = if(isSelected)
                                {
                                    selectedDays - option
                                }
                                else
                                {
                                    selectedDays + option
                                }
                            }
                        )
                    }
                }

            }

            TextField( value = timeADay,
                onValueChange = { input ->
                    // Tylko liczby będą akceptowane
                    if (input.all { it.isDigit() }) {
                        timeADay = input
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("Podaj ile razy dziennie przymowany ma być lek")})



            var openDialog by remember { mutableStateOf(false) }
            var allSet by remember { mutableStateOf(false) }
            var confirmed by remember { mutableStateOf(false) }

            NavigationBar(modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
            ) {
                NavigationBarItem(selected = false, onClick = onDismiss,
                    icon = {Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )},
                    label = {Text(text =stringResource(R.string.back),
                        modifier =Modifier
                            .wrapContentSize()
                    )}
                )

                NavigationBarItem(selected = false, onClick =
                {
                    if(medicinName.isNotBlank() && medicinDose.isNotBlank() && medicinStore.isNotBlank() && selectedDays.isNotEmpty() && timeADay.toInt()>=1) {

                        scheduleDetails.medicinDetails =
                            MedicinDetails(name = medicinName, form = selectedForm, relation =  selectedRelation)
                       // scheduleDetails.dose = medicinDose.toInt()
                        scheduleDetails.startDate = Date()
                        scheduleDetails.endDate = null

                        Log.i("scheduleDialog", "dni: $selectedDays")
                        Log.i("scheduleDialog", "w ciagu dniua: $timeADay ")
                        // Wywołanie funkcji
                        openDialog = true

                    }
                },
                    icon = {Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.confirm)
                    )},
                    label = {Text(text =stringResource(R.string.confirm),
                        modifier =Modifier
                            .wrapContentSize()
                    )}
                )
            }

            var infoScheduleMed by remember { mutableStateOf("") }

            if(openDialog)
            {
                showTimePickersForTimesADay(timeADay.toInt()) { selectedTimes ->
                    // Kiedy wszystkie godziny zostaną wybrane, przypisz je do wybranych dni
                    for (day in selectedDays) {
                        for ((hour, minute) in selectedTimes) {

                            // Tutaj możesz utworzyć Schedule dla każdego dnia i każdej godziny
                            val scheduleTermDetails = ScheduleTermDetails(
                                day = day,
                                hour = hour,
                                minute = minute,
                                dose = medicinDose.toInt(),
                            )
                            scheduleTermDetailsListPriv.add( scheduleTermDetails)
                            //onScheduleListChange(scheduleDetailsList+scheduleDetails)
                            Log.i(
                                "createSchedule",
                                "scheduleDetailsListPriv: ${scheduleTermDetailsListPriv.size}"
                            )
                        }
                    }
                    onScheduleChange(
                        scheduleDetails.copy(
                            startDate = Date(),
                            endDate = null,
                            scheduleTermDetailsList =  scheduleTermDetailsListPriv,
                            medicinDetails = MedicinDetails(name = medicinName , form = selectedForm, relation =  selectedRelation)
                            )
                        )
                }
                Log.i("aaaa","${scheduleTermDetailsListPriv.size}")
                for(schedule in scheduleTermDetailsListPriv)
                     {
                    Log.i("aaaa","aaaassdffgafa")
                        infoScheduleMed += "${stringResource(schedule.day.title)} o ${schedule.hour}:${schedule.minute} w dawce ${schedule.dose} ${stringResource(selectedForm.dopelniacz)} \n"
                  }
               // allSet =true
                openDialog = false
                allSet =true

            }

            if(allSet)
            {
            //    for(schedule in scheduleTermDetailsListPriv)
           //     {
            //        infoScheduleMed += "${stringResource(schedule.day.title)} o ${schedule.hour}:${schedule.minute} w dawce ${schedule.dose} ${stringResource(selectedForm.dopelniacz)} \n"
              //  }

                areYouSureDialog(
                    onConfirm = {
                        confirmed = true
                    },
                    onDismiss = {
                        allSet =false
                        infoScheduleMed = ""
                    },
                    info = "Lek o nazwie ${medicinName} jest przyjmowany ${selectedRelation} w formie ${stringResource(selectedForm.dopelniacz)} w:\n" +
                            infoScheduleMed
                   // scheduleTermDetailsListPriv.get(0).hour
                )
            }

            if(confirmed)
            {
                onAdd()
               // openDialog = false
                onDismiss()
            }

        }
    }
}




@Composable
fun showTimePickersForTimesADay(
timeADay: Int,
onTimesSelected: (List<Pair<Int, Int>>) -> Unit
) {
    val selectedTimes = mutableListOf<Pair<Int, Int>>()
    val context = LocalContext.current
    var pickedHour by remember { mutableIntStateOf(0) }
    var pickedMinute by remember { mutableIntStateOf(0) }

    fun showTimePicker(index: Int) {
        if (index >= timeADay) {
            // Kiedy wszystkie czasy zostały wybrane, zwróć listę wybranych czasów
            onTimesSelected(selectedTimes)
            return
        }

        // Pokaż dialog dla wyboru godziny i minuty
        val timePickerDialog = TimePickerDialog(
            context,
            { _: TimePicker, hour: Int, minute: Int ->
                selectedTimes.add(Pair(hour, minute))
                // Rekurencyjnie pokazuj kolejne dialogi
                showTimePicker(index + 1)
            },
            pickedHour, pickedMinute, true // true -> 24-hour format
        )

        timePickerDialog.show()
    }

    // Rozpocznij proces pokazywania dialogów
    showTimePicker(0)
}




@Composable
fun medicinRemainders(
    scheduleList: List<ScheduleDetails>,
    onScheduleClick: (ScheduleDetails) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit ,
    contentPadding: PaddingValues = PaddingValues(0.dp)
)
{
    if(scheduleList.isEmpty())
    {
        Text(
            text = stringResource(R.string.no_medicin),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(contentPadding),
        )

    }
    else {

        LazyColumn(modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))) {

            items(items = scheduleList, key = { it.medicinDetails.id })
            { schedule ->
                var expanded by remember { mutableStateOf(false) }

                medicinCard(
                    medicinScheduleInfo = schedule,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        .clickable {
                            onScheduleClick(schedule)
                            expanded = !expanded
                        },
                    expanded = expanded,
                    onEditClick = {
                        onScheduleClick(schedule)
                        onEditClick()
                    },
                    onDeleteClick = {
                        onScheduleClick(schedule)
                        onDeleteClick()
                    }
                    //expanded =false
                )


            }

        }
    }

}


@Composable
fun medicinCard(
    medicinScheduleInfo: ScheduleDetails,
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
)
{
    val color by animateColorAsState(targetValue = if (expanded) MaterialTheme.colorScheme.tertiaryContainer
    else MaterialTheme.colorScheme.primaryContainer)
    val contentColor by animateColorAsState(targetValue = if (expanded) MaterialTheme.colorScheme.onTertiaryContainer
    else MaterialTheme.colorScheme.onPrimaryContainer)

    var openDialogEdit by remember { mutableStateOf(false) }
    var openDialogDelete by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .background(color = color),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.padding_very_small))

    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                .fillMaxWidth()
                .animateContentSize(animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium)
                )
                .background(color = color),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(color = color))
            {
                Text(
                    text = medicinScheduleInfo.medicinDetails.name,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentSize().fillMaxWidth(),
                    color = contentColor
                )

                Icon(
                    imageVector = if(!expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Rozwinięcie",
                    tint = contentColor
                )


            }


            Spacer(Modifier.weight(1f))
            if (expanded) {
                Row(
                    modifier = Modifier
                        .wrapContentSize().fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_small)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Icon(imageVector = Icons.Filled.Delete,
                        modifier = Modifier
                            .padding(end = dimensionResource(R.dimen.padding_small))
                            .background(color = MaterialTheme.colorScheme.secondaryContainer)
                            .clickable {
                                openDialogDelete = true
                            }
                            .defaultMinSize(minHeight = 36.dp, minWidth = 36.dp)
                        ,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        contentDescription = "Usuń"
                    )

                    Icon(imageVector = Icons.Filled.Edit,
                        modifier = Modifier
                            .padding(end = dimensionResource(R.dimen.padding_small))
                            .background(color = MaterialTheme.colorScheme.secondaryContainer)
                            .clickable {
                                openDialogEdit = true
                            }
                            .defaultMinSize(minHeight = 36.dp, minWidth = 36.dp)
                        ,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        contentDescription = "Modyfikuj"
                    )
                }

                Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.schedule),
                        contentDescription = null
                    )
                    Spacer(Modifier.weight(1f))

                    for (scheduleInfo in medicinScheduleInfo.scheduleTermDetailsList) {
                        Text(text="${stringResource(scheduleInfo.day.title)} ${"%02d".format(scheduleInfo.hour)}:${"%02d".format(scheduleInfo.minute)}  - ${scheduleInfo.dose} ${medicinScheduleInfo.medicinDetails.form}",
                            color = contentColor)
                    }

                }
            }

            if(openDialogDelete)
            {
                areYouSureDialog(
                    onConfirm = {onDeleteClick()},
                    onDismiss = {openDialogDelete = false},
                    info = "Czy na pewno chcesz usunąć zalecenia zażywania leku ${medicinScheduleInfo.medicinDetails.name}"
                )
            }

        }


    }
}



@Composable
fun zalecenieDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    scheduleDetails: ScheduleDetails,
    storageDetails: StorageDetails,
    onValueStorageChange: (StorageDetails) -> Unit,
    onScheduleChange: (ScheduleDetails) -> Unit,
    viewModel: ZalecenieViewModel
)
{
    var openMedicinDialog by remember { mutableStateOf(true) }
    var medicinDetails by remember { mutableStateOf(scheduleDetails.medicinDetails) }
    var openAreYouSure by remember { mutableStateOf(false) }
    var confirmedMedicine by remember { mutableStateOf(false) }
    var openScheduleDialog by remember { mutableStateOf(false) }

    if(openMedicinDialog)
    {
        addMedicinDialog(
            medicinDetails = medicinDetails,
            storageDetails = storageDetails,
            viewModel = viewModel,
            onDismiss = {openMedicinDialog = false},
            onConfirm = { medicinDetailsR, storageDetailsR ->
                medicinDetails = medicinDetailsR
                onValueStorageChange(storageDetailsR)
                onScheduleChange(scheduleDetails.copy(medicinDetails = medicinDetails))
                openAreYouSure = true
            Log.i("zalecenia", "Name: ${medicinDetails.name}")
            }
        )
    }

    if(confirmedMedicine)
    {
        openAreYouSure = false
        openMedicinDialog = false
        openScheduleDialog = true
    }

    if(openScheduleDialog)
    {
        addScheduleDialog(
            onDismiss = {openScheduleDialog = false},
            onConfirm = {
                onScheduleChange(it)
                openScheduleDialog = false
                openAreYouSure = true
                Log.i("medicine", "tutaj")
            },
            medicinDetails = medicinDetails
        )
    }


    if(openAreYouSure)
    {
        if(!confirmedMedicine) {
            areYouSureDialog(
                onConfirm = {
                    confirmedMedicine = true
                },
                onDismiss = { openAreYouSure = false },
                info = "Lek, dla którego wprowadzane jest nowe zlecenie to: ${medicinDetails.name} przyjmowany jest w formie: ${medicinDetails.form}" +
                        ". Zależność leku z posiłkiem: ${medicinDetails.relation}"
            )
        }

        else
        {
            areYouSureDialog(
                onConfirm = {
                    onConfirm()
                    onDismiss
                },
                onDismiss = { openAreYouSure = false },
                info = scheduleDetails.toInfo()
            )
        }
    }




}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun addScheduleDialog(
    onDismiss: () -> Unit,
    onConfirm: (ScheduleDetails) -> Unit,
    medicinDetails: MedicinDetails
)
{
    var addNewScheduleTerm by remember { mutableStateOf(false) }
    val scheduleDetails by remember { mutableStateOf(ScheduleDetails(medicinDetails = medicinDetails)) }
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
    )
    {
        Column(
            modifier = Modifier
                .wrapContentSize().fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Text(
                text = "Uzupełnij informacje na temat dawkowania leku ${medicinDetails.name}",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_large))
            )

            Text(text = "Wybierz datę początku kuracji")
            Text(text = "Wybierz datę końca kuracji jeśli jest znana")

            ButtonIconColumn(
                onButtonCLick = { addNewScheduleTerm = true },
                labelTextId = R.string.add_medicin_title,
                imageVector = Icons.Filled.Add,
                isSelected = false,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            LazyColumn {
                items(items = scheduleDetails.scheduleTermDetailsList, key = { it.id })
                {
                    Text(
                        text = "${stringResource(it.day.title)} ${"%02d".format(it.hour)}:${
                            "%02d".format(
                                it.minute
                            )
                        }  - ${it.dose} ${medicinDetails.form}",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                ButtonIconRow(
                    onButtonCLick = onDismiss,
                    isSelected = false,
                    labelTextId = R.string.back,
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack
                )

                ButtonIconRow(
                    onButtonCLick = {
                        onConfirm(scheduleDetails)
                    },
                    isSelected = false,
                    labelTextId = R.string.confirm,
                    imageVector = Icons.Filled.Check
                )

            }
        }

        if (addNewScheduleTerm) {
            addScheduleTermDialog(
                onDismiss = { addNewScheduleTerm = false },
                onConfirm = {
                    //scheduleDetails.copy(scheduleTermDetailsList = scheduleDetails.scheduleTermDetailsList + it)
                    //scheduleDetails.scheduleTermDetailsList + it
                    scheduleDetails.scheduleTermDetailsList = scheduleDetails.scheduleTermDetailsList + it
                    Log.i("medicine", "ile juz termow: ${scheduleDetails.scheduleTermDetailsList.size}")
                    addNewScheduleTerm = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun addScheduleTermDialog(
    onDismiss: () -> Unit,
    onConfirm: (ScheduleTermDetails) -> Unit
)
{
    val scheduleTermDetails by remember { mutableStateOf(ScheduleTermDetails())}
    var expandedDays by remember { mutableStateOf(false) }
    var dose by remember { mutableStateOf(scheduleTermDetails.dose.toString()) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            .fillMaxWidth()
    )
    {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.padding_large))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Text(
                text = "Uzupełnij infomacje na temat termin zażycia",
                textAlign = TextAlign.Justify,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
            )

            TextField(value = dose,
                onValueChange = { input ->
                    // Tylko liczby będą akceptowane
                    if (input.all { it.isDigit() }) {
                        dose = input
                        // scheduleDetails = scheduleDetails.copy(dose = input.toInt())
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("Podaj dawkę leku") },
                isError = dose.isBlank() || dose.equals("0")
            )

            Column()
            {
                Row()
                {
                    Button(onClick = { expandedDays = !expandedDays })
                    {
                        Icon(
                            imageVector = if (expandedDays) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                    Text(
                        text = "Wybrany dzień tygodnia: " + stringResource(scheduleTermDetails.day.title)
                    )
                }
                DropdownMenu(expanded = expandedDays,
                    onDismissRequest = { expandedDays = false }
                )
                {
                    DayWeek.values().forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(option.title))
                            },
                            onClick = {
                                scheduleTermDetails.day = option
                                expandedDays = false
                            }
                        )
                    }
                }

            }

            val context = LocalContext.current

            val calendar = Calendar.getInstance()
            var pickedHour by remember { mutableIntStateOf(calendar[Calendar.HOUR_OF_DAY]) }
            var pickedMinute by remember { mutableIntStateOf(calendar[Calendar.MINUTE]) }
            var scheduleHour by remember { mutableIntStateOf(scheduleTermDetails.hour) }
            var scheduleMinute by remember { mutableIntStateOf(scheduleTermDetails.minute) }
            val timePickerDialog = TimePickerDialog(
                context,
                { _: TimePicker, hour: Int, minute: Int ->
                    scheduleTermDetails.hour = hour
                    scheduleHour = hour
                    scheduleTermDetails.minute = minute
                    scheduleMinute = minute
                }, pickedHour, pickedMinute, true
            )

            Text("Czas zażycia: ${scheduleHour}:${scheduleMinute}")
            ButtonIconRow(
                onButtonCLick = { timePickerDialog.show() },
                isSelected = false,
                labelTextId = R.string.czas,
                imageVector = Icons.Filled.DateRange
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                ButtonIconRow(
                    onButtonCLick = onDismiss,
                    isSelected = false,
                    labelTextId = R.string.back,
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack
                )

                ButtonIconRow(
                    onButtonCLick = {
                        scheduleTermDetails.dose = dose.toInt()
                        onConfirm(scheduleTermDetails)
                    },
                    isSelected = false,
                    labelTextId = R.string.confirm,
                    imageVector = Icons.Filled.Check
                )

            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun addMedicinDialog(
    medicinDetails: MedicinDetails,
    storageDetails: StorageDetails,
    viewModel: ZalecenieViewModel,
    onDismiss: () -> Unit,
    onConfirm: (MedicinDetails, StorageDetails) -> Unit
)
{
    var medicinName by remember { mutableStateOf(medicinDetails.name)}
    val suggestions = viewModel.medicinSuggestions
    var expandedOptions by remember { mutableStateOf(false) }
    var medicinDetails by remember { mutableStateOf(medicinDetails) }
    var expandedForm by remember { mutableStateOf(false) }
    var expandedRelations by remember { mutableStateOf(false) }
    var okey by remember { mutableStateOf(true) }
    var medicinStore by remember { mutableStateOf(storageDetails.quantity.toString()) }
    var chosenFromExisting by remember { mutableStateOf(false) }
    var storageDetails by remember { mutableStateOf(storageDetails) }
    val coroutineScope = rememberCoroutineScope()
    var storageMap : Map< StorageDetails, List<String>> = mutableMapOf()
    var expandedStorage by remember { mutableStateOf(true) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            .fillMaxWidth()
    )
    {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.padding_large))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Text(text = stringResource(R.string.add_medicin_title),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_large)))

            Text(text = stringResource(R.string.requiered_to_add_medicin),
                textAlign = TextAlign.Justify,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium)))



                TextField(
                    value = medicinName,
                    onValueChange = {
                        Log.i("zalecenia", "zmieniono wartość w textfield")
                        viewModel.onValueMedicinNameChange(it)
                        medicinName = it
                        expandedOptions = suggestions.isNotEmpty()
                    },
                    isError = !okey,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Podaj nazwę leku")}
                )

            DropdownMenu(
                expanded = expandedOptions,
                onDismissRequest = { expandedOptions = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Text(text= "Wybierz lek z istniejących",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentSize().fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onTertiaryContainer)

                suggestions.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item.name, style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.wrapContentSize().fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        },
                        onClick = {
                            medicinName = item.name
                            medicinDetails = item
                            expandedOptions = false
                            //chosenFromExisting = true
                            coroutineScope.launch {
                                storageMap = viewModel.getMedicinesStorage(item)
                                Log.i("medicine", "Wielkosc mapy; $storageMap")
                                chosenFromExisting = true
                            }
                            Log.i("medicine", "Wielkosc mapy; $storageMap")
                            //onConfirm(item)
                            // onDismiss()
                        }

                    )
                }

            }
            Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                Text(text = "Forma: ${medicinDetails.form}")
                Button(onClick = { expandedForm = !expandedForm })
                {
                    Icon(
                        imageVector = if (expandedForm) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }
                DropdownMenu(expanded = expandedForm,
                    onDismissRequest = { expandedForm = false }
                )
                {
                    MedicinForm.values().forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.name) },
                            onClick = {
                                medicinDetails = medicinDetails.copy(form = item)
                                expandedForm = false
                            },
                            enabled = !chosenFromExisting
                        )
                    }
                }


                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(text = "Zależność od posiłku ${medicinDetails.relation}")
                    Button(onClick = { expandedRelations = !expandedRelations })
                    {
                        Icon(
                            imageVector = if (expandedRelations) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                }
                DropdownMenu(expanded = expandedRelations,
                    onDismissRequest = { expandedRelations = false }
                )
                {
                    MealRelation.values().forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.name) },
                            onClick = {
                                medicinDetails = medicinDetails.copy(
                                    relation = item
                                )
                                expandedRelations = false
                            },
                            enabled = !chosenFromExisting
                        )
                    }
                }


            TextField(value = medicinStore,
                onValueChange = { input ->
                    // Tylko liczby będą akceptowane
                    if (input.isBlank() || input.all { it.isDigit() }) {
                        medicinStore = input
                        storageDetails.copy(MedicinId = medicinDetails.id, quantity = if(input.isBlank()) 0 else medicinStore.toInt())
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("Podaj jakim zapasem leku dysponujesz") })

            DropdownMenu(expanded = chosenFromExisting && expandedStorage ,
                onDismissRequest = { expandedStorage = false }
            )
            {
                Text(text ="Lek jest już przyjmowany przez innych pacjentów. Wybierz jeśli chcesz dzielić apteczkę z innym pacjentem",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentSize().fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Log.i("medicine", "Wielkosc mapy; $storageMap")
                storageMap.forEach { (storageDetailsM, patientsList) ->

                    var listName : String = ""
                    for(name in patientsList)
                    {
                        Log.i("medicine", "w forze: $name")
                        listName.plus("$name,")
                        listName = name
                    }
                    Log.i("medicine", "${storageDetailsM.id} : $listName")

                    DropdownMenuItem(
                        text = { Text(text = listName, style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentSize().fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        ) },
                        onClick = {
                            storageDetails = storageDetailsM
                            expandedStorage = false
                            medicinStore = storageDetailsM.quantity.toString()
                        }
                    )
                }
            }


            Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                ButtonIconRow(
                    onButtonCLick = onDismiss,
                    isSelected = false,
                    labelTextId = R.string.back,
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack
                )

                ButtonIconRow(
                    onButtonCLick = {
                        if(medicinName.isNotBlank()) {
                            onConfirm(medicinDetails, storageDetails)
                        }
                        else okey =false
                    },
                    // onDismiss()},
                    isSelected = false,
                    labelTextId = R.string.confirm,
                    imageVector = Icons.Filled.Check
                )

            }
        }
    }
}


