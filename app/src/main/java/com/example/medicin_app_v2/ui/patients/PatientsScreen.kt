package com.example.medicin_app_v2.ui.patients

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TextField
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientsListScreen(
    onAddPatient: (String) -> Unit,
    onBack: () -> Unit,
    onPatientClick: (Patient) -> Unit,
    onDeleteClicked: (Patient) -> Unit,
    currentPatientIdx: Int,
    patientsList: List<Patient>,
    modifier: Modifier = Modifier)
{
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var openDialog  = remember { mutableStateOf(false)}

    Scaffold(modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DialogTopBar(onBack = onBack , onAdd = {
                openDialog.value=true
            })
        }
    ) {  innerPadding ->
            if (patientsList.isEmpty()) {
                Text(stringResource(R.string.no_patient),
                    modifier= modifier.padding(innerPadding)
                )
            }
            else
            {
                PatientsList(
                    patientsList = patientsList,
                    onDeleteClicked = onDeleteClicked,
                    onPatientClick = onPatientClick,
                    currentPatientIdx= currentPatientIdx,
                    contentPadding = innerPadding)
            }

            if(openDialog.value)
            {
                PatientAddDialog(onAdd = onAddPatient, onDismiss = {openDialog.value = false})
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
        .padding(dimensionResource(R.dimen.padding_small))
    ) {
        Text(text = stringResource(R.string.PatientsListDialog),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_large)))

        Text(text = stringResource(R.string.PatientsListDialogText),
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium)))



        NavigationBar(modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            ) {
                NavigationBarItem(selected = false, onClick = onBack,
                    icon = {Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                    )},
                    label = {Text(text =stringResource(R.string.back),
                        modifier =Modifier
                            .wrapContentSize()
                    )}
                )

                NavigationBarItem(selected = false, onClick = onAdd,
                    icon = {Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.new_patient)
                    )},
                    label = {Text(text =stringResource(R.string.new_patient),
                        modifier =Modifier
                            .wrapContentSize()
                    )}
                )

        }

    }
}



@Composable
private fun PatientsList(
    patientsList: List<Patient>,
    onPatientClick : (Patient) -> Unit,
    onDeleteClicked : (Patient) -> Unit,
    currentPatientIdx: Int,
    contentPadding: PaddingValues = PaddingValues(dimensionResource(R.dimen.padding_small)),
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = patientsList, key = {it.name} ){
            patient ->

            Row(modifier = Modifier
                .wrapContentSize().fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_small)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                PatientCard(patient = patient,
                    modifier = Modifier
                        .padding(start=dimensionResource(R.dimen.padding_medium),
                            end= dimensionResource(R.dimen.padding_small))
                        .weight(1f)
                        .clickable { onPatientClick(patient)})

                Icon(imageVector = Icons.Filled.Delete,
                    modifier = Modifier
                        .padding(end=dimensionResource(R.dimen.padding_small))
                        .clickable {onDeleteClicked(patient) } ,
                    contentDescription = stringResource(R.string.delete_patient)
                )
            }

        }
    }

}


@Composable
private fun PatientCard(patient: Patient, modifier: Modifier = Modifier)
{

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Text(
            text = patient.name,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.wrapContentSize().fillMaxWidth()
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PatientAddDialog(onAdd: (String) ->  Unit, onDismiss: () -> Unit)
{
    var patientName by remember { mutableStateOf("") }

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
            Text(text = stringResource(R.string.add_pateint_title),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_large)))

            Text(text = stringResource(R.string.requiered_to_add),
                textAlign = TextAlign.Justify,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium)))

            TextField( value = patientName,
                onValueChange = {patientName = it},
                placeholder = { Text("Podaj imię i nazwisko")})

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
                    if(patientName.isNotEmpty())
                    {
                        onAdd(patientName)
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