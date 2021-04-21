package com.github.calories.calendar

import android.view.View
import android.widget.TextView
import com.github.calories.R
import com.kizitonwose.calendarview.ui.ViewContainer

class MonthViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.headerTextView)
}