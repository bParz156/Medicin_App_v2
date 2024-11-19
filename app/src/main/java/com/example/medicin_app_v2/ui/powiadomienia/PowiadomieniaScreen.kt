package com.example.medicin_app_v2.ui.powiadomienia

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
    var showNot by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(contentPadding)) {
    Text(text= stringResource(R.string.Powiadomienia),
        modifier = Modifier.padding(contentPadding))

    Button(
        onClick = { showNot = !showNot }
    )
    {
        Text(text = "Kliknij")
    }
        }

    if(showNot)
    {
        notificationBuild()
    }

}
@Composable
fun notificationBuild()
{
    val context = LocalContext.current
    val CHANNEL_ID = "alarm_id"

    var builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("My notification")
        .setContentText("Much longer text that cannot fit one line...")
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText("Much longer text that cannot fit one line..."))
        .setPriority(NotificationCompat.PRIORITY_MAX)

       // .setSound("android.resource://"+context.packageName+"/"+R.raw.FILE_NAME)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
            //                                        grantResults: IntArray)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return@with
        }
        notify(0, builder.build())
    }
}