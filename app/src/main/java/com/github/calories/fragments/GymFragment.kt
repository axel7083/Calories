package com.github.calories.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.github.calories.DatabaseHelper
import com.github.calories.activities.CreateExerciseActivity
import com.github.calories.activities.CreateWorkoutActivity
import com.github.calories.adapters.WorkoutAdapter
import com.github.calories.databinding.FragmentGymBinding
import com.github.calories.databinding.FragmentStatsBinding
import com.github.calories.models.RawValues
import com.github.calories.models.Stats
import com.github.calories.models.Workout
import com.github.calories.utils.CustomBarChartRender
import com.github.calories.utils.ThreadUtils
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


class GymFragment : Fragment() {

    private lateinit var binding: FragmentGymBinding
    private lateinit var db: DatabaseHelper
    private lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHelper(context)
        workoutAdapter = WorkoutAdapter(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentGymBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.workouts.layoutManager = GridLayoutManager(activity,2)
        binding.workouts.setHasFixedSize(false)
        binding.workouts.adapter = workoutAdapter

        binding.btnCreate.setOnClickListener {
            startActivity(Intent(context, CreateWorkoutActivity::class.java))
        }

        binding.btnDebug.setOnClickListener {
            startActivity(Intent(context, CreateExerciseActivity::class.java))
        }

    }

    companion object {
        private const val TAG: String = "StatsFragment"
    }
}