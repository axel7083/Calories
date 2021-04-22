package com.github.calories.calendar

import android.view.View
import com.github.calories.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendarview.ui.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    // With ViewBinding
    val dayValue = CalendarDayLayoutBinding.bind(view).calendarDayText
    val dayEnergy = CalendarDayLayoutBinding.bind(view).calendarDayEnergy
    val dayWeight = CalendarDayLayoutBinding.bind(view).calendarDayWeight
    val dayLayout = CalendarDayLayoutBinding.bind(view).calendarDayLayout
    val calendarDayEnergyCard = CalendarDayLayoutBinding.bind(view).calendarDayEnergyCard
    val calendarDayWeightCard = CalendarDayLayoutBinding.bind(view).calendarDayWeightCard
}