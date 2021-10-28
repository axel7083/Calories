package com.github.calories.fragments

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.calories.R
import com.github.calories.databinding.FragmentExerciseBinding
import com.github.calories.models.Exercise
import com.github.calories.models.ExerciseInput
import com.github.calories.utils.SimpleCountDownTimer
import com.github.calories.utils.UtilsTime.*
import java.util.*


class ExerciseFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentExerciseBinding
    private lateinit var exerciseEvent: ExerciseEvent
    private lateinit var exercise: Exercise

    private var exerciseInputFragment: ExerciseInputFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun setExercise(exercise: Exercise) {
        this.exercise = exercise
    }

    private fun setupData() {
        binding.preview.setImageBitmap(exercise.image)
        exerciseEvent.onSetTitle(exercise.name!!)

        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        exerciseInputFragment = ExerciseInputFragment(exerciseId = exercise.id!!/*TODO: send this to have callback events*/)
        fragmentTransaction?.replace(R.id.repetitionContainer, exerciseInputFragment!!)
        //fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
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

        binding.btnCancel.setOnClickListener(this)
        binding.btnFinish.setOnClickListener(this)
    }


    override fun onClick(p0: View) {
        when(p0.id) {
            binding.btnFinish.id -> {
                System.out.println("FINISH clicked")
                if(exerciseInputFragment?.exerciseInputAdapter?.data != null && exerciseInputFragment?.exerciseInputAdapter?.data?.size!! > 0) {
                    exerciseEvent.onFinish(exerciseInputFragment?.exerciseInputAdapter?.data)
                }
                else
                {
                    Toast.makeText(context, "You need at least one record of weight/repetition to finish this exercise.", Toast.LENGTH_LONG).show()
                }
            }
            binding.btnCancel.id -> {
                System.out.println("CANCEL clicked")
                exerciseEvent.onCancel()
            }
        }
    }

    companion object {
        private const val TAG: String = "ExerciseFragment"
    }

    interface ExerciseEvent {
        fun onFinish(inputs: List<ExerciseInput>?)
        fun onCancel()
        fun onSetTitle(str: String)
    }


}