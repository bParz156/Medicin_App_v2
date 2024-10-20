package com.example.medicin_app_v2.data

import androidx.annotation.StringRes
import com.example.medicin_app_v2.R

enum class MedicinForm(@StringRes val dopelniacz: Int) {
    TABLETKA (dopelniacz = R.string.tabletka_dop),
    CIECZ ( dopelniacz = R.string.ciecz_dop)
}