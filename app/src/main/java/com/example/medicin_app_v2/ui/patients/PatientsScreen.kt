package com.example.medicin_app_v2.ui.patients

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.data.patient.Patient
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicin_app_v2.navigation.NavigationDestination
import com.example.medicin_app_v2.ui.AppViewModelProvider
import com.example.medicin_app_v2.ui.ButtonIcon
import com.example.medicin_app_v2.ui.PatientDetails
import com.example.medicin_app_v2.ui.PatientUiState
import com.example.medicin_app_v2.ui.PatientViewModel
import com.example.medicin_app_v2.ui.toPatient
import com.example.medicin_app_v2.ui.toPatientDetails
import com.example.medicin_app_v2.ui.toPatientUiState
import kotlinx.coroutines.launch


object PatientsDestination : NavigationDestination{
    override val route = "patients_settings"
    override val titleRes = R.string.Pacjenci
    const val patientIdArg = "patientId"
    val routeWithArgs = "$route/{$patientIdArg}"

}


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientsListScreen(
    onBack: () -> Unit,
    navigateToPatientHome: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PatientViewModel = viewModel(factory = AppViewModelProvider.Factory)
)
{
    Log.i("startOFScreen", "Przed czymkowliek")

  //  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var openDialog  = remember { mutableStateOf(false)}
    val patientsListviewModel = viewModel.patientslistUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Log.i("startOFScreen", "86")

    Scaffold(
        topBar = {
            DialogTopBar(onBack = onBack , onAdd = {
                openDialog.value=true
            })
        }
    ) {  innerPadding ->

            PatientsList(
                patientsList = patientsListviewModel.value.patientList,
                onDeleteClicked =
                {
                    coroutineScope.launch {
                        viewModel.deletePatient()
                    }
                },
                onPatientClick = {Log.i("przekierowanie", "ListScreen do ${it.id}")
                    navigateToPatientHome(it.id)},
                currentPatient= viewModel.uiState.value.patientDetails.toPatient(),
                viewModel = viewModel,
                contentPadding = innerPadding)

            if(openDialog.value)
            {
                PatientAddDialog(onAdd =
                {
                    coroutineScope.launch {
                        viewModel.createPatient()
                    }
                }, onDismiss = {openDialog.value = false},
                patientsDetails = viewModel.uiState.value.patientDetails,
                    onValueChange = viewModel::updatePatientUiState
                )
            }
        }
}



@Composable
private fun DialogTopBar(
    modifier: Modifier = Modifier,
    onBack: () -> Unit, onAdd: () -> Unit)
{
    Column(modifier = modifier
        .wrapContentSize()
        .padding(dimensionResource(R.dimen.padding_medium))
    ) {

        Row(modifier =Modifier
            .wrapContentSize().fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            ButtonIcon(onButtonCLick = onBack,
                isSelected = false,
                labelTextId = R.string.back,
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                modifier = Modifier.weight(1f)
                )


            Text(text = stringResource(R.string.PatientsListDialog),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_large))
                  //  .background(color= MaterialTheme.colorScheme.primary)
                    .weight(3f),
                color = MaterialTheme.colorScheme.onPrimary
            )

            ButtonIcon(
                onButtonCLick = onAdd,
                imageVector = Icons.Filled.Add,
                labelTextId = R.string.new_patient,
                isSelected = false,
                modifier = Modifier.weight(1f)
            )

        }



        Text(text = stringResource(R.string.PatientsListDialogText),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(vertical =  dimensionResource(R.dimen.padding_medium))
                .background(color= MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth(),
            color= MaterialTheme.colorScheme.onPrimaryContainer
        )


    }
}



@Composable
private fun PatientsList(
    patientsList: List<Patient>,
    onPatientClick : (Patient) -> Unit,
    onDeleteClicked : () -> Unit,
    currentPatient: Patient,
    viewModel: PatientViewModel,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(R.dimen.padding_small)),
    modifier: Modifier = Modifier
) {
    Log.i("delete", "NA poczatku list pacjent do usuniecia ${viewModel.patientUiState.patientDetails.name}")

    var openDialog  = remember { mutableStateOf(false)}
    if (patientsList.isEmpty()) {
        Text(stringResource(R.string.no_patient),
            modifier= modifier.padding(contentPadding)
        )
    }
    else {
        LazyColumn(
            modifier = modifier,
            contentPadding = contentPadding
        ) {
            items(items = patientsList, key = { it.name }) { patient ->
                Row(
                    modifier = Modifier
                        .wrapContentSize().fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_small)),
                     //   .background(if (patient == currentPatient) Color.Yellow else Color.Transparent),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    PatientCard(patient = patient,
                        modifier = Modifier
                            .padding(
                                start = dimensionResource(R.dimen.padding_medium),
                                end = dimensionResource(R.dimen.padding_small)
                            )
                            .defaultMinSize(minHeight = 36.dp, minWidth = 36.dp)
                            //.background(color = MaterialTheme.colorScheme.secondaryContainer)
                            .weight(1f)
                            .clickable {viewModel.updatePatientUiState(patientDetails = patient.toPatientDetails())
                                viewModel.updatetUiState(patientDetails = patient.toPatientDetails())
                                onPatientClick(patient)
                            }
                    )

                    Icon(imageVector = Icons.Filled.Delete,
                        modifier = Modifier
                            .padding(end = dimensionResource(R.dimen.padding_small))
                            .background(color = MaterialTheme.colorScheme.secondaryContainer)
                            .clickable {
                                viewModel.updatePatientUiState(patientDetails = patient.toPatientDetails())
                                Log.i("delete", "pacjent do usuniecia ${viewModel.patientUiState.patientDetails.name}")
                                openDialog.value = true
                            }
                            .defaultMinSize(minHeight = 36.dp, minWidth = 36.dp)
                        ,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        contentDescription = stringResource(R.string.delete_patient)
                    )
                }

            }
        }
    }

    if (openDialog.value ) {
        Log.i("delete", "Wywolanie open.dialog.value pacjent do usuniecia ${viewModel.patientUiState.patientDetails.name}")
        DeletePatientDialog(
            patient = viewModel.patientUiState.patientDetails.toPatient(),
            onDeleteClicked = {
                onDeleteClicked() // Wywołaj akcję usunięcia pacjenta
                openDialog.value = false // Zamknij dialog po usunięciu
            },
            onDismiss = { openDialog.value = false } // Zamknij dialog, jeśli anulowano
        )
    }

}


@Composable
private fun PatientCard(patient: Patient, modifier: Modifier = Modifier)
{
    val cardColors = CardColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        disabledContentColor = MaterialTheme.colorScheme.surfaceTint
    )


    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.padding_very_small)),
        colors = cardColors
    ) {

        Text(
            text = patient.name,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.wrapContentSize().fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PatientAddDialog(onAdd: () ->  Unit, onDismiss: () -> Unit, patientsDetails: PatientDetails, onValueChange: (PatientDetails)-> Unit)
{
    var patientName by remember { mutableStateOf("") }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .background(color = MaterialTheme.colorScheme.tertiary)
    )
    {
        Column(modifier = Modifier
            .wrapContentSize()
            .padding(dimensionResource(R.dimen.padding_small))
            .background(color = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Text(text = stringResource(R.string.add_pateint_title),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_large)),
                color = MaterialTheme.colorScheme.onPrimary
            )

            Text(text = stringResource(R.string.requiered_to_add),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium)),
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )

            TextField( value = patientName,
                onValueChange = {patientName = it
                                onValueChange(patientsDetails.copy(name=it))},
                placeholder = { Text(stringResource(R.string.requiered_to_add))},
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .background(
                        color = MaterialTheme.colorScheme.tertiary
                    )
            )


            Row(
                modifier = Modifier
                    .wrapContentSize().fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .padding(vertical =  dimensionResource(R.dimen.padding_small)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                ButtonIcon(
                    onButtonCLick = onDismiss,
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    isSelected = false,
                    labelTextId = R.string.back,
                    modifier = Modifier.weight(1f)
                )
                ButtonIcon(
                    onButtonCLick = {
                        patientsDetails.name=patientName
                        //onValueChange(patientsDetails.copy(name=patientName))
                        Log.i("in Add","imie to ${patientsDetails.name} : ${patientsDetails.name.isNotBlank()}")
                        if(patientsDetails.name.isNotBlank())
                        {
                            onAdd()
                            Log.i("in Add, after ADD","patinetDetails ${patientsDetails.name}")
                            onDismiss()
                        }
                    },
                    imageVector = Icons.Filled.Check,
                    isSelected = false,
                    labelTextId = R.string.confirm,
                    modifier = Modifier.weight(1f)
                )
            }

        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeletePatientDialog(patient: Patient, onDeleteClicked: () -> Unit, onDismiss: () -> Unit)
{
    Log.i("delete", "Z dialogu pacjent do usuniecia ${patient.name}")
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
    )
    {
        Column(modifier = Modifier
            .wrapContentSize()
            .padding(dimensionResource(R.dimen.padding_small))
            .background(color = Color.Cyan)
        ) {
            Text(text = stringResource(R.string.delete_patient_title),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_large)))

            Text(text = stringResource(R.string.are_you_sure, patient.name),
                textAlign = TextAlign.Justify,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium)))


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
                    Log.i("delete", "pacjent do usuniecia ${patient.name}")
                    onDeleteClicked()
                    onDismiss()
                },
                    icon = {Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete_patient)
                    )},
                    label = {Text(text =stringResource(R.string.delete_patient),
                        modifier =Modifier
                            .wrapContentSize()
                    )}
                )
            }
        }
    }
}


/*

@Composable
@Preview
fun PatientEmptyListPreview()
{
    PatientsListScreen(
        onAddPatient = {},
        onBack = {},
        onPatientClick = {} ,
        onDeleteClicked = {},
        currentPatientIdx = -1,
        patientsList = listOf(),
        )
}

@Composable
@Preview
fun PatientListPreview()
{
    PatientsListScreen(
        onAddPatient = {},
        onBack = {},
        onPatientClick = {} ,
        onDeleteClicked = {},
        currentPatientIdx = 1,
        patientsList = listOf(Patient(1, "Ania"), Patient(2,"Jan")),
        )
}

 */