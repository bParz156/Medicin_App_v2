package com.example.medicin_app_v2.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.medicin_app_v2.R
import com.example.medicin_app_v2.navigation.Location
import com.example.medicin_app_v2.navigation.MedicinNavHost




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunUI(
    location: Location,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onButtonHomeClick: () -> Unit = {},
    onButtonMagazynClicked: () ->Unit,
    onButtonZaleceniaClicked: () ->Unit,
    onButtonPowiadomieniaClicked: () ->Unit,
    onButtonUstawieniaClicked: () ->Unit,
    onButtonPatientClicked : () -> Unit,
    patientsName: String? = null
)
{
    Column(modifier = modifier)
    {
        MedicinTopAppBar(location = location, scrollBehavior = scrollBehavior, onButtonHomeClick = onButtonHomeClick)
        PatientBar(onPatientsButtonCLick = onButtonPatientClicked, patientsName = patientsName?: "Nie wybrano pacjenta" , onButtonUstawieniaClicked = onButtonUstawieniaClicked)
        MedicinNavigationBar(location=location,
            onButtonMagazynClicked = onButtonMagazynClicked,
            onButtonZaleceniaClicked = onButtonZaleceniaClicked,
            onButtonUstawieniaClicked = onButtonUstawieniaClicked,
            onButtonPowiadomieniaClicked = onButtonPowiadomieniaClicked)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicinTopAppBar(
    location: Location,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onButtonHomeClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {Text(stringResource(R.string.app_name))},
        modifier= modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onButtonHomeClick,
                enabled = location!=Location.HOME,
                modifier = Modifier
                    .background(color = if(location==Location.HOME) Color.LightGray
                    else Color.White)
            )
            {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home"
                )
            }
        }
    )
}

@Composable
fun PatientBar(
    onPatientsButtonCLick : () -> Unit,
    onButtonUstawieniaClicked: () -> Unit,
    patientsName : String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier
        .wrapContentSize().fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPatientsButtonCLick,
            modifier = Modifier.padding(start=dimensionResource(R.dimen.padding_small)))
        {
           Icon(imageVector = Icons.Filled.Person,
               contentDescription = "Patient")
        }

        Text(text=patientsName,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start=dimensionResource(R.dimen.padding_medium))
                .weight(1f))

        IconButton(onClick = onButtonUstawieniaClicked,
            modifier = Modifier.padding(end=dimensionResource(R.dimen.padding_small)))
        {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = Location.USTAWIENIA.name)
        }

    }
}


@Composable
fun MedicinNavigationBar(
    location: Location,
    onButtonMagazynClicked: () ->Unit,
    onButtonZaleceniaClicked: () ->Unit,
    onButtonPowiadomieniaClicked: () ->Unit,
    onButtonUstawieniaClicked: () ->Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier=modifier)
    {
        //MAGAZYN
        NavigationBarItem(selected = (location==Location.MAGAZYN),
            onClick = onButtonMagazynClicked,
            icon = { Icon(imageVector = Icons.Filled.ShoppingCart, contentDescription = Location.MAGAZYN.name) },
            label = {Text(text=Location.MAGAZYN.name, textAlign = TextAlign.Center,
                fontWeight = if(location==Location.MAGAZYN) FontWeight.Bold else FontWeight.Normal)}
            )

        //ZALECENIA
        NavigationBarItem(selected = (location==Location.ZALECENIA),
            onClick = onButtonZaleceniaClicked,
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = Location.ZALECENIA.name) },
            label = {Text(text=Location.ZALECENIA.name, textAlign = TextAlign.Center,
                fontWeight = if(location==Location.ZALECENIA) FontWeight.Bold else FontWeight.Normal)}
            )

        //POWIADOMIENIA
        NavigationBarItem(selected = (location==Location.POWIADOMIENIA),
            onClick = onButtonPowiadomieniaClicked,
            icon = { Icon(imageVector = Icons.Filled.Notifications, contentDescription = Location.POWIADOMIENIA.name) },
            label = {Text(text=Location.POWIADOMIENIA.name, textAlign = TextAlign.Center,
                fontWeight = if(location==Location.POWIADOMIENIA) FontWeight.Bold else FontWeight.Normal)}
            )
//
//        //USTAWIENIA
//        NavigationBarItem(selected = (location==Location.USTAWIENIA),
//            onClick = onButtonUstawieniaClicked,
//            icon = { Icon(imageVector = Icons.Filled.Settings, contentDescription = Location.USTAWIENIA.name) },
//            label = {Text(text=Location.USTAWIENIA.name, textAlign = TextAlign.Center,
//                fontWeight = if(location==Location.USTAWIENIA) FontWeight.Bold else FontWeight.Normal)}
//            )

    }

}


/*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun MedicinTopBarPreview()
{
    MedicinTopAppBar(location = Location.HOME)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun MedicinTopBarPreviewNotHome()
{
    MedicinTopAppBar(location = Location.POWIADOMIENIA)
}


 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun CommunUIPreviw()
{
    val location = Location.POWIADOMIENIA
    CommunUI(
        location= location,
        modifier= Modifier,
    onButtonHomeClick = {},
    onButtonMagazynClicked={},
    onButtonZaleceniaClicked={},
    onButtonPowiadomieniaClicked={},
    onButtonUstawieniaClicked={},
        onButtonPatientClicked = {}
    )
}