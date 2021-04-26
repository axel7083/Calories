package com.github.calories.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.calories.DatabaseHelper
import com.github.calories.R
import com.github.calories.databinding.ActivityWorkoutBinding
import com.github.calories.databinding.FragmentFinishWorkoutBinding
import com.github.calories.fragments.*
import com.github.calories.models.Exercise
import com.github.calories.models.Workout
import com.github.calories.utils.ThreadUtils
import com.github.calories.utils.UtilsTime
import com.google.gson.Gson

class WorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutBinding
    lateinit var db: DatabaseHelper

    // Fragments
    private lateinit var exerciseFragment: ExerciseFragment
    private lateinit var recoverFragment: RecoverFragment
    private lateinit var workoutFinishWorkoutFragment: FinishWorkoutFragment


    private lateinit var workout: Workout
    private lateinit var exercises: List<Exercise>
    private var currentExercise: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)
        workout = Gson().fromJson(intent.getStringExtra("workout"),Workout::class.java)

        ThreadUtils.execute(this, { db.getExerciseByWorkoutID(workout.id!!,true,applicationContext) }, { exercises ->
            this.exercises = exercises as List<Exercise>
            displayNextExercise()
        })

        createFragments()
        binding.statusBar.setTitle(workout.name!!)
        exerciseFragment.setEventListener(object : ExerciseFragment.ExerciseEvent {
            override fun onFinish() {

                if(exercises.size > currentExercise) {
                    recoverFragment.setExercises(exercises[currentExercise - 1],exercises[currentExercise], DEFAULT_RECOVER_TIME)
                    switchFragments(RECOVER_FRAGMENT)
                }
                else {
                    recoverFragment.setExercises(exercises[currentExercise - 1], null,0)
                    switchFragments(RECOVER_FRAGMENT)
                }
            }
        })

        recoverFragment.setEventListener(object : RecoverFragment.RecoverEvent {
            override fun onStart() {
                displayNextExercise()
            }
        })
    }

    private fun displayNextExercise() {

        if(exercises.size == currentExercise) {
            //Finish
            workoutFinishWorkoutFragment = FinishWorkoutFragment()
            switchFragments(FINISH_FRAGMENT)
        }
        else
        {
            exerciseFragment.setExercise(exercises[currentExercise])
            switchFragments(EXERCISE_FRAGMENT)
            currentExercise++
        }

    }

    private fun switchFragments(index : Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment: Fragment? = when(index) {
            EXERCISE_FRAGMENT -> exerciseFragment
            RECOVER_FRAGMENT -> recoverFragment
            FINISH_FRAGMENT -> workoutFinishWorkoutFragment
            else -> null
        }
        fragmentTransaction.replace(R.id.contentFrame, fragment!!)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun createFragments() {
        exerciseFragment = ExerciseFragment()
        recoverFragment = RecoverFragment()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    var backCount: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() / 1000 - backCount < 3) {
            finish()
        } else {
            backCount = System.currentTimeMillis() / 1000
            Toast.makeText(this, "Press one more time to exit the workout.", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG = "WorkoutActivity"
        private const val EXERCISE_FRAGMENT = 0
        private const val RECOVER_FRAGMENT = 1
        private const val FINISH_FRAGMENT = 2
        private const val DEFAULT_RECOVER_TIME = 30
    }
}