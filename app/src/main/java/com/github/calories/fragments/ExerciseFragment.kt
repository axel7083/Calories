package com.github.calories.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.calories.DatabaseHelper
import com.github.calories.databinding.FragmentExerciseBinding
import com.github.calories.databinding.FragmentStatsBinding
import com.github.calories.models.Exercise
import com.github.calories.models.RawValues
import com.github.calories.models.Stats
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


class ExerciseFragment : Fragment() {

    private lateinit var binding: FragmentExerciseBinding
    private lateinit var db: DatabaseHelper
    private var exerciseEvent: ExerciseEvent? = null
    private lateinit var exercise: Exercise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHelper(context)
    }

    fun setExercise(exercise: Exercise) {
        this.exercise = exercise
        if(this::binding.isInitialized) {
            setupData()
        }
    }

    private fun setupData() {
        binding.preview.setImageBitmap(exercise.image!!)
        binding.exerciseName.text = exercise.name

        if(exercise.time != null) {
            binding.timeLeft.text = exercise.time.toString()
        }
        else
        {
            binding.timeLeft.text = "add next btn"
        }
    }

    fun setEventListener(exerciseEvent: ExerciseEvent) {
        this.exerciseEvent = exerciseEvent
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentExerciseBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(this::exercise.isInitialized) {
            setupData()
        }

        binding.btnSkip.setOnClickListener {
            exerciseEvent?.onFinish()
        }

        binding.btnLater.setOnClickListener {
            exerciseEvent?.onFinish()
        }
    }

    interface ExerciseEvent {
        fun onFinish()
    }

    companion object {
        private const val TAG: String = "StatsFragment"
    }
}