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
import com.github.calories.dialogs.InputDialog
import com.github.calories.models.Category
import com.github.calories.models.Exercise
import com.github.calories.models.RawValues
import com.github.calories.models.Workout
import com.github.calories.utils.StorageUtils.saveToInternalStorage
import com.github.calories.utils.ThreadUtils
import com.github.calories.utils.UtilsTime
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

class CreateWorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateWorkoutBinding
    private lateinit var adapter: ExerciseAdapter
    // SQLite Database
    private lateinit var db: DatabaseHelper

    private var workout: Workout = Workout()

    var list: List<Exercise> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)


        binding.statusBar.setLeftIconClickListener {
            finish()
        }

        adapter = ExerciseAdapter(this)
        binding.categoryRV.adapter = adapter
        binding.categoryRV.layoutManager = GridLayoutManager(this, 2)

        /*binding.addCategory.setOnClickListener {
            val dialog = InputDialog(this, { categoryName ->
                ThreadUtils.execute(this@CreateWorkoutActivity, {db.addCategory(Category(categoryName,null)) }, { category ->
                    list = list.plus(category as Category)
                    adapter.updateData(list)
                    adapter.notifyDataSetChanged()
                })
            },"Create a category", InputType.TYPE_CLASS_TEXT)
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
        }*/

        // Fetching data
        ThreadUtils.execute(this@CreateWorkoutActivity, { db.exercises }, { exercises ->

            list = exercises as List<Exercise>

            list.forEach { exercise ->
                run {
                    exercise.loadBitmap(this@CreateWorkoutActivity)
                }
            }

            Log.d(TAG, "onCreate: db.exercises => ${list.size}" )

            adapter.updateData(list)
            adapter.notifyDataSetChanged()
        })

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

            ThreadUtils.execute(this@CreateWorkoutActivity, { db.addWorkout(workout) }, { workout ->

                /*Log.d(TAG, "onCreate: Saving to storage success: $b")

                val returnIntent = Intent()
                returnIntent.putExtra("exercise", Gson().toJson(exercise))
                setResult(RESULT_OK, returnIntent)
                finish()*/
            })

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CREATE_EXERCISE_ACTIVITY -> {
                if (resultCode == RESULT_OK && data != null) {
                    val json = data.getStringExtra("exercise")
                    val exercise = Gson().fromJson(json,Exercise::class.java)

                    //TODO: ADD TO LIST AND SELECT IT => see in adapter
                }
            }
        }
    }

    companion object {
        private const val TAG = "CreateExerciseActivity"
        private const val CREATE_EXERCISE_ACTIVITY = 1
    }
}