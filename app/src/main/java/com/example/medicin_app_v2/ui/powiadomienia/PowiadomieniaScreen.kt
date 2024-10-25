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
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.navigation.Location
import com.example.medicin_app_v2.ui.CommunUI
import com.example.medicin_app_v2.ui.magazyn.MagazynBody


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PowiadomieniaScreen(onButtonHomeClick: () -> Unit ,
                    onButtonMagazynClicked: () ->Unit,
                    onButtonZaleceniaClicked: () ->Unit,
                    onButtonPowiadomieniaClicked: () ->Unit,
                    onButtonUstawieniaClicked: () ->Unit,
                    onButtonPatientClicked: () ->Unit,
                    modifier: Modifier = Modifier)
{

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {CommunUI(
            location = Location.POWIADOMIENIA,
            onButtonHomeClick = onButtonHomeClick,
            onButtonMagazynClicked = onButtonMagazynClicked,
            onButtonZaleceniaClicked = onButtonZaleceniaClicked,
            onButtonUstawieniaClicked = onButtonUstawieniaClicked,
            onButtonPowiadomieniaClicked = onButtonPowiadomieniaClicked,
            onButtonPatientClicked = onButtonPatientClicked,
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