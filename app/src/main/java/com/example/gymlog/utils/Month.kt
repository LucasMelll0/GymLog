package com.example.gymlog.utils

import androidx.annotation.StringRes
import com.example.gymlog.R

enum class Month {
    January,
    February,
    March,
    April,
    May,
    June,
    July,
    August,
    September,
    October,
    November,
    December;

    @StringRes
    fun stringRes(): Int {
        return when(this) {
            January -> R.string.common_january
            February -> R.string.common_february
            March -> R.string.common_march
            April -> R.string.common_april
            May -> R.string.common_may
            June -> R.string.common_june
            July -> R.string.common_july
            August -> R.string.common_august
            September -> R.string.common_september
            October -> R.string.common_october
            November -> R.string.common_november
            December -> R.string.common_december
        }
    }
}