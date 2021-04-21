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
    private var rawValues: List<RawValues> = ArrayList()

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
            rawValues = rawValues.plus(raw.value)
        }

        rawValues = rawValues.sortedWith(kotlin.Comparator { t, t2 ->
            when(toCalendar(t.date, DAY_PATTERN).before(toCalendar(t2.date,DAY_PATTERN))) {
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
        setupChart()
    }

    private fun checkEmpty() {
        if (rawValues.isEmpty()) {
            Log.d(TAG, "No data to display")
            binding.emptyGraphWarning.visibility = View.VISIBLE
            binding.chart.visibility = View.GONE
            return
        } else {
            binding.emptyGraphWarning.visibility = View.GONE
            binding.chart.visibility = View.VISIBLE
        }
    }

    fun setupChart() {
        checkEmpty()
        binding.chart.animateY(800)
        binding.chart.data = createChartData()
        configureChartAppearance()
    }

    private fun configureChartAppearance() {
        binding.chart.description.isEnabled = false

        // Make round corner
        val barChartRender =
            CustomBarChartRender(
                binding.chart,
                binding.chart.animator,
                binding.chart.viewPortHandler
            )
        barChartRender.setRadius(20)
        binding.chart.renderer = barChartRender
        binding.chart.isClickable = false
        binding.chart.isHighlightFullBarEnabled = false
        //barChart.setTouchEnabled(false);
        binding.chart.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
        binding.chart.scrollBarSize = 100
        binding.chart.isHorizontalScrollBarEnabled = true
        binding.chart.scrollBarDefaultDelayBeforeFade = 100000
        binding.chart.setVisibleXRangeMaximum(8f)
        binding.chart.setPinchZoom(false)
        binding.chart.isScaleYEnabled = false
        binding.chart.isScaleXEnabled = false
        binding.chart.enableScroll()
        binding.chart.legend.isEnabled = false
        binding.chart.extraBottomOffset = 20f
        binding.chart.data.isHighlightEnabled = false //hightlight on click
        binding.chart.axisLeft.setDrawGridLines(false)
        val xAxis = binding.chart.xAxis
        xAxis.textSize = 20f
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true


        //xAxis.setAxisMinimum(-1);
        //xAxis.setAxisMaximum(7);
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return rawValues[value.toInt()].date.substring(5)
            }
        }
        binding.chart.axisRight.isEnabled = false
    }

    private fun createChartData(): BarData? {
        val values = ArrayList<BarEntry>()
        for (i in rawValues.indices) {
            values.add(BarEntry(i.toFloat(), rawValues[i].energy.toFloat()))
        }
        val set1 = BarDataSet(values, "SET_LABEL")
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)
        val data = BarData(dataSets)
        data.barWidth = 0.5f
        data.setDrawValues(true)
        return data
    }

    companion object {
        private const val TAG: String = "StatsFragment"
    }
}