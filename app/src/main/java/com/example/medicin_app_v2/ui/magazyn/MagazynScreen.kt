package com.example.medicin_app_v2.ui.magazyn

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.navigation.Location
import com.example.medicin_app_v2.navigation.NavigationDestination
import com.example.medicin_app_v2.ui.AppViewModelProvider
import com.example.medicin_app_v2.ui.CommunUI
import com.example.medicin_app_v2.ui.patients.missingFieldsDialog


object MagazynDestination : NavigationDestination {
    override val route = "magazyn"
    override val titleRes = R.string.magazyn
    const val patientIdArg = "patientId"
    val routeWithArgs = "$route/{$patientIdArg}"

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagazynScreen(
    viewModel: MagazynViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onButtonHomeClick: (Int) -> Unit,
    onButtonMagazynClicked: () ->Unit,
    onButtonZaleceniaClicked: (Int) ->Unit,
    onButtonPowiadomieniaClicked: (Int) ->Unit,
    onButtonUstawieniaClicked: (Int) ->Unit,
    onButtonPatientClicked: (Int) ->Unit,
    navigateToStorage: (Int) -> Unit,
    modifier: Modifier= Modifier)
{

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {CommunUI(
            location = Location.MAGAZYN,
            onButtonHomeClick = {onButtonHomeClick(viewModel.magazynUiState.patientDetails.id)},
            onButtonMagazynClicked = onButtonMagazynClicked,
            onButtonZaleceniaClicked = {onButtonZaleceniaClicked(viewModel.magazynUiState.patientDetails.id)},
            onButtonUstawieniaClicked = {onButtonUstawieniaClicked(viewModel.magazynUiState.patientDetails.id)},
            onButtonPowiadomieniaClicked = {onButtonPowiadomieniaClicked(viewModel.magazynUiState.patientDetails.id)},
            onButtonPatientClicked = {onButtonPatientClicked(viewModel.magazynUiState.patientDetails.id)},
            patientsName = viewModel.getPatientsName()
        )},
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {  innerPadding ->
        MagazynBody(
            viewModel = viewModel,
            contentPadding = innerPadding,
            onStorageClick = navigateToStorage
        )
    }

}


@Composable
fun MagazynBody (
    viewModel: MagazynViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onStorageClick: (Int) -> Unit
) {


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background),
    ) {
        if (viewModel.getPatientsName().isEmpty()) {
            //   Log.i("homeeee", "list empty? "+
            //    homeViewModel.homeUiState.usageList.isEmpty().toString()
            //       )
            Text(
                text = stringResource(R.string.no_patient),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        else {
            Text(
                text = stringResource(R.string.aptecznka_stan),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
              //  modifier = modifier.padding(contentPadding)
               //     .background(color = MaterialTheme.colorScheme.primary),
               // color = MaterialTheme.colorScheme.onPrimary
            )

            MagazynList(
                storageList = viewModel.magazynUiState.storageDetailsList,
                //onStorageClick = {onStorageClick(it.storageId)},
                // changingStorageDetails = viewModel.magazynUiState.changingStoragDetails,
                onStorageClick = { viewModel.increaseStorageQuantity() },
                contentPadding = contentPadding,
                onValueChange = viewModel::updateUiState,
                modifier = modifier.padding(contentPadding)
            )
        }
    }
}


@Composable
fun MagazynList(
    storageList: List<StorageDetails>,
   // changingStorageDetails: StorageDetails,
    onValueChange: (StorageDetails) -> Unit,
    onStorageClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
)
{
    if(storageList.isEmpty())
    {
        Text(
            text = stringResource(R.string.no_medicin),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = modifier.padding(contentPadding),
        )
    }

    else
    {
        Log.i("filtr", "${storageList.size}")

        var openDialog = remember { mutableStateOf(false) }
        var changingStorageDetails = remember { mutableStateOf(StorageDetails()) }

//        Column {
//

            LazyColumn(modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))) {

                items(items = storageList, key = { it.storageId })
                { storage ->

                    storageCard(
                        storageInfo = storage,
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small))
                            .clickable {
                                // onStorageClick(storage)
                                openDialog.value = true
                                onValueChange(storage)
                                changingStorageDetails.value = storage
                            }
                            .background(color = MaterialTheme.colorScheme.primaryContainer)
                    )
                }

            }
   //     }

        if (openDialog.value) {
            magazynDialog(
                storageDetails = changingStorageDetails.value,
                onDismiss = { openDialog.value = false },
                onConfirm = onStorageClick,
                onValueChange = onValueChange
            )
    }
}

}



@Composable
fun storageCard(
    storageInfo : StorageDetails,
    modifier: Modifier = Modifier
)
{

    val lowSupply = storageInfo.daysToEnd<7
    val color by animateColorAsState(targetValue = if (lowSupply) MaterialTheme.colorScheme.errorContainer
    else MaterialTheme.colorScheme.primaryContainer)
    val contentColor by animateColorAsState(targetValue = if (lowSupply) MaterialTheme.colorScheme.onErrorContainer
    else MaterialTheme.colorScheme.onPrimaryContainer)
    Log.i("filtr", "in here")
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

            Text(
                text = "${storageInfo.medName} - ${storageInfo.quantity} ${stringResource(storageInfo.medicinForm.dopelniacz)}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.wrapContentSize().fillMaxWidth(),
                color = contentColor
            )

            Text(
                text= stringResource(R.string.koniec_zapasow, storageInfo.daysToEnd),
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
                )

        }

    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun magazynDialog(
    storageDetails: StorageDetails,
    onDismiss: () -> Unit,
    onValueChange: (StorageDetails) -> Unit,
    onConfirm:() -> Unit
    )
{
    var medQuantity by remember { mutableStateOf("0") }
    var openDialogAdd by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false) }
    var confirmed by remember { mutableStateOf(false) }


    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium))
            .background(color = MaterialTheme.colorScheme.tertiaryContainer)
    )
    {
        Column {
            Text(
                text = stringResource(R.string.zapas, storageDetails.medName) ,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.wrapContentSize().fillMaxWidth()
            )

            Text(
                text = stringResource (R.string.dialog_zakupiono, storageDetails.medicinForm.name),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Justify,
                modifier = Modifier.wrapContentSize().fillMaxWidth()
            )

            TextField(
                value = medQuantity,
                onValueChange = {
                    medQuantity = it
                }
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
                        openDialog = true
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

    if(openDialog)
    {
        if(medQuantity.isBlank() || medQuantity.all { !it.isDigit() } || (medQuantity.all { it.isDigit() } && medQuantity.toInt() < 1))
        {
            missingFieldsDialog(
                onDismiss = {openDialog = false},
                missingValues = stringResource(R.string.ostrzezenie_zapas)
            )

        }
        else
        {
            openDialog = false
            openDialogAdd = true
        }

    }

    if(openDialogAdd)
    {
        areYouSureDialog(
            onDismiss = {openDialogAdd = false},
            onConfirm = {confirmed = true
                            Log.i("magazyn", "kliknieto w accept")
                            //onValueChange(patientsDetails.copy(name=patientName))
                           onValueChange(storageDetails.copy(quantity = storageDetails.quantity + medQuantity.toInt()))
                            Log.i(
                                "magazyn",
                                "po valueChange new_quant : ${storageDetails.quantity}"
                            )
                            onConfirm()
                            onDismiss()
                        },
            info = stringResource(R.string.summary_increase_storage, storageDetails.medName, medQuantity, storageDetails.medicinForm.name)
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun areYouSureDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    info: String
)
{
    BasicAlertDialog(onDismissRequest = onDismiss,
        modifier = modifier.padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState())
            .background(color = MaterialTheme.colorScheme.tertiaryContainer)
    )
    {
        Column(modifier = Modifier
            .wrapContentSize()
            .padding(dimensionResource(R.dimen.padding_small))
        ) {
            Text(text= stringResource(R.string.check_info),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.tertiary)
                    .padding(dimensionResource(R.dimen.padding_large)),
                color = MaterialTheme.colorScheme.onTertiary
            )

            Text(text= info,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.tertiary)
                    .padding(dimensionResource(R.dimen.padding_medium)),
                color = MaterialTheme.colorScheme.onTertiary
            )

            Row(modifier = Modifier
                .wrapContentSize().fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                .padding(vertical =  dimensionResource(R.dimen.padding_small)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {

                Button(onClick = onDismiss)
                {
                    Row()
                    {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )

                        Text(text=stringResource(R.string.back))
                    }
                }

                 Button(onClick = onConfirm)
                {
                    Row()
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(R.string.confirm)
                        )

                        Text(text=stringResource(R.string.confirm))
                    }
                }

            }
        }

    }
}

