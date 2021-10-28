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
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.calories.DatabaseHelper
import com.github.calories.adapters.CategoryAdapter
import com.github.calories.databinding.ActivityCreateExerciseBinding
import com.github.calories.dialogs.ConfirmDialog
import com.github.calories.dialogs.InputDialog
import com.github.calories.models.Category
import com.github.calories.models.Exercise
import com.github.calories.models.RawValues
import com.github.calories.utils.StorageUtils.saveToInternalStorage
import com.github.calories.utils.ThreadUtils
import com.github.calories.utils.UtilsTime
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

class CreateExerciseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateExerciseBinding
    private lateinit var adapter: CategoryAdapter
    // SQLite Database
    private lateinit var db: DatabaseHelper

    private var exercise: Exercise = Exercise()

    var list: List<Category> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)


        binding.statusBar.setLeftIconClickListener {
            finish()
        }

        binding.cvImage.setOnClickListener {
            val i = Intent(application, CroppingActivity::class.java)
            startActivityForResult(i, CROPPING_ACTIVITY)
        }

        adapter = CategoryAdapter(this)
        adapter.setOnLongClickListener { category ->

            val dialog = ConfirmDialog(this, { output ->
                if(output)
                    ThreadUtils.execute(this@CreateExerciseActivity, {db.deleteCategory(category) }, {
                        list = list.minus(category)
                        adapter.deleteCategory(category)
                        adapter.updateData(list)
                        adapter.notifyDataSetChanged()
                    })
            },"Delete ${category.name}","Are you sure you want to delete this category?")
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
        binding.categoryRV.adapter = adapter
        binding.categoryRV.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        binding.addCategory.setOnClickListener {
            val dialog = InputDialog(this, { categoryName ->
                ThreadUtils.execute(this@CreateExerciseActivity, {db.addCategory(Category(categoryName,null)) }, { category ->
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
        }

        // Fetching data
        ThreadUtils.execute(this@CreateExerciseActivity, { db.categories }, { categories ->
            list = categories as List<Category>
            Log.d(TAG, "onCreate: list size ${list.size}")
            adapter.updateData(list)
            adapter.notifyDataSetChanged()
        })

        binding.btnSave.setOnClickListener {
            val name = binding.nameEdit.text.toString()
            if(name.isEmpty()) {
                Toast.makeText(this@CreateExerciseActivity,"Name should not be empty.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(exercise.image == null) {
                Toast.makeText(this@CreateExerciseActivity,"You need to put an image.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            exercise.name = name
            exercise.categories = adapter.selected
            Log.d(TAG, "onCreate: ${exercise.categories?.size}")

            ThreadUtils.execute(this@CreateExerciseActivity, { db.addExercise(exercise) }, { exercise ->
                val b = saveToInternalStorage((exercise as Exercise).image, this@CreateExerciseActivity, "exercise_${exercise.id}.jpg")

                Log.d(TAG, "onCreate: Saving to storage success: $b")

                val returnIntent = Intent()
                returnIntent.putExtra("exercise", Gson().toJson(exercise))
                setResult(RESULT_OK, returnIntent)
                finish()
            })

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CROPPING_ACTIVITY -> {
                if (resultCode == RESULT_OK && data != null) {
                    val byteArray = data.getByteArrayExtra("bitmap")
                    val userImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

                    binding.imageContainer.setPadding(0)
                    binding.imageContainer.clearColorFilter()
                    binding.imageContainer.setImageBitmap(userImage)
                    exercise.image = userImage
                }
            }
        }
    }

    companion object {
        private const val CROPPING_ACTIVITY = 0;
        private const val TAG = "CreateExerciseActivity"
    }
}