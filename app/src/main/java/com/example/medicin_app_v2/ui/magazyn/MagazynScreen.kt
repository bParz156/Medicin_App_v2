package com.example.medicin_app_v2.ui.magazyn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagazynScreen(onButtonHomeClick: () -> Unit ,
                  onButtonMagazynClicked: () ->Unit,
                  onButtonZaleceniaClicked: () ->Unit,
                  onButtonPowiadomieniaClicked: () ->Unit,
                  onButtonUstawieniaClicked: () ->Unit,
                  modifier: Modifier= Modifier)
{

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {CommunUI(
            location = Location.MAGAZYN,
            onButtonHomeClick = onButtonHomeClick,
            onButtonMagazynClicked = onButtonMagazynClicked,
            onButtonZaleceniaClicked = onButtonZaleceniaClicked,
            onButtonUstawieniaClicked = onButtonUstawieniaClicked,
            onButtonPowiadomieniaClicked = onButtonPowiadomieniaClicked
        )}
    ) {  innerPadding ->
        MagazynBody(
            contentPadding = innerPadding
        )
    }

}


@Composable
fun MagazynBody (modifier: Modifier = Modifier,
                 contentPadding: PaddingValues = PaddingValues(0.dp)) {

    Text(text= stringResource(R.string.magazyn),
        modifier = Modifier.padding(contentPadding))
}
