package com.github.calories.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.calories.DatabaseHelper
import com.github.calories.R
import com.github.calories.adapters.DetailedExerciseAdapter
import com.github.calories.databinding.ActivityWorkoutBinding
import com.github.calories.fragments.*
import com.github.calories.models.Exercise
import com.github.calories.models.ExerciseInput
import com.github.calories.models.Workout
import com.github.calories.utils.ThreadUtils
import com.google.gson.Gson

class WorkoutActivity : AppCompatActivity(), DetailedExerciseAdapter.ClickEvent {

    private lateinit var binding: ActivityWorkoutBinding
    lateinit var db: DatabaseHelper

    // Fragments
    private lateinit var exerciseFragment: ExerciseFragment
    private lateinit var workoutFragment: WorkoutFragment

    private lateinit var workout: Workout
    private var updated: Boolean = false
    private lateinit var currentExercise: Exercise


    private fun setupWorkout() {
        ThreadUtils.execute(this, { db.getExerciseByWorkoutID(workout.id!!,true,applicationContext) }, { exercises ->
            workout.exercises = exercises as List<Exercise>
            workoutFragment.updateExercises(exercises)
        })

        binding.statusBar.setTitle(workout.name!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)
        workout = Gson().fromJson(intent.getStringExtra("workout"),Workout::class.java)

        createFragments()
        setupWorkout()

        binding.statusBar.setLeftIconClickListener {
            finish()
        }

        binding.statusBar.setRightIconClickListener {
            if(currentFragment == WORKOUT_FRAGMENT) {
                val intent = Intent(this@WorkoutActivity, CreateWorkoutActivity::class.java)

                Log.d(TAG, "onCreate: workout exercise size: " + workout.exercises?.size)

                intent.putExtra("workout", Gson().toJson(workout))
                startActivityForResult(
                    intent,
                    MainActivity.CREATE_WORKOUT_ACTIVITY
                )
            }
            else
            {
                val intent = Intent(this@WorkoutActivity, StatsActivity::class.java)
                intent.putExtra("workoutId", currentExercise.id)
                startActivity(intent)
                //TODO: handle show stats (another activity)
            }
        }

        exerciseFragment.setEventListener(object : ExerciseFragment.ExerciseEvent {
            override fun onFinish(inputs: List<ExerciseInput>?) {

                System.out.println(TAG + " onFinish received " + inputs?.size)
                if(inputs != null) {
                    for(input in inputs) {
                        db.addExerciseRecord(input)

                    }
                }

                currentExercise.doneToday = true
                //workoutFragment.updateAdapters()
                switchFragments(WORKOUT_FRAGMENT)
            }

            override fun onCancel() {
                switchFragments(WORKOUT_FRAGMENT)
            }

            override fun onSetTitle(str: String) {
                binding.statusBar.setTitle(str)
            }
        })

        switchFragments(WORKOUT_FRAGMENT)
    }

    private var currentFragment = -1

    private fun switchFragments(index : Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment: Fragment? = when(index) {
            EXERCISE_FRAGMENT -> {
                currentFragment = EXERCISE_FRAGMENT
                binding.statusBar.setRightIcon(ContextCompat.getDrawable(this, R.drawable.ic_statistics))
                exerciseFragment
            }
            WORKOUT_FRAGMENT -> {
                currentFragment = WORKOUT_FRAGMENT
                binding.statusBar.setTitle(workout.name!!)
                binding.statusBar.setRightIcon(ContextCompat.getDrawable(this, R.drawable.ic_pencil))
                workoutFragment
            }
            else -> null
        }
        fragmentTransaction.replace(R.id.contentFrame, fragment!!)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun createFragments() {
        exerciseFragment = ExerciseFragment()
        workoutFragment = WorkoutFragment()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onBackPressed() {
        if(updated)
            setResult(RESULT_OK)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == MainActivity.CREATE_WORKOUT_ACTIVITY && resultCode == RESULT_OK) {
            if(data != null) {
                workout = Gson().fromJson(data.getStringExtra("workout"), Workout::class.java)

                if(workout.exercises == null) {
                    finish()
                    return
                }
                updated = true
                workoutFragment.updateExercises(workout.exercises!!)
                binding.statusBar.setTitle(workout.name!!)
            }
            else {
                setResult(RESULT_OK)
                finish()
            }

        }
    }

    companion object {
        private const val TAG = "WorkoutActivity"
        private const val WORKOUT_FRAGMENT = 0
        private const val EXERCISE_FRAGMENT = 1
    }

    override fun onClick(exercise: Exercise) {
        currentExercise = exercise
        exerciseFragment.setExercise(exercise)
        switchFragments(EXERCISE_FRAGMENT)
    }
}