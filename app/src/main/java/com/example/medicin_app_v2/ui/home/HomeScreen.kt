package com.example.medicin_app_v2.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.data.MealRelation
import com.example.medicin_app_v2.data.MedicinForm
import com.example.medicin_app_v2.data.getDayWeekByWeekDay
import com.example.medicin_app_v2.navigation.Location
import com.example.medicin_app_v2.navigation.NavigationDestination
import com.example.medicin_app_v2.ui.AppViewModelProvider
import com.example.medicin_app_v2.ui.CommunUI
import java.util.Calendar
import java.util.Date


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
    onButtonMagazynClicked: (Int) ->Unit,
    onButtonZaleceniaClicked: (Int) ->Unit,
    onButtonPowiadomieniaClicked: () ->Unit,
    onButtonUstawieniaClicked: (Int) ->Unit,
    onButtonPatientClicked: (Int) ->Unit
    ) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()


    Scaffold(
        topBar = {CommunUI(
            location = Location.HOME,
            onButtonHomeClick = onButtonHomeClick,
            onButtonMagazynClicked = { onButtonMagazynClicked(viewModel.homeUiState.patientUiState.patientDetails.id)},
            onButtonZaleceniaClicked = { onButtonZaleceniaClicked(viewModel.homeUiState.patientUiState.patientDetails.id)},
            onButtonUstawieniaClicked = {onButtonUstawieniaClicked(viewModel.homeUiState.patientUiState.patientDetails.id)},
            onButtonPowiadomieniaClicked = onButtonPowiadomieniaClicked,
            onButtonPatientClicked ={ onButtonPatientClicked(viewModel.homeUiState.patientUiState.patientDetails.id)},
            patientsName = viewModel.getPatientsName(),
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
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
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background),
    ) {
        Log.i("homeeee", "in homebody")
        Log.i("homeeee", "name: "+ homeViewModel.homeUiState.patientUiState.patientDetails.name)
        if (homeViewModel.getPatientsName().isEmpty()) {
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
        } else {
            Log.i("homeeee", "in else")
            MedicinRemainders(scheduleMap =homeViewModel.homeUiState.usageMapDay ,
                contentPadding = contentPadding)
        }
    }
}

@Composable
fun MedicinRemainders(
    scheduleMap: Map<Date, List<UsageDetails>>,
    contentPadding: PaddingValues = PaddingValues(0.dp)
)
{

    if(scheduleMap.isEmpty())
    {
        Text(
            text = stringResource(R.string.no_medicin),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(contentPadding),
            color = MaterialTheme.colorScheme.onBackground
        )

    }
    else {
        LazyColumn(
            modifier = Modifier.padding(contentPadding)
                .background(color = MaterialTheme.colorScheme.background)
        ) {

            scheduleMap.forEach { (date, usageList) ->

                val calendar = Calendar.getInstance()
                calendar.time = date

                // Pobieranie poszczególnych elementów daty
                val month = calendar.get(Calendar.MONTH) + 1  // Miesiące zaczynają się od 0
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val dayWeek = getDayWeekByWeekDay(calendar.get(Calendar.DAY_OF_WEEK) )?.title

                item {
                    Text(
                        text = "${dayWeek?.let { stringResource(it) }} $dayOfMonth.$month",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(color = MaterialTheme.colorScheme.primary),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    HorizontalDivider(
                        thickness = dimensionResource(R.dimen.padding_very_small) / 2,
                        color = MaterialTheme.colorScheme.outline
                    )
                }


                items(usageList)
                { schedule ->
                    Log.i("homeeee", schedule.medicinDetails.name)
                    medicinCard(
                        schedule.medicinDetails.name,
                        schedule.date,
                        schedule.dose, schedule.medicinDetails.form,
                        mealRelation = schedule.medicinDetails.relation,
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small))
                            .background(color = MaterialTheme.colorScheme.primaryContainer)
                    )
                }

            }
        }
    }

}





@Composable
fun medicinCard(
    medicinName: String,
    date: Date,
    dose: Int,
    medicinForm: MedicinForm,
    mealRelation: MealRelation,
    modifier: Modifier = Modifier
)
{
    val calendar = Calendar.getInstance()
    calendar.time = date

    // Pobieranie poszczególnych elementów daty
 //   val year = calendar.get(Calendar.YEAR)
 //   val month = calendar.get(Calendar.MONTH) + 1  // Miesiące zaczynają się od 0
 //   val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)  // Dzień tygodnia (1=Sunday, 7=Saturday)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)  // Godzina w formacie 24-godzinnym
    val minute = calendar.get(Calendar.MINUTE)
    Log.i("usage date","${medicinName} $hour:$minute ")

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

            Column(modifier = modifier.padding(dimensionResource(id = R.dimen.padding_large))
                .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)))
            {

                Text(
                    text = medicinName,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentSize().fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "dawka: $dose  ${medicinForm.name} $mealRelation",
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )


            Text(
                text = "godzina: ${"%02d".format(hour)}:${"%02d".format(minute)}",
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}



