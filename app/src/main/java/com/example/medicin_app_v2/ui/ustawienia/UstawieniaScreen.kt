package com.example.medicin_app_v2.ui.ustawienia

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.data.ThemeMode
import com.example.medicin_app_v2.navigation.Location
import com.example.medicin_app_v2.navigation.NavigationDestination
import com.example.medicin_app_v2.ui.AppViewModelProvider
import com.example.medicin_app_v2.ui.CommunUI
import com.example.medicin_app_v2.ui.magazyn.MagazynBody
import com.example.medicin_app_v2.ui.powiadomienia.PowiadomieniaBody

object UstawieniaDestination : NavigationDestination {
    override val route = "ustawienia"
    override val titleRes = R.string.Ustawienia
    const val patientIdArg = "patientId"
    val routeWithArgs = "$route/{$patientIdArg}"

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UstawieniaScreen(
    viewModel: UstawieniaViewModel =  viewModel(factory = AppViewModelProvider.Factory),
    onButtonHomeClick: (Int) -> Unit ,
                    onButtonMagazynClicked: (Int) ->Unit,
                    onButtonZaleceniaClicked: (Int) ->Unit,
                    onButtonPowiadomieniaClicked: () ->Unit,
                    onButtonUstawieniaClicked: () ->Unit,
                     onButtonPatientClicked: (Int) ->Unit,
                     modifier: Modifier = Modifier)
{
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {CommunUI(
            location = Location.USTAWIENIA,
            onButtonHomeClick = {onButtonHomeClick(viewModel.ustawieniaUiState.patientDetails.id)},
            onButtonMagazynClicked = {onButtonMagazynClicked(viewModel.ustawieniaUiState.patientDetails.id)},
            onButtonZaleceniaClicked = {onButtonZaleceniaClicked(viewModel.ustawieniaUiState.patientDetails.id)},
            onButtonUstawieniaClicked = onButtonUstawieniaClicked,
            onButtonPowiadomieniaClicked = onButtonPowiadomieniaClicked,
            onButtonPatientClicked = {onButtonPatientClicked(viewModel.ustawieniaUiState.patientDetails.id)},
            patientsName = viewModel.getPatientsName()
        )}
    ) {  innerPadding ->
        UstawieniaBody(
            contentPadding = innerPadding,
            viewModel = viewModel

        )
    }

}

@Composable
fun UstawieniaBody (
    viewModel: UstawieniaViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {


   // val themeMode = viewModel.ustawieniaUiState.themeMode

    Column {
        Text(text= stringResource(R.string.Ustawienia),
            modifier= modifier.padding(contentPadding))

        Text(text= "Wybierz mode",
            modifier= modifier.padding(contentPadding))

        // Display buttons to select the theme mode
        ThemeMode.entries.forEach { mode ->
            Button(
                onClick = { viewModel.setThemeMode(mode) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = mode.name)
            }
        }
    }

}