package com.github.calories.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.calories.DatabaseHelper
import com.github.calories.R
import com.github.calories.animations.ViewAnimation
import com.github.calories.databinding.ActivityMainBinding
import com.github.calories.dialogs.WeightDialog
import com.github.calories.fragments.CalendarFragment
import com.github.calories.fragments.StatsFragment
import com.github.calories.models.Record
import com.github.calories.utils.UtilsTime
import com.google.gson.Gson
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // SQLite Database
    lateinit var db: DatabaseHelper

    //lateinit var calendarFragment: CalendarFragment


    private lateinit var homeFragment: CalendarFragment
    private lateinit var statsFragment: StatsFragment

    private var isRotate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        binding.fabFood.setOnClickListener {
            startActivityForResult(Intent(this, AddActivity::class.java), ADD_ACTIVITY) //TODO: start activity for results
        }

        binding.fabScale.setOnClickListener {
            val dialog = WeightDialog(this, (object : WeightDialog.Callback {
                override fun addWeight(weight: Float) {
                    val calendar: Calendar = Calendar.getInstance()
                    calendar.timeInMillis = System.currentTimeMillis()
                    db.addWeight(UtilsTime.formatSQL(calendar.toInstant(), TimeZone.getDefault().id), weight)
                    homeFragment.refresh()
                }
            }))

            val window = dialog.window
            if (window != null) {
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
                window.setGravity(Gravity.BOTTOM)
                window.setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
        }

        ViewAnimation.init(binding.fabFood);
        ViewAnimation.init(binding.fabScale);

        binding.fabMenu.setOnClickListener { v->
            isRotate = ViewAnimation.rotateFab(v, !isRotate)
            if(isRotate){
                ViewAnimation.showIn(binding.fabFood);
                ViewAnimation.showIn(binding.fabScale);
            }else{
                ViewAnimation.showOut(binding.fabFood);
                ViewAnimation.showOut(binding.fabScale);
            }
        }

        binding.home.setOnClickListener {
            switchFragment(false)
        }

        binding.stats.setOnClickListener {
            switchFragment(true)
        }
        createFragments()
        switchFragment(false)
    }


    private fun createFragments() {
        Log.d(TAG, "createFragments")
        homeFragment = CalendarFragment()
        statsFragment = StatsFragment()
    }

    private var isStats = true
    private fun switchFragment(isStats: Boolean) {

        this.isStats = isStats

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment: Fragment
        if (this.isStats) {
            fragment = statsFragment
            binding.home.setTextColor(getColor(R.color.grey))
            binding.stats.setTextColor(getColor(R.color.blue))
        } else {
            fragment = homeFragment
            binding.home.setTextColor(getColor(R.color.blue))
            binding.stats.setTextColor(getColor(R.color.grey))
        }
        fragmentTransaction.replace(R.id.contentFrame, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    /*private fun setupCalendarFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        calendarFragment = CalendarFragment()
        fragmentTransaction.replace(R.id.contentFrame, calendarFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult $resultCode | $requestCode")

        if(requestCode == ADD_ACTIVITY) {
            if (resultCode == RESULT_CANCELED || data == null)
                return
            else
            {
                val record = Gson().fromJson(data.getStringExtra("record"), Record::class.java)
                Log.d(TAG, "onActivityResult: adding record ${db.addRecord(record)}")
                homeFragment.refresh()
            }
        }
        else if(requestCode == DAY_DETAIL_ACTIVITY && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: Received DAY_DETAIL_ACTIVITY")
            homeFragment.refresh()
        }
    }

    var backCount: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() / 1000 - backCount < 3) {
            finish()
        } else {
            backCount = System.currentTimeMillis() / 1000
            Toast.makeText(this, R.string.back_alert, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val SCAN_ACTIVITY: Int = 2
        const val ADD_ACTIVITY: Int = 1
        const val DAY_DETAIL_ACTIVITY: Int = 3
        const val TAG: String = "MainActivity"
    }
}