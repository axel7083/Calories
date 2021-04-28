package com.github.calories.fragments

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.calories.R
import com.github.calories.databinding.FragmentExerciseBinding
import com.github.calories.models.Exercise
import com.github.calories.utils.SimpleCountDownTimer
import com.github.calories.utils.UtilsTime.*
import java.util.*


class ExerciseFragment : Fragment(), SimpleCountDownTimer.OnCountDownListener, View.OnClickListener {

    private lateinit var binding: FragmentExerciseBinding
    private lateinit var exerciseEvent: ExerciseEvent
    private lateinit var exercise: Exercise

    private var simpleCountDownTimer: SimpleCountDownTimer? = null
    private var repetitionFragment: ExerciseInputFragment? = null

    private var repeatedIndex: Int? = null

    private var currentState: Int = STATE_STARTING
    private var isRecover: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun setExercise(exercise: Exercise) {
        this.exercise = exercise
        if(this::binding.isInitialized) {
            setupData()
        }
    }

    private fun setupData() {
        binding.preview.setImageBitmap(exercise.image)
        startExercise()
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

        if(this::exercise.isInitialized)
            setupData()

        binding.btnCenter.setOnClickListener(this)
        binding.btnSkip.setOnClickListener(this)
        binding.btnLater.setOnClickListener(this)
        binding.stats.setOnClickListener(this)
    }

    private fun startExercise() {
        currentState = STATE_WORKING
        isRecover = false

        exerciseEvent.onSetTitle("Working")
        if(repeatedIndex == null)
            repeatedIndex = 1

        binding.exerciseName.text = exercise.name + (if(repeatedIndex != null) " $repeatedIndex/${ exercise.repetitionCount}" else "")

        simpleCountDownTimer = SimpleCountDownTimer(exercise.time!!.toLong(), 1, this)
        simpleCountDownTimer!!.start()
        binding.btnCenter.text = "Pause"
    }

    private fun startRecover() {
        currentState = STATE_RECOVER
        isRecover = true

        repeatedIndex = repeatedIndex!! + 1

        exerciseEvent.onSetTitle("Recover time")
        binding.exerciseName.text = "Recover time ${exercise.name}"

        simpleCountDownTimer = SimpleCountDownTimer(exercise.recoverTime!!.toLong(), 1, this)
        simpleCountDownTimer!!.start()
        binding.btnCenter.text = "+ 20s"
    }

    override fun onCountDownActive(time: String?) {
        binding.timeLeft.text = time
    }

    override fun onCountDownFinished() {

        if(currentState == STATE_RECOVER) {
            startExercise()
            return
        }

        if(exercise.repetitionCount != null) {
            if(repeatedIndex == exercise.repetitionCount) {

                if(exercise.weighted == true) {
                    
                }
                else
                {
                    currentState = STATE_FINISHED
                    exerciseEvent.onFinish()
                }
            }
            else
                startRecover()
        }
        else
        {
            currentState = STATE_FINISHED
            exerciseEvent.onFinish()
        }
    }

    override fun onPause() {
        super.onPause()

        if(currentState != STATE_PAUSED)
            pause()
    }

    override fun onResume() {
        super.onResume()

        if(currentState == STATE_PAUSED)
            resume()
    }

    override fun onDestroy() {
        simpleCountDownTimer?.pause()
        simpleCountDownTimer = null
        super.onDestroy()
    }

    private fun pause() {
        currentState = STATE_PAUSED
        binding.btnCenter.text = "Resume"
        simpleCountDownTimer!!.pause()
    }

    private fun resume() {
        currentState = if(isRecover) STATE_RECOVER else STATE_WORKING
        binding.btnCenter.text = "Pause"
        simpleCountDownTimer!!.resume()
    }

    override fun onClick(p0: View) {

        when(p0.id) {
            binding.btnCenter.id -> {
                when (currentState) {
                    STATE_WORKING -> {
                        pause()
                    }
                    STATE_RECOVER -> {
                        simpleCountDownTimer!!.add(20)
                    }
                    STATE_PAUSED -> {
                        resume()
                    }
                }
            }
            binding.btnSkip.id -> {
                currentState = STATE_FINISHED
                exerciseEvent.onSkip()
            }
            binding.btnLater.id -> {
                currentState = STATE_FINISHED
                exerciseEvent.onLater()
            }
            binding.stats.id -> {

            }
        }
    }

    companion object {
        private const val TAG: String = "StatsFragment"
        private const val STATE_STARTING = 0
        private const val STATE_RECOVER = 1
        private const val STATE_WORKING = 2
        private const val STATE_PAUSED = 3
        private const val STATE_FINISHED = 4
    }

    interface ExerciseEvent {
        fun onFinish()
        fun onSkip()
        fun onLater()
        fun onSetTitle(str: String)
    }


}