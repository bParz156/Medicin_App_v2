package com.example.medicin_app_v2.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
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
    Column(modifier = modifier
        .background(color = MaterialTheme.colorScheme.background))
    {
        MedicinTopAppBar(location = location, scrollBehavior = scrollBehavior, onButtonHomeClick = onButtonHomeClick)
        HorizontalDivider(thickness = dimensionResource(R.dimen.padding_very_small), color = MaterialTheme.colorScheme.outline)
        PatientBar(onPatientsButtonCLick = onButtonPatientClicked, patientsName = patientsName?: "Nie wybrano pacjenta" , onButtonUstawieniaClicked = onButtonUstawieniaClicked,
            isAtUstawienie = location==Location.USTAWIENIA)
        HorizontalDivider(thickness = dimensionResource(R.dimen.padding_very_small), color = MaterialTheme.colorScheme.outline)
        MedicinNavigationBar(location=location,
            onButtonMagazynClicked = onButtonMagazynClicked,
            onButtonZaleceniaClicked = onButtonZaleceniaClicked,
            onButtonPowiadomieniaClicked = onButtonPowiadomieniaClicked)
        HorizontalDivider(thickness = dimensionResource(R.dimen.padding_very_small), color = MaterialTheme.colorScheme.outline)
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
        title = {Text(text =stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )},
        modifier= modifier
            .wrapContentSize().fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary),
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            ButtonIcon(
                onButtonCLick = onButtonHomeClick,
                isSelected = location == Location.HOME,
                labelTextId = Location.HOME.title,
                imageVector = Icons.Filled.Home,
                showLabel = false
            )
//
//            IconButton(onClick = onButtonHomeClick,
//                enabled = location!=Location.HOME,
//                modifier = Modifier
//                    .background(color = if(location==Location.HOME) MaterialTheme.colorScheme.primaryContainer
//                    else MaterialTheme.colorScheme.secondaryContainer,
//                    shape =MaterialTheme.shapes.small)
//            )
//            {
//                Icon(
//                    imageVector = Icons.Filled.Home,
//                    contentDescription = "Home",
//                    tint = if(location==Location.HOME) MaterialTheme.colorScheme.onPrimaryContainer
//                    else MaterialTheme.colorScheme.onSecondaryContainer
//                )
//            }
        }
    )
}

@Composable
fun PatientBar(
    onPatientsButtonCLick : () -> Unit,
    onButtonUstawieniaClicked: () -> Unit,
    patientsName : String = "Nie wybrano pacjenta",
    isAtUstawienie: Boolean,
    modifier: Modifier = Modifier,
) {

    Row(modifier = modifier
        .wrapContentSize().fillMaxWidth()
        .background(color = MaterialTheme.colorScheme.secondaryContainer),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ButtonIcon(
            onButtonCLick =  onPatientsButtonCLick,
            isSelected = false,
            labelTextId = Location.PACJENCI.title,
            imageVector = Icons.Filled.Person,
            showLabel = false,
            modifier = Modifier.weight(1f)
        )

        /*
        IconButton(onClick = onPatientsButtonCLick,
            modifier = Modifier.padding(start=dimensionResource(R.dimen.padding_small))
                .background(color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape =MaterialTheme.shapes.small))
        {
           Icon(imageVector = Icons.Filled.Person,
               contentDescription = "Patient",
               tint = MaterialTheme.colorScheme.onTertiaryContainer)
        }

         */

        Text(text=if(patientsName.isNotBlank()) patientsName else "Wybierz pacjenta",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start=dimensionResource(R.dimen.padding_medium))
                .weight(2f)
                .alpha(0.7f)
                .background(color = MaterialTheme.colorScheme.secondary,
                    shape = RectangleShape),
            color = MaterialTheme.colorScheme.onSecondary
        )

        ButtonIcon(onButtonCLick = onButtonUstawieniaClicked,
            isSelected = isAtUstawienie,
            labelTextId = Location.USTAWIENIA.title,
            imageVector = Icons.Filled.Settings,
            showLabel = false,
            modifier = Modifier.weight(1f)
            )
//        IconButton(onClick = onButtonUstawieniaClicked,
//            modifier = Modifier.padding(end=dimensionResource(R.dimen.padding_small))
//                .background(color = if(isAtUstawienie) MaterialTheme.colorScheme.secondaryContainer
//                else MaterialTheme.colorScheme.tertiaryContainer,
//                    shape =MaterialTheme.shapes.small)
//        )
//        {
//            Icon(imageVector = Icons.Filled.Settings, contentDescription = Location.USTAWIENIA.name)
//        }

    }
}


@Composable
fun MedicinNavigationBar(
    location: Location,
    onButtonMagazynClicked: () ->Unit,
    onButtonZaleceniaClicked: () ->Unit,
    onButtonPowiadomieniaClicked: () ->Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier
            .wrapContentSize().fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .padding(vertical =  dimensionResource(R.dimen.padding_small)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //MAGAZYN
        ButtonIcon(onButtonCLick = onButtonMagazynClicked,
            isSelected = location==Location.MAGAZYN,
            labelTextId = Location.MAGAZYN.title,
            imageVector = Icons.Filled.ShoppingCart,
            modifier = Modifier.weight(1f)
        )
        //ZALECENIA
        ButtonIcon(onButtonCLick = onButtonZaleceniaClicked,
            isSelected = location==Location.ZALECENIA,
            labelTextId = Location.ZALECENIA.title,
            imageVector = Icons.AutoMirrored.Filled.List,
            modifier = Modifier.weight(1f)
        )
        //POWIADOMIENIA
        ButtonIcon(onButtonCLick = onButtonPowiadomieniaClicked,
            isSelected = location==Location.POWIADOMIENIA,
            labelTextId = Location.POWIADOMIENIA.title,
            imageVector = Icons.Filled.Notifications,
            modifier = Modifier.weight(1f)
        )

    }
}

@Composable
fun ButtonIcon(
    onButtonCLick: () -> Unit,
    isSelected : Boolean,
    @StringRes labelTextId: Int,
    imageVector: ImageVector,
    showLabel: Boolean = true,
    modifier: Modifier = Modifier
)
{
//    IconButton(
//        onClick = onButtonCLick,
//        modifier = modifier.padding(start = dimensionResource(R.dimen.padding_small))
//            .background(
//                color = if(isSelected) MaterialTheme.colorScheme.secondaryContainer
//                else MaterialTheme.colorScheme.tertiaryContainer,
//                shape = MaterialTheme.shapes.medium
//            )
//            .wrapContentSize()
//    )
//    {
        Column(
//            modifier = Modifier
//                .background(
//                    color = if(isSelected) MaterialTheme.colorScheme.secondaryContainer
//                    else MaterialTheme.colorScheme.tertiaryContainer,
//                    shape = if(!showLabel) MaterialTheme.shapes.small else MaterialTheme.shapes.extraLarge
//                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier.padding(dimensionResource(R.dimen.padding_small))
                .background(
                    color = if(isSelected) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.tertiaryContainer,
                    shape = MaterialTheme.shapes.medium,
                )
                .wrapContentSize()
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .clickable (onClick = onButtonCLick, enabled = !isSelected)

        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = stringResource(labelTextId),
                tint = if(isSelected) MaterialTheme.colorScheme.onSecondaryContainer
                else MaterialTheme.colorScheme.onTertiaryContainer
            )
            if(showLabel) {
                Text(
                    text = stringResource(labelTextId), textAlign = TextAlign.Center,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
                    else MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
  //  }

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
@Preview()
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