package com.github.calories.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.calories.DatabaseHelper
import com.github.calories.R
import com.github.calories.animations.FabAnimation
import com.github.calories.databinding.ActivityMainBinding
import com.github.calories.dialogs.InputDialog
import com.github.calories.fragments.CalendarFragment
import com.github.calories.fragments.GymFragment
import com.github.calories.fragments.StatsFragment
import com.github.calories.models.Record
import com.github.calories.utils.ThreadUtils.execute
import com.github.calories.utils.UtilsTime
import com.github.calories.utils.UtilsTime.DATE_PATTERN
import com.google.gson.Gson
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // SQLite Database
    lateinit var db: DatabaseHelper

    //lateinit var calendarFragment: CalendarFragment

    private lateinit var homeFragment: CalendarFragment
    private lateinit var gymFragment: GymFragment
    private lateinit var statsFragment: StatsFragment

    private var isRotate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //DatabaseHelper.copyDataBase(this)
        //finish()
        //exitProcess(0)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)

        createFragments()
        switchFragment(GYM_FRAGMENT)
        setupViews()
    }

    private fun setupViews() {
        binding.fabFood.setOnClickListener {
            startActivityForResult(Intent(this, AddRecordActivity::class.java), ADD_ACTIVITY) //TODO: start activity for results
        }

        binding.fabScale.setOnClickListener {
            val dialog = InputDialog(this, { weight ->
                    val calendar: Calendar = Calendar.getInstance()
                    calendar.timeInMillis = System.currentTimeMillis()
                    execute(this@MainActivity, { db.addWeight(UtilsTime.format(calendar.toInstant(), DATE_PATTERN), weight.toFloat()) }, { _ ->
                        homeFragment.refresh()
                    })
            },"Current weight",InputType.TYPE_NUMBER_FLAG_SIGNED,"Kg")

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

        FabAnimation.init(binding.fabFood);
        FabAnimation.init(binding.fabScale);

        binding.fabMenu.setOnClickListener { v->
            isRotate = FabAnimation.rotateFab(v, !isRotate)
            if(isRotate){
                FabAnimation.showIn(binding.fabFood);
                FabAnimation.showIn(binding.fabScale);
            }else{
                FabAnimation.showOut(binding.fabFood);
                FabAnimation.showOut(binding.fabScale);
            }
        }

        binding.home.setOnClickListener {
            switchFragment(HOME_FRAGMENT)
        }

        binding.gym.setOnClickListener {
            switchFragment(GYM_FRAGMENT)
        }

        binding.stats.setOnClickListener {
            switchFragment(STATS_FRAGMENT)
        }
    }


    private fun createFragments() {
        Log.d(TAG, "createFragments")
        homeFragment = CalendarFragment()
        gymFragment = GymFragment()
        statsFragment = StatsFragment()
    }

    private var currentIndex = -1
    private fun switchFragment(index : Int) {

        if(currentIndex == index)
            return

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment: Fragment? = when(index) {
            0 -> {
                binding.fabContainer.visibility = View.VISIBLE
                binding.home.setTextColor(getColor(R.color.blue))
                binding.gym.setTextColor(getColor(R.color.grey))
                binding.stats.setTextColor(getColor(R.color.grey))
                homeFragment
            }
            1-> {
                binding.fabContainer.visibility = View.GONE
                binding.home.setTextColor(getColor(R.color.grey))
                binding.stats.setTextColor(getColor(R.color.grey))
                binding.gym.setTextColor(getColor(R.color.blue))
                gymFragment
            }
            2 -> {
                binding.fabContainer.visibility = View.VISIBLE
                binding.home.setTextColor(getColor(R.color.grey))
                binding.stats.setTextColor(getColor(R.color.blue))
                binding.gym.setTextColor(getColor(R.color.grey))
                statsFragment
            }
            else -> null
        }
        fragmentTransaction.replace(R.id.contentFrame, fragment!!)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        currentIndex = index

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

        if (resultCode == RESULT_CANCELED)
            return

        when(requestCode) {
            ADD_ACTIVITY -> {
                if(data == null)
                    return
                val record = Gson().fromJson(data.getStringExtra("record"), Record::class.java)
                Log.d(TAG, "onActivityResult: adding record ${db.addRecord(record)}")
                homeFragment.refresh()
            }
            DAY_DETAIL_ACTIVITY -> {
                homeFragment.refresh()
            }
            CREATE_WORKOUT_ACTIVITY-> {
                gymFragment.refresh()
            }
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
        const val CREATE_WORKOUT_ACTIVITY: Int = 4
        const val TAG: String = "MainActivity"
        const val HOME_FRAGMENT: Int = 0
        const val GYM_FRAGMENT: Int = 1
        const val STATS_FRAGMENT: Int = 2
    }
}