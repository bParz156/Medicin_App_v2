package com.example.medicin_app_v2

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicin_app_v2.navigation.Location

//import androidx.navigation.NavHostController
//import androidx.navigation.compose.rememberNavController

//@Composable
//fun MedicinApp(navController: NavHostController = rememberNavController()) {
//    InventoryNavHost(navController = navController)
//}

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
                enabled = location!=Location.HOME

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun MedicinTopBarPreview()
{
    MedicinTopAppBar(location = Location.HOME)
}
