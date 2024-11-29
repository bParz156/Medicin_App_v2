package com.example.medicin_app_v2.ui.powiadomienia

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.data.MedicinForm
import com.example.medicin_app_v2.data.examination.Examination
import com.example.medicin_app_v2.data.examination.ExaminationType
import com.example.medicin_app_v2.navigation.Location
import com.example.medicin_app_v2.navigation.NavigationDestination
import com.example.medicin_app_v2.ui.AppViewModelProvider
import com.example.medicin_app_v2.ui.ButtonIconRow
import com.example.medicin_app_v2.ui.CommunUI
import com.example.medicin_app_v2.ui.home.HomeViewModel
import com.example.medicin_app_v2.ui.magazyn.MagazynBody
import com.example.medicin_app_v2.ui.magazyn.StorageDetails
import com.example.medicin_app_v2.ui.magazyn.areYouSureDialog
import com.example.medicin_app_v2.ui.patients.missingFieldsDialog
import kotlinx.coroutines.launch


object PowiadomieniaDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.Powiadomienia
    const val patientIdArg = "patientId"
    val routeWithArgs = "$route/{$patientIdArg}"

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PowiadomieniaScreen(
    viewModel: PowiadomieniaViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onButtonHomeClick: (Int) -> Unit,
    onButtonMagazynClicked: (Int) ->Unit,
    onButtonZaleceniaClicked: (Int) ->Unit,
    onButtonPowiadomieniaClicked: () ->Unit,
    onButtonUstawieniaClicked: (Int) ->Unit,
    onButtonPatientClicked: (Int) ->Unit,
    modifier: Modifier = Modifier)
{

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {CommunUI(
            location = Location.POWIADOMIENIA,
            onButtonHomeClick = { onButtonHomeClick(viewModel.notificationUiState.patientUiState.patientDetails.id)},
            onButtonMagazynClicked = { onButtonMagazynClicked(viewModel.notificationUiState.patientUiState.patientDetails.id)},
            onButtonZaleceniaClicked = { onButtonZaleceniaClicked(viewModel.notificationUiState.patientUiState.patientDetails.id)},
            onButtonUstawieniaClicked = { onButtonUstawieniaClicked(viewModel.notificationUiState.patientUiState.patientDetails.id)},
            onButtonPowiadomieniaClicked = onButtonPowiadomieniaClicked,
            onButtonPatientClicked = { onButtonPatientClicked(viewModel.notificationUiState.patientUiState.patientDetails.id)},
            patientsName = viewModel.getPatientsName(),
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        )}
    ) {  innerPadding ->
        PowiadomieniaBody(
            contentPadding = innerPadding,
            viewModel = viewModel
        )
    }

}

@Composable
fun PowiadomieniaBody (
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: PowiadomieniaViewModel

) {

    val coroutineScope = rememberCoroutineScope()
    var openDialog  = remember { mutableStateOf(false)}

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(contentPadding)
            //.verticalScroll(rememberScrollState()),
    ) {
        if (viewModel.getPatientsName().isEmpty()) {
            Text(
                text = stringResource(R.string.no_patient),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Text(
                text = stringResource(R.string.Powiadomienia),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )

            ButtonIconRow(
                onButtonCLick = {openDialog.value = true },
                labelTextId =  R.string.add,
                isSelected = false,
                imageVector = Icons.Filled.Add
            )

            BadaniaList(
                examinationList = viewModel.notificationUiState.listExamination,
                onExaminationClick = viewModel::updateUiState,
                onDeleteClick = {
                    coroutineScope.launch {
                        viewModel.deleteExamination()
                    }
                }
            )
        }
    }

    if(openDialog.value)
    {
        ExaminationDialog(
            onConfirm = {
                coroutineScope.launch {
                    viewModel.createExamination()
                }
            },
            onDismiss = {openDialog.value = false},
            updateValues = viewModel::updateUiState,
        )
    }


}

@Composable
fun BadaniaList(
    examinationList: List<Examination>,
    onExaminationClick: (Examination) -> Unit,
    onDeleteClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
)
{
    Log.i("Badania", "init: ${examinationList}")

    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(contentPadding)
       // .verticalScroll(rememberScrollState()),
    ) {
        if (examinationList.isEmpty()) {
            Text(
                text = stringResource(R.string.brakExamination),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = modifier.padding(contentPadding),
            )
        } else {


            LazyColumn(modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))) {

                items(items = examinationList, key = { it.id })
                { examination ->
                    ExaminationCard(
                        examination = examination,
                        onDeleteClick = {
                            onExaminationClick(examination)
                            onDeleteClick()
                        },
                    )
                }
            }
        }
    }


}

@Composable
fun ExaminationCard(
    examination : Examination,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
)
{
    val lowSupply = examination.value < examination.type.dolna || examination.value > examination.type.gorna
    val color by animateColorAsState(targetValue = if (lowSupply) MaterialTheme.colorScheme.errorContainer
    else MaterialTheme.colorScheme.primaryContainer)
    val contentColor by animateColorAsState(targetValue = if (lowSupply) MaterialTheme.colorScheme.onErrorContainer
    else MaterialTheme.colorScheme.onPrimaryContainer)

    var openDialogDelete by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .background(color = color)
            .padding(dimensionResource(R.dimen.padding_small)),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.padding_very_small))
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier =  Modifier
                .background(color = color)
        )
        {
            Icon(imageVector = Icons.Filled.Delete,
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.padding_small))
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .clickable {
                        openDialogDelete = true
                    }
                   // .defaultMinSize(minHeight = 36.dp, minWidth = 36.dp)
                    .weight(1f),
                tint = contentColor,
                contentDescription = stringResource(R.string.usun)
            )

            Text(
                text = stringResource(examination.type.title) +": "+ examination.date +" : "+ examination.value,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.wrapContentSize().weight(3f),
                color = contentColor
            )
        }
    }

    if(openDialogDelete)
    {
        areYouSureDialog(
            onConfirm = {
                onDeleteClick()
                openDialogDelete = false },
            onDismiss = { openDialogDelete = false },
            info = stringResource(R.string.deleteExamination)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExaminationDialog(
    onConfirm : () -> Unit,
    onDismiss : () -> Unit,
    updateValues : (Examination) -> Unit,
)
{
    var examination by remember { mutableStateOf(Examination()) }
    var expandedType by remember { mutableStateOf(false) }
    var givenValue by remember { mutableStateOf("") }
    var openErrorDialog by remember { mutableStateOf(false) }
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
                text = stringResource(R.string.addExaminationTitle),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_large))
            )

            Text(
                text = stringResource(R.string.addExaminationHeadline),
                textAlign = TextAlign.Justify,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
            )

            Row()
            {
                Text(
                    text = stringResource(examination.type.title)
                )

                ButtonIconRow(
                    onButtonCLick = {
                        expandedType = !expandedType
                    },
                    isSelected = expandedType,
                    labelTextId = R.string.Typ,
                    imageVector = if(expandedType) Icons.Filled.KeyboardArrowUp
                    else Icons.Filled.KeyboardArrowDown

                )

                DropdownMenu(expanded = expandedType,
                    onDismissRequest = { expandedType = false }
                )
                {
                    ExaminationType.entries.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(stringResource(item.title)) },
                            onClick = {
                                examination = examination.copy(type = item)
                                expandedType = false
                            }
                        )
                    }
                }
            }

            TextField(
                value = givenValue.toString(),
                onValueChange = { input ->
                        givenValue = input
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(text = stringResource(R.string.value))}
                    )

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
                        try {
                            examination.value = givenValue.toFloat()
                            updateValues(examination)
                            onConfirm()
                            onDismiss()
                        }
                        catch (e: Exception)
                        {
                            openErrorDialog = true
                        }

                    },
                    // onDismiss()},
                    isSelected = false,
                    labelTextId = R.string.confirm,
                    imageVector = Icons.Filled.Check
                )

            }
        }
        if(openErrorDialog)
        {
            missingFieldsDialog(
                onDismiss = {openErrorDialog = false},
                missingValues = stringResource(R.string.notANumber)
            )
        }

    }



}
