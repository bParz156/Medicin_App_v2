package com.example.medicin_app_v2.ui.magazyn

import android.app.appsearch.StorageInfo
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.navigation.Location
import com.example.medicin_app_v2.navigation.NavigationDestination
import com.example.medicin_app_v2.ui.AppViewModelProvider
import com.example.medicin_app_v2.ui.CommunUI
import com.example.medicin_app_v2.ui.zalecenia.ZalecenieViewModel
import com.example.medicin_app_v2.ui.zalecenia.medicinCard


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
    modifier: Modifier= Modifier)
{

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {CommunUI(
            location = Location.MAGAZYN,
            onButtonHomeClick = {onButtonHomeClick(viewModel.homeUiState.patientDetails.id)},
            onButtonMagazynClicked = onButtonMagazynClicked,
            onButtonZaleceniaClicked = {onButtonZaleceniaClicked(viewModel.homeUiState.patientDetails.id)},
            onButtonUstawieniaClicked = onButtonUstawieniaClicked,
            onButtonPowiadomieniaClicked = onButtonPowiadomieniaClicked,
            onButtonPatientClicked = {onButtonPatientClicked(viewModel.homeUiState.patientDetails.id)},
            patientsName = viewModel.getPatientsName()
        )}
    ) {  innerPadding ->
        MagazynBody(
            viewModel = viewModel,
            contentPadding = innerPadding
        )
    }

}


@Composable
fun MagazynBody (
    viewModel: MagazynViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
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
            storageList = viewModel.homeUiState.storageDetailsList
        )
    }
}


@Composable
fun MagazynList(
    storageList: List<StorageDetails>,
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

        LazyColumn(modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))) {

            items(items = storageList, key = { it.storageId})
            { storage ->
                var expanded by remember { mutableStateOf(false) }

                storageCard(
                    storageInfo= storage,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        .clickable {
                            expanded = !expanded},
                    expanded = expanded
                )
            }

        }
    }
}

@Composable
fun storageCard(
    storageInfo : StorageDetails,
    modifier: Modifier = Modifier,
    expanded: Boolean
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


            Spacer(Modifier.weight(1f))
            if (expanded) {
                Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.schedule),
                        contentDescription = null
                    )
                    Spacer(Modifier.weight(1f))

                    Text("Przewidywany czas wyczerpania zapasów: ${storageInfo.daysToEnd} dni")

                }
            }
        }


    }
}
