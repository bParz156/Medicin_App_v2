package com.example.medicin_app_v2.navigation

import android.icu.text.CaseMap.Title
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.example.medicin_app_v2.R


enum class Location(@StringRes val title: Int) {
    HOME(title = R.string.home ),
    MAGAZYN(title = R.string.magazyn),
    ZALECENIA(title = R.string.zalecenia),
    POWIADOMIENIA(title = R.string.Powiadomienia),
    USTAWIENIA(title = R.string.Ustawienia)
}