package com.github.calories.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.calories.openFoodFact.ProductByID
import com.github.calories.utils.UtilsTime.formatSQL
import com.github.calories.adapters.FoodAdapter
import com.github.calories.adapters.FoodsSearchAdapter
import com.github.calories.databinding.ActivityAddBinding
import com.github.calories.models.Food
import com.github.calories.models.Record
import com.github.calories.openFoodFact.ProductsByQuery
import com.github.calories.utils.ThreadUtils
import com.google.gson.Gson
import java.util.*

class AddRecordActivity : AppCompatActivity(), ProductByID.Callback,
    FoodAdapter.ItemClickListener {

    private lateinit var binding: ActivityAddBinding
    private lateinit var foodAdapter: FoodAdapter

    private lateinit var record: Record
    private var ignoreResults : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        record = Record(-1,formatSQL(calendar.toInstant(), TimeZone.getDefault().id))
        Log.d(TAG, "onCreate: ${record.date}")

        binding.plusAdd.setOnClickListener {
            startActivityForResult(
                Intent(this, ScannerActivity::class.java),
                MainActivity.SCAN_ACTIVITY
            )
        }

        binding.btnSave.setOnClickListener {
            if(foodAdapter.data == null || foodAdapter.data.isEmpty()) {
                Toast.makeText(this,"Nothing selected",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent()
            record.foods = foodAdapter.data
            intent.putExtra("record", Gson().toJson(record))
            setResult(RESULT_OK,intent)
            finish()
        }

        binding.btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED,intent)
            finish()
        }

        binding.searchOnline.setOnClickListener {
            val i = Intent(this, HistoryActivity::class.java)
            i.putExtra("isInternalSearch",false)
            startActivityForResult(i, HISTORY_ACTIVITY)
        }

        binding.statusBar.setRightIconClickListener {
            val i = Intent(this, HistoryActivity::class.java)
            startActivityForResult(i, HISTORY_ACTIVITY)
        }

        setupAdapters()
    }

    private fun setupAdapters() {
        // set up food record adapter
        binding.rvFood.layoutManager = LinearLayoutManager(this)
        foodAdapter = FoodAdapter(this)
        foodAdapter.setClickListener(this)
        foodAdapter.updateData(record.foods)
        binding.rvFood.adapter = foodAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("AddActivity", "onActivityResult $resultCode")

        if (resultCode == RESULT_CANCELED || data == null)
            return

        when(requestCode) {
            MainActivity.SCAN_ACTIVITY -> {
                    val number = data.getStringExtra("value")
                    Toast.makeText(this, "Value = $number", Toast.LENGTH_LONG).show()
                    Thread(ProductByID(number!!, this)).start()
            }
            HISTORY_ACTIVITY -> {
                val food = data.getStringExtra("food")
                onProductByID(Gson().fromJson(food,Food::class.java))
            }
        }
    }

    override fun onProductByID(food: Food) {
        Log.d("MainActivity", "FOOD: $food")

        // try to touch View of UI thread
        this.runOnUiThread {
            record.addFood(food)
            foodAdapter.updateData(record.foods)
            foodAdapter.notifyDataSetChanged()
        }
    }

    // Callback from FoodAdapter
    override fun onRemove(food: Food?) {
        if(food == null)
            return
        record.removeFood(food)
        foodAdapter.updateData(record.foods)
        foodAdapter.notifyDataSetChanged()
    }

    override fun onOpen(food: Food?) {
        val intent = Intent(this, IngredientsDetailsActivity::class.java)
        intent.putExtra("food",Gson().toJson(food))
        startActivity(intent)
    }

    companion object {
        private const val TAG: String = "AddActivity"
        private const val HISTORY_ACTIVITY: Int = 1
    }
}