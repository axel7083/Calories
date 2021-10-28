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
import com.github.calories.models.Stats
import com.github.calories.utils.CustomBarChartRender
import com.github.calories.utils.ThreadUtils
import com.github.calories.utils.UtilsTime.*
import com.github.mikephil.charting.components.LimitLine
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
    //private var rawValues: List<Pair<String, Float>> = ArrayList()

    private var stats: List<Stats>? = null
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = DatabaseHelper(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentStatsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root    }


    private fun getEnergy(): List<Pair<String, FloatArray>> {
        var energy: List<Pair<String, FloatArray>> = ArrayList()

        stats!!.forEach { stat ->
            energy = energy.plus(Pair(stat.day.substring(5),floatArrayOf(stat.energy.toFloat())))
            //rawValues = rawValues.plus(raw.value)
        }
        return energy
    }

    private fun fetchData(back: () -> Unit) {
        val now = LocalDate.now()
        val field: TemporalField = WeekFields.of(Locale.getDefault()).dayOfWeek()

        ThreadUtils.execute(requireActivity(), { db.getStats(now.with(field, 1).toString(),
                now.with(field, 7).toString()) }, { stats ->
            this.stats = stats as List<Stats>

            back()
        })


    }

    private fun setupCharts() {
        binding.chart.setData(getEnergy(), true, "%.0f kcal")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(stats == null) {
            fetchData {
                setupCharts()
            }
        }
        else
        {
            setupCharts()
        }

    }

    companion object {
        private const val TAG: String = "StatsFragment"
    }
}