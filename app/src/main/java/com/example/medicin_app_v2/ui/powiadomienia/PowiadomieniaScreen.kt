package com.example.medicin_app_v2.ui.powiadomienia

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.navigation.Location
import com.example.medicin_app_v2.navigation.NavigationDestination
import com.example.medicin_app_v2.ui.AppViewModelProvider
import com.example.medicin_app_v2.ui.CommunUI
import com.example.medicin_app_v2.ui.home.HomeViewModel
import com.example.medicin_app_v2.ui.magazyn.MagazynBody


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
            onButtonHomeClick = { onButtonHomeClick(viewModel.patientUiState.patientDetails.id)},
            onButtonMagazynClicked = { onButtonMagazynClicked(viewModel.patientUiState.patientDetails.id)},
            onButtonZaleceniaClicked = { onButtonZaleceniaClicked(viewModel.patientUiState.patientDetails.id)},
            onButtonUstawieniaClicked = { onButtonUstawieniaClicked(viewModel.patientUiState.patientDetails.id)},
            onButtonPowiadomieniaClicked = onButtonPowiadomieniaClicked,
            onButtonPatientClicked = { onButtonPatientClicked(viewModel.patientUiState.patientDetails.id)},
            patientsName = viewModel.getPatientsName(),
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        )}
    ) {  innerPadding ->
        PowiadomieniaBody(
            contentPadding = innerPadding
        )
    }

}

@Composable
fun PowiadomieniaBody (modifier: Modifier = Modifier,
                       contentPadding: PaddingValues = PaddingValues(0.dp)
) {

    Text(text= stringResource(R.string.Powiadomienia),
        modifier = Modifier.padding(contentPadding))
}