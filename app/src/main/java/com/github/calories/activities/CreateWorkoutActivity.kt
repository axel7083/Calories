package com.github.calories.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.calories.DatabaseHelper
import com.github.calories.adapters.CategoryAdapter
import com.github.calories.adapters.ExerciseAdapter
import com.github.calories.adapters.WorkoutAdapter
import com.github.calories.databinding.ActivityCreateExerciseBinding
import com.github.calories.databinding.ActivityCreateWorkoutBinding
import com.github.calories.dialogs.ConfirmDialog
import com.github.calories.dialogs.InputDialog
import com.github.calories.models.Category
import com.github.calories.models.Exercise
import com.github.calories.models.RawValues
import com.github.calories.models.Workout
import com.github.calories.utils.StorageUtils.saveToInternalStorage
import com.github.calories.utils.ThreadUtils
import com.github.calories.utils.UtilsTime
import com.google.gson.Gson
import java.lang.NumberFormatException
import java.util.*
import kotlin.collections.ArrayList

class CreateWorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateWorkoutBinding
    private lateinit var adapter: ExerciseAdapter
    // SQLite Database
    private lateinit var db: DatabaseHelper

    private lateinit var workout: Workout

    var list: List<Exercise> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // load workout if we are editing it
        val json = intent.getStringExtra("workout");
        workout = if(json != null) Gson().fromJson(json, Workout::class.java) else Workout()

        if(workout.name != null)
            binding.nameEdit.setText(workout.name)

        if(workout.id == null) {
            binding.statusBar.setRightIcon(null)
        }
        else
        {
            binding.statusBar.setTitle("Edit workout")

            binding.statusBar.setRightIconClickListener {
                ThreadUtils.execute(this@CreateWorkoutActivity, { db.deleteWorkout(workout) }, {
                    Log.d(TAG, "Workout deleted")
                    setResult(RESULT_OK)
                    finish()
                })
            }
        }


        db = DatabaseHelper(this)

        binding.statusBar.setLeftIconClickListener {
            finish()
        }

        adapter = ExerciseAdapter(this)

        adapter.setLongClickListener {
            val dialog = ConfirmDialog(this, { output ->
                if(output)
                    ThreadUtils.execute(this@CreateWorkoutActivity, {db.deleteExercise(it) }, {
                        fetchData()
                    })
            },"Delete ${it.name}","Are you sure you want to delete this exercise? It will delete all records made in this exercise.")
            val window = dialog.window
            if (window != null) {
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
                window.setGravity(Gravity.CENTER)
                window.setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
        }

        fetchData()

        binding.exerciseRV.adapter = adapter
        binding.exerciseRV.layoutManager = GridLayoutManager(this, 2)

        binding.addExercise.setOnClickListener {
            startActivityForResult(Intent(this,CreateExerciseActivity::class.java),CREATE_EXERCISE_ACTIVITY)
        }

        binding.btnSave.setOnClickListener {
            val name = binding.nameEdit.text.toString()
            if(name.isEmpty()) {
                Toast.makeText(this@CreateWorkoutActivity,"Name should not be empty.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            workout.name = name
            workout.exercises = adapter.selected
            
            if(workout.exercises!!.isEmpty()) {
                Toast.makeText(this@CreateWorkoutActivity,"Select one exercise at least.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(workout.id != null) {
                ThreadUtils.execute(this@CreateWorkoutActivity, { db.updateWorkout(workout) }, { updatedWorkout ->

                    Log.d(TAG, "onCreate: Saving to storage success")
                    val intent = Intent()
                    intent.putExtra("workout", Gson().toJson(updatedWorkout as Workout))
                    setResult(RESULT_OK, intent)
                    finish()
                })
            }
            else
            {
                ThreadUtils.execute(this@CreateWorkoutActivity, { db.addWorkout(workout) }, {
                    Log.d(TAG, "onCreate: Saving to storage success")
                    setResult(RESULT_OK)
                    finish()
                })
            }
        }
    }

    private fun fetchData() {
        // Fetching data
        ThreadUtils.execute(this@CreateWorkoutActivity, { db.exercises }, { exercises ->


            list = exercises as List<Exercise>

            list.forEach { exercise ->
                run {
                    Log.d(TAG, "onCreate: loading bitmap for id " + exercise.id)
                    exercise.loadBitmap(this@CreateWorkoutActivity)
                }
            }

            workout.exercises?.forEach { exercise ->
                adapter.setSelected(exercise, true)
                adapter.notifyDataSetChanged()
            }

            Log.d(TAG, "onCreate: db.exercises => ${list.size}" )

            adapter.updateData(list)
            adapter.notifyDataSetChanged()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CREATE_EXERCISE_ACTIVITY -> {
                if (resultCode == RESULT_OK && data != null) {
                    val json = data.getStringExtra("exercise")
                    val exercise = Gson().fromJson(json,Exercise::class.java)
                    list = list.plus(exercise)
                    adapter.updateData(list)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    var backCount: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() / 1000 - backCount < 3) {
            finish()
        } else {
            backCount = System.currentTimeMillis() / 1000
            Toast.makeText(this, "Press one more time to exit.", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG = "CreateExerciseActivity"
        private const val CREATE_EXERCISE_ACTIVITY = 1
    }
}