package com.github.calories.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.calories.DatabaseHelper
import com.github.calories.R
import com.github.calories.activities.DayDetailsActivity
import com.github.calories.activities.MainActivity.Companion.DAY_DETAIL_ACTIVITY
import com.github.calories.calendar.DayViewContainer
import com.github.calories.calendar.MonthViewContainer
import com.github.calories.databinding.FragmentCalendarBinding
import com.github.calories.models.RawValues
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.HashMap

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var db: DatabaseHelper
    private lateinit var energyMap: HashMap<String, RawValues>
    private lateinit var weightMap: HashMap<String, Float>
    private var currentMonth: CalendarMonth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHelper(context)
    }

    fun refresh() {
        //map = db.energyPerDay //TODO: fix that
        fetchData()
        binding.calendarView.notifyCalendarChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun fetchData() {
        val now = LocalDate.now()
        val start = now.withDayOfMonth(1).toString()
        val end = now.withDayOfMonth(now.lengthOfMonth()).toString()
        energyMap = db.getEnergyPerDay(start,end)
        weightMap = db.getWeights(start,end)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()

        binding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                // Log.d(TAG, "bind: ${day.date.toString()}")
                container.dayValue.text = day.date.dayOfMonth.toString()

                if(energyMap.contains(day.date.toString())) {
                    container.calendarDayEnergyCard.visibility = View.VISIBLE
                    container.dayEnergy.text = String.format("%d", energyMap[day.date.toString()]!!.energy)
                    // On click
                    container.dayLayout.setOnClickListener {
                        // Log.d(TAG, "bind: Clicked ${day.date.toString()}")
                        val intent = Intent(activity!!, DayDetailsActivity::class.java)
                        intent.putExtra("day",day.date.toString())
                        activity!!.startActivityForResult(intent,DAY_DETAIL_ACTIVITY)
                    }
                }
                else {
                    container.calendarDayEnergyCard.visibility = View.GONE
                    container.dayLayout.setOnClickListener(null)
                }

                if(weightMap.contains(day.date.toString())) {
                    container.calendarDayWeightCard.visibility = View.VISIBLE
                    container.dayWeight.text = String.format("%.1f", weightMap[day.date.toString()]!!)
                }
                else {
                    container.calendarDayWeightCard.visibility = View.GONE
                }

                if (day.owner == DayOwner.THIS_MONTH) {
                    container.dayValue.setTextColor(ContextCompat.getColor(context!!,R.color.textColorPrimary))
                } else {
                    container.dayValue.setTextColor(Color.GRAY)
                }


            }
        }
        binding.calendarView.monthScrollListener = { month : CalendarMonth ->

            if(month != currentMonth) {
                Log.d(TAG, "monthScrollListener: ${month.year}/${month.month}")
                val start = LocalDate.of(month.year, month.month, 1)
                energyMap.putAll(db.getEnergyPerDay(start.toString(),start.withDayOfMonth(start.lengthOfMonth()).toString()))
                binding.calendarView.notifyCalendarChanged()
                currentMonth = month
            }
        }


        binding.calendarView.monthHeaderBinder = object :
                MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                container.textView.text = "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"
            }
        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        binding.calendarView.setup(firstMonth, currentMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)

    }

    companion object {
        private const val TAG: String = "CalendarFragment"
    }
}