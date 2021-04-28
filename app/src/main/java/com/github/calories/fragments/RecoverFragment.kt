package com.github.calories.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.calories.DatabaseHelper
import com.github.calories.databinding.FragmentRecoverBinding
import com.github.calories.databinding.FragmentStatsBinding
import com.github.calories.models.Exercise
import com.github.calories.models.RawValues
import com.github.calories.models.Stats
import com.github.calories.utils.CustomBarChartRender
import com.github.calories.utils.SimpleCountDownTimer
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


class RecoverFragment : Fragment(), SimpleCountDownTimer.OnCountDownListener {

    private lateinit var binding: FragmentRecoverBinding

    private lateinit var previousExercise: Exercise
    private var nextExercise: Exercise? = null
    private var recoverEvent: RecoverEvent? = null

    private var simpleCountDownTimer: SimpleCountDownTimer? = null

    private var recoverTime: Int?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun setExercises(previousExercise: Exercise, nextExercise: Exercise?, recoverTime: Int) {
        this.previousExercise = previousExercise
        this.nextExercise = nextExercise
        this.recoverTime = recoverTime
        if(this::binding.isInitialized) {
            setupData()
        }
    }

    fun setEventListener(recoverEvent: RecoverEvent) {
        this.recoverEvent = recoverEvent
    }


    private fun setupData() {
        if(nextExercise != null) {

            simpleCountDownTimer = SimpleCountDownTimer(recoverTime!!.toLong(),1,this)
            simpleCountDownTimer!!.start()

            binding.nextExercisePreview.setImageBitmap(nextExercise!!.image!!)
            binding.nextExerciseName.text = nextExercise!!.name

            binding.btnMoreTime.setOnClickListener {
                simpleCountDownTimer!!.add(BONUS_TIME.toLong())
            }
        }
        else
        {
            binding.nextPreview.visibility = View.GONE
            binding.nextTitle.visibility = View.GONE
            binding.timer.visibility = View.GONE
            binding.btnStart.text = "Finish Workout"
            binding.btnMoreTime.text = "Finish Workout"
            binding.btnMoreTime.setOnClickListener {
                recoverEvent?.onStart()
            }
        }

        binding.rateTitle.text = "Rate previous exercise (${previousExercise.name})"
        binding.btnStart.setOnClickListener {
            recoverEvent?.onStart()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentRecoverBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(this::previousExercise.isInitialized) {
            setupData()
        }

    }

    interface RecoverEvent {
        fun onStart()
    }

    companion object {
        private const val TAG: String = "RecoverFragment"
        private const val BONUS_TIME: Int = 20
    }

    override fun onCountDownActive(time: String?) {
        binding.timerValue.text = time
    }

    override fun onCountDownFinished() {
        recoverEvent?.onStart()
    }

    override fun onPause() {
        super.onPause()
        if(simpleCountDownTimer != null)
            simpleCountDownTimer!!.pause()
    }

    override fun onResume() {
        super.onResume()

        if(simpleCountDownTimer!!.isPaused) {
            simpleCountDownTimer!!.resume()
        }
    }
}