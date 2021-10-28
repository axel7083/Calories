package com.github.calories.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.calories.DatabaseHelper
import com.github.calories.databinding.ActivityStatsBinding
import com.github.calories.utils.ThreadUtils
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import java.text.SimpleDateFormat

class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding
    private lateinit var db: DatabaseHelper
    private lateinit var rawData: List<Pair<String, List<Pair<Int, Int>>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val workoutId = intent.getLongExtra("workoutId", -1)
        if(workoutId == -1L)
            finish()

        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.workout.setDrawValueAboveBar(false)

        binding.workout.valueFormatter = object : ValueFormatter() {

            override fun getBarStackedLabel(value: Float, stackedEntry: BarEntry?): String {

                val x = stackedEntry!!.x.toInt()

                rawData[x].second.forEach {  (first, second) ->
                    if(value.toInt() == first*second) {
                        return String.format("%d kg × %d", first, second)
                    }
                }

                return String.format("%.0f kg", value)
            }

        }

        db = DatabaseHelper(this)
        ThreadUtils.execute(this, { db.getExerciseInputs(workoutId.toString()) }, { stats ->

            rawData = (stats as List<Pair<String, List<Pair<Int, Int>>>>).sortedBy {
                SimpleDateFormat("yyyy-MM-dd").parse(it.first).time
            }

            var out :List<Pair<String, FloatArray>> = ArrayList()

            rawData.forEach { (first, second) ->
                val arr = FloatArray(second.size)

                second.forEachIndexed { index, pair ->

                    arr[index] = (pair.first*pair.second).toFloat()
                    Log.d("StatsActivity", "forEachIndexed: " + index + " value: " + arr[index] )
                }

                out = out.plus(Pair(first, arr))
            }

            binding.workout.setData(out, false, "%.0f kg")
        })
    }
}