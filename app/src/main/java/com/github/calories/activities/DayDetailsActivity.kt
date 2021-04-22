package com.github.calories.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.calories.DatabaseHelper
import com.github.calories.adapters.RecordAdapter
import com.github.calories.databinding.ActivityDayDetailsBinding
import com.github.calories.models.Record
import com.github.calories.utils.UtilsTime
import com.github.calories.utils.UtilsTime.format
import com.github.calories.utils.UtilsTime.toCalendar

class DayDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDayDetailsBinding
    private lateinit var adapter: RecordAdapter

    private val recordsRemoved: ArrayList<String> = ArrayList()
    private val foodRemoved: ArrayList<Pair<String, String>> = ArrayList()
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDayDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        val day = intent.getStringExtra("day")
        adapter = RecordAdapter(this)
        val records = db.getRecords(day)

        binding.chart.setData(buildStats(records))

        adapter.setRemoveListener(object : RecordAdapter.OnRemoveListener {
            override fun onRecordRemoveListener(record_id: String): Boolean {
                recordsRemoved.add(record_id)
                return true
            }

            override fun onFoodRemoveListener(record_id: String, food_id: String): Boolean {
                binding.bottomBar.visibility = View.VISIBLE
                foodRemoved.add(Pair(record_id, food_id))
                return true
            }
        })

        binding.statusBar.setTitle(
            format(
                toCalendar(day, UtilsTime.DATE_PATTERN).toInstant(),
                UtilsTime.WEEK_MONTH_DAY_PATTERN
            )
        )

        binding.statusBar.setLeftIconClickListener {
            onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            db.deleteFoodLink(foodRemoved)
            db.deleteRecords(recordsRemoved)
            Log.d(TAG, "btnSave: Saving")
            setResult(RESULT_OK, Intent())
            finish()
        }

        Log.d(TAG, "onCreate: ${records.size}")
        adapter.updateData(records)
        adapter.notifyDataSetChanged()
        binding.rvDayDetails.layoutManager = LinearLayoutManager(this)
        binding.rvDayDetails.adapter = adapter
    }

    private fun buildStats(records: List<Record>): List<Pair<String, Float>> {
        var lists : List<Pair<String, Float>> = ArrayList()

        var fat = 0.0
        var fiber = 0.0
        var salt = 0.0
        var proteins = 0.0
        var saturatedFat = 0.0
        var sodium = 0.0
        var sugars = 0.0

        for(record in records)  {
            for(food in record.foods!!) {
                fat += food.fat100g*food.quantity/100
                fiber += food.fiber100g*food.quantity/100
                salt += food.salt100g*food.quantity/100
                proteins += food.proteins100g*food.quantity/100
                saturatedFat += food.saturatedFat100g*food.quantity/100
                sodium += food.sodium100g*food.quantity/100
                sugars += food.sugars100g*food.quantity/100
            }
        }

        lists = lists.plus(Pair("Fat", fat.toFloat()))
        lists = lists.plus(Pair("Fiber", fiber.toFloat()))
        lists = lists.plus(Pair("Salt", salt.toFloat()))
        lists = lists.plus(Pair("Proteins", proteins.toFloat()))
        lists = lists.plus(Pair("SaturatedFat", saturatedFat.toFloat()))
        lists = lists.plus(Pair("Sodium", sodium.toFloat()))
        lists = lists.plus(Pair("Sugars", sugars.toFloat()))

        return lists
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED, Intent())
        finish()
    }

    companion object {
        private const val TAG: String = "DayDetailsActivity"
    }
}