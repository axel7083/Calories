package com.github.calories.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.github.calories.R
import com.github.calories.databinding.ViewBarGraphBinding
import com.github.calories.utils.CustomBarChartRender
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

class BarGraph(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val binding: ViewBarGraphBinding = ViewBarGraphBinding.bind(
        inflate(
            context,
            R.layout.view_bar_graph,
            this
        )
    )
    private lateinit var data: List<Pair<String, Float>>
    private val BACKGROUND: Int = Color.TRANSPARENT

    fun setData(data: List<Pair<String, Float>>) {
        this.data = data
        setupChart()
    }

    private fun checkEmpty() {
        if (data.isEmpty()) {
            binding.emptyGraphWarning.visibility = View.VISIBLE
            binding.graph.visibility = View.GONE
            return
        } else {
            binding.emptyGraphWarning.visibility = View.GONE
            binding.graph.visibility = View.VISIBLE
        }
    }

    private fun setupChart() {
        checkEmpty()
        binding.graph.animateY(800)
        binding.graph.data = createChartData()
        configureChartAppearance()
    }

    private fun createChartData(): BarData? {
        val values = ArrayList<BarEntry>()
        for (i in data.indices) {
            values.add(BarEntry(i.toFloat(), data[i].second))
        }
        val set1 = BarDataSet(values, "SET_LABEL")
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)
        val data = BarData(dataSets)
        data.barWidth = 0.5f
        data.setValueTextColor( ContextCompat.getColor(context, R.color.textColorSecondary))
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.0fg",value)
            }
        })
        data.setDrawValues(true)
        return data
    }

    private fun configureChartAppearance() {
        binding.graph.description.isEnabled = false
        // Make round corner
        val barChartRender =
            CustomBarChartRender(
                binding.graph,
                binding.graph.animator,
                binding.graph.viewPortHandler
            )
        barChartRender.setRadius(20)
        binding.graph.renderer = barChartRender
        binding.graph.isClickable = false
        binding.graph.isHighlightFullBarEnabled = false
        //barChart.setTouchEnabled(false);
        binding.graph.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
        binding.graph.scrollBarSize = 100
        binding.graph.isHorizontalScrollBarEnabled = true
        binding.graph.scrollBarDefaultDelayBeforeFade = 100000
        binding.graph.setVisibleXRangeMaximum(8f)
        binding.graph.setPinchZoom(false)
        binding.graph.isScaleYEnabled = false
        binding.graph.isScaleXEnabled = false
        binding.graph.enableScroll()
        binding.graph.legend.isEnabled = false
        binding.graph.extraBottomOffset = 20f
        binding.graph.data.isHighlightEnabled = false //hightlight on click
        binding.graph.axisLeft.setDrawGridLines(false)
        val xAxis = binding.graph.xAxis
        xAxis.textSize = 8f
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.textColor = ContextCompat.getColor(context, R.color.textColorPrimary)
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return data[value.toInt()].first
            }
        }
        binding.graph.axisRight.isEnabled = false
        //
        binding.graph.axisLeft.textColor = ContextCompat.getColor(context, R.color.textColorPrimary)
       // binding.graph.textC
    }

    init {

    }
}