package com.github.calories.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.calories.DatabaseHelper
import com.github.calories.adapters.IngredientAdapter
import com.github.calories.databinding.ActivityIngDetailsBinding
import com.github.calories.models.Food
import com.google.gson.Gson

class FoodDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIngDetailsBinding

    lateinit var db: DatabaseHelper
    lateinit var food: Food
    lateinit var adapter: IngredientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIngDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        food = Gson().fromJson(intent.getStringExtra("food"), Food::class.java)

        binding.statusBar.setTitle(food.name)

        binding.rvFood.layoutManager = LinearLayoutManager(this)
        adapter = IngredientAdapter(this)
        adapter.updateData(food.ingredients)
        binding.rvFood.adapter = adapter

        binding.btnClose.setOnClickListener {
            finish()
        }
    }
}