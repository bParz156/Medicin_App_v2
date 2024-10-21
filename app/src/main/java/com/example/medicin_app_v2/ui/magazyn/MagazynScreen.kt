package com.example.medicin_app_v2.ui.magazyn

import android.util.Log
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
    onButtonPowiadomieniaClicked: () ->Unit,
    onButtonUstawieniaClicked: () ->Unit,
    onButtonPatientClicked: (Int) ->Unit,
    navigateToStorage: (Int) -> Unit,
    modifier: Modifier= Modifier)
{

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {CommunUI(
            location = Location.MAGAZYN,
            onButtonHomeClick = {onButtonHomeClick(viewModel.magazynUiState.patientDetails.id)},
            onButtonMagazynClicked = onButtonMagazynClicked,
            onButtonZaleceniaClicked = {onButtonZaleceniaClicked(viewModel.magazynUiState.patientDetails.id)},
            onButtonUstawieniaClicked = onButtonUstawieniaClicked,
            onButtonPowiadomieniaClicked = onButtonPowiadomieniaClicked,
            onButtonPatientClicked = {onButtonPatientClicked(viewModel.magazynUiState.patientDetails.id)},
            patientsName = viewModel.getPatientsName()
        )}
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
        modifier = modifier.padding(contentPadding),
    ) {
        Text(
            text = "Status apteczki",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(contentPadding)
        )

        MagazynList(
            storageList = viewModel.magazynUiState.storageDetailsList,
            //onStorageClick = {onStorageClick(it.storageId)},
           // changingStorageDetails = viewModel.magazynUiState.changingStoragDetails,
            onStorageClick = {viewModel.increaseStorageQuantity()},
            contentPadding = contentPadding,
            onValueChange = viewModel::updateUiState
        )
    }
}


@Composable
fun MagazynList(
    storageList: List<StorageDetails>,
   // changingStorageDetails: StorageDetails,
    onValueChange: (StorageDetails) -> Unit,
    onStorageClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
)
{
    if(storageList.isEmpty())
    {
        Text(
            text = stringResource(R.string.no_medicin),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(contentPadding),
        )
    }
    else{
        Log.i("filtr", "${storageList.size}")

        var openDialog  = remember { mutableStateOf(false)}
        var changingStorageDetails = remember { mutableStateOf(StorageDetails()) }



        LazyColumn(modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))) {

            items(items = storageList, key = { it.storageId})
            { storage ->

                storageCard(
                    storageInfo= storage,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        .clickable {
                           // onStorageClick(storage)
                            openDialog.value = true
                            onValueChange(storage)
                            changingStorageDetails.value = storage

                        }
                )
            }

        }

        if(openDialog.value)
        {
            magazynDialog(
                storageDetails = changingStorageDetails.value,
                onDismiss = {openDialog.value = false},
                onConfirm =  onStorageClick,
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
    Log.i("filtr", "in here")
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {

            Text(
                text = "${storageInfo.medName} - ${storageInfo.quantity} ${stringResource(storageInfo.medicinForm.dopelniacz)}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.wrapContentSize().fillMaxWidth()
            )

            Text(
                text="Przewidywany czas wyczerpania zapasów: ${storageInfo.daysToEnd} dni",
                style = MaterialTheme.typography.labelMedium
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
    var openDialog by remember { mutableStateOf(false) }
    var confirmed by remember { mutableStateOf(false) }


    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium))
            .background(Color.Gray)
    )
    {
        Column {
            Text(
                text = "Zapasy leku ${storageDetails.medName}",
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.wrapContentSize().fillMaxWidth()
            )

            Text(
                text = "Wpisz w pole zakupioną ilość leku. Wpisz ty;lp liczbę, przyjęte jednostka to: ${storageDetails.medicinForm.name}",
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
        areYouSureDialog(
            onDismiss = {openDialog = false},
            onConfirm = {confirmed = true
                            Log.i("magazyn", "kliknieto w accept")
                            //onValueChange(patientsDetails.copy(name=patientName))
                            if (medQuantity.isNotBlank() && medQuantity.all { it.isDigit() } && medQuantity.toInt() > 0) {
                                Log.i(
                                    "magazyn",
                                    "spelnia warunek: quant: ${storageDetails.quantity}  + ${medQuantity.toInt()}"
                                )
                                onValueChange(storageDetails.copy(quantity = storageDetails.quantity + medQuantity.toInt()))
                                Log.i(
                                    "magazyn",
                                    "po valueChange new_quant : ${storageDetails.quantity}"
                                )
                                onConfirm()
                                onDismiss()
                            }
                        },
            info = "Dokupiono ${medQuantity} ${stringResource(storageDetails.medicinForm.dopelniacz)} leku o nazwie ${storageDetails.medName}"
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
        modifier = modifier.padding(dimensionResource(R.dimen.padding_medium)))
    {
        Column {
            Text(text= "Sprawdź poprawność informacji",
                style = MaterialTheme.typography.titleLarge)

            Text(text= info,
                style = MaterialTheme.typography.titleMedium)

            Row(modifier = Modifier.fillMaxWidth())
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

