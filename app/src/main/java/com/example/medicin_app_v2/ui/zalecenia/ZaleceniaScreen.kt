package com.example.medicin_app_v2.ui.zalecenia

import android.app.TimePickerDialog
import android.util.Log
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
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
import com.example.medicin_app_v2.ui.CommunUI
import com.example.medicin_app_v2.ui.home.MedicinDetails
import com.example.medicin_app_v2.ui.home.ScheduleDetails
import kotlinx.coroutines.launch
import java.util.Date

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
    onButtonMagazynClicked: () ->Unit,
    onButtonZaleceniaClicked: () ->Unit,
    onButtonPowiadomieniaClicked: () ->Unit,
    onButtonUstawieniaClicked: () ->Unit,
    onButtonPatientClicked: (Int) ->Unit,
    modifier: Modifier = Modifier)
{

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {CommunUI(
            location = Location.ZALECENIA,
            onButtonHomeClick = {onButtonHomeClick(viewModel.patientUiState.patientDetails.id)},
            onButtonMagazynClicked = onButtonMagazynClicked,
            onButtonZaleceniaClicked = onButtonZaleceniaClicked,
            onButtonUstawieniaClicked = onButtonUstawieniaClicked,
            onButtonPowiadomieniaClicked = onButtonPowiadomieniaClicked,
            onButtonPatientClicked = {onButtonPatientClicked(viewModel.patientUiState.patientDetails.id)},
            patientsName = viewModel.getPatientsName()
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
        modifier = modifier,
    ) {

        if (viewModel.getPatientsName().isEmpty()) {
            Text(
                text = stringResource(R.string.no_patient),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {


            Scaffold(modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    Text(text="Przyjmowane leki")
                },
                bottomBar = {
                    Column() {

                        Button(onClick = {openDialog.value=true})
                        {
                            Row() {
                                Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(R.string.add))
                                Text(stringResource(R.string.add))
                            }
                        }


                        Text(text = "Aby dokonać zmian, przytrzymaj lek, którego dotyczy lek")
                    }
                }
            ) {  innerPadding ->
                medicinRemainders(scheduleList =viewModel.patientsSchedule.scheduleDetailsList ,
                    contentPadding = innerPadding)

                if(openDialog.value)
                {
                    addNewMedicin(
                        onAdd ={
                            coroutineScope.launch {
                                viewModel.createSchedule()
                            }
                        },
                        onDismiss = {openDialog.value=false},
                        scheduleDetails = viewModel.scheduleUiState.scheduleDetails,
                        storageDetails = viewModel.storageUiState.storageDetails,
                        onValueStorageChange = viewModel::updatestorageState,
                        onValueChange = viewModel::updatetUiState)
                }
            }



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
    onValueChange: (ScheduleDetails)-> Unit) {

    var medicinName by remember { mutableStateOf("") }
    var medicinDose by remember { mutableStateOf("") }
    var medicinStore by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedForm by remember { mutableStateOf(MedicinForm.TABLETKA) }

    var selectedRelation by remember { mutableStateOf(MealRelation.Nie) }
    var expandedR by remember { mutableStateOf(false) }

    var selectedDays by remember { mutableStateOf(listOf<DayWeek>()) }
    var expandedDays by remember { mutableStateOf(false) }

    var timeADay by remember { mutableStateOf("") }



    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
    )
    {
        Column(modifier = Modifier
            .wrapContentSize().fillMaxWidth()
            .background(color = Color.LightGray)
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
                                onValueChange(scheduleDetails.copy(medicinDetails= MedicinDetails(name =it)))},
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
                                onValueChange(scheduleDetails.copy(medicinDetails = MedicinDetails( form = item)))
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
                        onValueChange(scheduleDetails.copy(dose = input.toInt()))
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
                                onValueChange(scheduleDetails.copy(mealRelation = item))
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
                                    Text(option.name)
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
                Button(onClick = {expandedDays=!expandedDays})
                {
                    Icon(imageVector = if(expandedDays) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, contentDescription = null)
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



            var pickedHour by remember { mutableIntStateOf(0) }
            var pickedMinute by remember { mutableIntStateOf(0) }
            val context = LocalContext.current
            val timePickerDialog = TimePickerDialog(
                context,
                { _: TimePicker, hour: Int, minute: Int ->
                    pickedHour = hour
                    pickedMinute = minute
                    scheduleDetails.hour = pickedHour
                    scheduleDetails.minute = pickedMinute
                   // onValueChange(scheduleDetails.copy( minute = pickedMinute, hour = pickedHour))
                    onAdd()
                }, pickedHour, pickedMinute, true // true -> 24-hour format
            )

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
                        onValueChange(scheduleDetails.copy(medicinDetails = MedicinDetails(name  =medicinName, form = selectedForm)))
                        scheduleDetails.medicinDetails = MedicinDetails(name = medicinName, form = selectedForm)
                        scheduleDetails.dose = medicinDose.toInt()
                        scheduleDetails.mealRelation  = selectedRelation
                        scheduleDetails.startDate = Date()

                        for(day in selectedDays)
                        {
                            for(i in 0 until timeADay.toInt()) {
                                //onValueChange(scheduleDetails.copy(day = day))
                                scheduleDetails.day = day
                                timePickerDialog.show()
                            }
                        }
                            onDismiss()
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
        }
    }
}

@Composable
fun medicinRemainders(
    scheduleList: List<ScheduleDetails>,
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
        LazyColumn(modifier = Modifier.padding(contentPadding)) {

            items(items = scheduleList, key = { it.day })
            { schedule ->
                medicinCard(
                    schedule.medicinDetails.name,
                    schedule.day, schedule.hour,
                    schedule.dose, schedule.medicinDetails.form
                )
            }

        }
    }

}


@Composable
fun medicinCard(
    medicinName: String,
    day: DayWeek,
    hour: Int,
    dose: Int,
    medicinForm: MedicinForm
)
{
    Card(
        modifier = Modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row()
        {
            Column()
            {

                Text(
                    text = medicinName,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentSize().fillMaxWidth()
                )

                Text(
                    text = "dawka: $dose  ${medicinForm.name}",
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                )
            }

            Text(
                text = "Dzien przyjecia: $day godzina: $hour",
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}