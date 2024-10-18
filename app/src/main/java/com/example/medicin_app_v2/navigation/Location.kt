package com.example.medicin_app_v2.navigation

import androidx.annotation.StringRes
import com.example.medicin_app_v2.R


interface NavigationDestination
{
    val route: String
    val titleRes: Int
}



enum class Location(@StringRes val title: Int) {
    HOME(title = R.string.home ),
    MAGAZYN(title = R.string.magazyn),
    ZALECENIA(title = R.string.zalecenia),
    POWIADOMIENIA(title = R.string.Powiadomienia),
    USTAWIENIA(title = R.string.Ustawienia),
    PACJENCI(title= R.string.Pacjenci)
}