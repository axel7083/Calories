package com.github.calories.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.calories.DatabaseHelper
import com.github.calories.databinding.FragmentStatsBinding
import com.github.calories.models.RawValues
import com.github.calories.utils.CustomBarChartRender
import com.github.calories.utils.UtilsTime.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.time.LocalDate
import java.time.temporal.TemporalField
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.ArrayList


class StatsFragment : Fragment() {

    private lateinit var binding: FragmentStatsBinding
    private var rawValues: List<Pair<String, Float>> = ArrayList()

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = DatabaseHelper(context)
        
        val now = LocalDate.now()
        val field: TemporalField = WeekFields.of(Locale.getDefault()).dayOfWeek()
        val map = db.getEnergyPerDay(
            now.with(field, 1).toString(),
            now.with(field, 7).toString()
        )

        map.forEach { raw ->
            rawValues = rawValues.plus(Pair(raw.value.date.substring(5),raw.value.energy.toFloat()))
            //rawValues = rawValues.plus(raw.value)
        }

        rawValues = rawValues.sortedWith(kotlin.Comparator { t, t2 ->
            when(toCalendar(t.first, DAY_PATTERN).before(toCalendar(t2.first,DAY_PATTERN))) {
                true -> -1
                else -> 1
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentStatsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.chart.setData(rawValues)
    }

    companion object {
        private const val TAG: String = "StatsFragment"
    }
}