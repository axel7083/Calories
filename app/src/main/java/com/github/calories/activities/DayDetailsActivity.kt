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

class DayDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDayDetailsBinding
    private lateinit var adapter: RecordAdapter

    private val recordsRemoved: ArrayList<String> = ArrayList()
    private val foodRemoved: ArrayList<Pair<String,String>> = ArrayList()
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDayDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        val day = intent.getStringExtra("day")
        adapter = RecordAdapter(this)
        val records = db.getRecords(day)

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

        binding.statusBar.setTitle(day!!) // TODO: format in a nice way
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

    override fun onBackPressed() {
        setResult(RESULT_CANCELED, Intent())
        finish()
    }

    companion object {
        private const val TAG: String = "DayDetailsActivity"
    }
}