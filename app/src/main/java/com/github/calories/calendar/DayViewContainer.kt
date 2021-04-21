package com.github.calories.calendar

import android.view.View
import com.github.calories.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendarview.ui.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    // With ViewBinding
    val dayValue = CalendarDayLayoutBinding.bind(view).calendarDayText
    val dayInfo = CalendarDayLayoutBinding.bind(view).calendarDayInfo
    val dayLayout = CalendarDayLayoutBinding.bind(view).calendarDayLayout
    val calendarDayCard = CalendarDayLayoutBinding.bind(view).calendarDayCard
}