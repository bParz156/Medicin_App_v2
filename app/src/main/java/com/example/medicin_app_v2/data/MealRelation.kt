package com.example.medicin_app_v2.data

import androidx.annotation.StringRes
import com.example.medicin_app_v2.R

enum class MealRelation {
    Przed,
    Po,
    Nie
}

enum class DayWeek(@StringRes val title: Int) {
    PON(title = R.string.pon ),
    WT(title = R.string.wt),
    SR(title = R.string.sr),
    CZ(title = R.string.cz),
    PT(title = R.string.pt),
    SB(title= R.string.sb),
    ND(title= R.string.nd)
}