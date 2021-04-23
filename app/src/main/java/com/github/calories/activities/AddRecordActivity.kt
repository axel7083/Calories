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
import com.google.gson.Gson
import java.util.*

class AddRecordActivity : AppCompatActivity(), ProductByID.Callback, ProductsByQuery.Callback,
    FoodAdapter.ItemClickListener {

    private lateinit var binding: ActivityAddBinding
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var foodsSearchAdapter: FoodsSearchAdapter


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


        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG, "onTextChanged: $s")

                if(s== null || s.isEmpty()) {
                    foodsSearchAdapter.clear()
                    ignoreResults = true
                    return
                }
                else
                    ignoreResults = false

                if(s.length < 3)
                    return

                binding.progressIndicator.visibility = View.VISIBLE
                Thread(ProductsByQuery(s.toString(), this@AddRecordActivity)).start()
            }
        })

        binding.statusBar.setRightIconClickListener {
            val i = Intent(this, HistoryActivity::class.java)
            startActivity(i)
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

        // Setup research adapter
        binding.rvSearchResults.layoutManager = LinearLayoutManager(this)
        foodsSearchAdapter = FoodsSearchAdapter(this)
        foodsSearchAdapter.setClickListener { food: Food ->
            record.addFood(food)
            foodAdapter.addFood(food)
            foodAdapter.notifyDataSetChanged()
            true
        }
        binding.rvSearchResults.adapter = foodsSearchAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("AddActivity", "onActivityResult $resultCode")

        if(requestCode == MainActivity.SCAN_ACTIVITY) {
            if (resultCode == RESULT_CANCELED || data == null)
                return
            else
            {
                val number = data.getStringExtra("value")
                Toast.makeText(this, "Value = $number", Toast.LENGTH_LONG).show()
                Thread(ProductByID(number!!, this)).start()
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

    override fun onProductsByQuery(foods: List<Food>?) {
        if(ignoreResults)
            return

        runOnUiThread {
            binding.progressIndicator.visibility = View.GONE
            foodsSearchAdapter.updateData(foods)
            foodsSearchAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        private const val TAG: String = "AddActivity"
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


}