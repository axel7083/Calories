package com.github.calories.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.calories.DatabaseHelper
import com.github.calories.databinding.FragmentExerciseBinding
import com.github.calories.models.Exercise
import com.github.calories.utils.SimpleCountDownTimer
import com.github.calories.utils.UtilsTime.*
import java.util.*


class ExerciseFragment : Fragment(), SimpleCountDownTimer.OnCountDownListener {

    private lateinit var binding: FragmentExerciseBinding
    private lateinit var db: DatabaseHelper
    private var exerciseEvent: ExerciseEvent? = null
    private lateinit var exercise: Exercise

    private var simpleCountDownTimer: SimpleCountDownTimer? = null

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

            binding.btnPause.setOnClickListener {
                if(simpleCountDownTimer!!.isPaused) {
                    binding.btnPause.text = "Pause"
                    simpleCountDownTimer!!.resume()
                }
                else {
                    binding.btnPause.text = "Resume"
                    simpleCountDownTimer!!.pause()
                }
            }


            simpleCountDownTimer = SimpleCountDownTimer(0,exercise.time!!.toLong(),1,this)
            simpleCountDownTimer!!.start()
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

    override fun onCountDownActive(time: String?) {
        binding.timeLeft.text = time
    }

    override fun onCountDownFinished() {
        exerciseEvent?.onFinish()
    }
}