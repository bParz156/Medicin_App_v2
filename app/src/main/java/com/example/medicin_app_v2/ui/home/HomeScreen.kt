package com.example.medicin_app_v2.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicin_app_v2.ui.MedicinTopAppBar
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.data.MedicinForm
import com.example.medicin_app_v2.data.patient.Patient
import com.example.medicin_app_v2.data.schedule.Schedule
import com.example.medicin_app_v2.navigation.Location
import com.example.medicin_app_v2.navigation.NavigationDestination
import com.example.medicin_app_v2.ui.AppViewModelProvider
import com.example.medicin_app_v2.ui.CommunUI
import com.example.medicin_app_v2.ui.PatientUiState
import com.example.medicin_app_v2.ui.PatientViewModel
import com.example.medicin_app_v2.ui.toPatient


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home
    const val patientIdArg = "patientId"
    val routeWithArgs = "$route/{$patientIdArg}"

}


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel =viewModel(factory = AppViewModelProvider.Factory),
  //  patientViewModel: PatientViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier : Modifier = Modifier,
    onButtonHomeClick: () -> Unit,
    onButtonMagazynClicked: () ->Unit,
    onButtonZaleceniaClicked: (Int) ->Unit,
    onButtonPowiadomieniaClicked: () ->Unit,
    onButtonUstawieniaClicked: () ->Unit,
    onButtonPatientClicked: (Int) ->Unit
    ) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()


    Scaffold(modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {CommunUI(
            location = Location.HOME,
            onButtonHomeClick = onButtonHomeClick,
            onButtonMagazynClicked = onButtonMagazynClicked,
            onButtonZaleceniaClicked = { onButtonZaleceniaClicked(viewModel.homeUiState.patientDetails.id)},
            onButtonUstawieniaClicked = onButtonUstawieniaClicked,
            onButtonPowiadomieniaClicked = onButtonPowiadomieniaClicked,
            onButtonPatientClicked ={ onButtonPatientClicked(viewModel.homeUiState.patientDetails.id)},
            patientsName = viewModel.getPatientsName()
        )}
    ) {  innerPadding ->
                HomeBody(
                    homeViewModel = viewModel,
                    modifier = modifier.fillMaxSize(),
                    contentPadding = innerPadding
                )
    }
}


@Composable
private fun HomeBody(
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {

        if (homeViewModel.getPatientsName().isEmpty()) {
            Text(
                text = stringResource(R.string.no_patient),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            //medicinRemainders(scheduleList =homeViewModel.patientsSchedule.scheduleDetailsList ,
             //   contentPadding = contentPadding)
        }
    }
}
/*
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
    day: Int,
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

 */