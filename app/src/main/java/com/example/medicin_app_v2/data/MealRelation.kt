package com.example.medicin_app_v2.data

import androidx.annotation.StringRes
import com.example.medicin_app_v2.R

/**
 * związek zażywaci aleku od posiłku
 */
enum class MealRelation(@StringRes val title: Int) {
    Przed(title = R.string.przed),
    Po(title = R.string.po),
    Nie(title = R.string.nie)
}

/**
 * dni tygodnia, numeracja zgodna z używanymi funkcjami określającymi dzień tygodnia
 */
enum class DayWeek(@StringRes val title: Int, val weekDay: Int) {
    PON(title = R.string.pon, weekDay = 2 ),
    WT(title = R.string.wt,  weekDay = 3),
    SR(title = R.string.sr, weekDay = 4),
    CZ(title = R.string.cz, weekDay = 5),
    PT(title = R.string.pt, weekDay = 6),
    SB(title= R.string.sb, weekDay = 7),
    ND(title= R.string.nd, weekDay = 1)
}

fun getDayWeekByWeekDay(weekDay: Int) : DayWeek?{
    return DayWeek.values().firstOrNull { it.weekDay == weekDay }
}
