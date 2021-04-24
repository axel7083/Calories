package com.github.calories.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.calories.DatabaseHelper
import com.github.calories.adapters.FoodsSearchAdapter
import com.github.calories.databinding.ActivityHistoryBinding
import com.github.calories.models.Food
import com.github.calories.models.Record
import com.github.calories.openFoodFact.ProductsByQuery
import com.github.calories.utils.ThreadUtils
import com.google.gson.Gson

class HistoryActivity : AppCompatActivity(), ProductsByQuery.Callback {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: FoodsSearchAdapter
    private var isInternalSearch : Boolean = true
    private var ignoreResults : Boolean = false

    private lateinit var mostEatenFoods: List<Food>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)
        adapter = FoodsSearchAdapter(this)

        isInternalSearch = intent.getBooleanExtra("isInternalSearch", true)
        binding.statusBar.setTitle(if(isInternalSearch) "History" else "Search online")

        binding.statusBar.setLeftIconClickListener {
            finish()
        }

        binding.foodsRV.adapter = adapter
        binding.foodsRV.layoutManager = LinearLayoutManager(this)

        adapter.setClickListener { food ->
            if(food.energyKCAL100g == null) {
                ThreadUtils.execute(this, {
                    db.getFood(food.id)
                }, { food ->
                    onFinish(food as Food)
                })
            }
            else
                onFinish(food as Food)
            false
        }

        if(isInternalSearch)
            setupInternalSearch()
        else
            setupExternalSearch()
    }

    private fun onFinish(food: Food) {
        val intent = Intent(this, IngredientsDetailsActivity::class.java)
        intent.putExtra("food", Gson().toJson(food))
        setResult(RESULT_OK,intent)
        finish()
    }

    private fun setupInternalSearch() {
        fetchInternal()

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.subtitle.visibility = View.GONE
                if(s== null || s.isEmpty()) {
                    adapter.clear()
                    ignoreResults = true
                    binding.progressIndicator.visibility = View.GONE
                    fetchInternal()
                    return
                }
                else
                    ignoreResults = false

                if(s.length < 3)
                    return

                binding.progressIndicator.visibility = View.VISIBLE
                ThreadUtils.execute(this@HistoryActivity, {
                    db.getFoodByQuery(s.toString())
                }, { foods ->

                    if(!ignoreResults) {
                        adapter.updateData(foods as List<Food>)
                        adapter.notifyDataSetChanged()
                        binding.progressIndicator.visibility = View.GONE
                    }

                })

            }
        })
    }

    private fun fetchInternal() {
        binding.subtitle.visibility = View.VISIBLE
        binding.subtitle.text = "Most eaten food"
        ThreadUtils.execute(this, {
            db.getMostEatenFood(LIMIT)
        }, { foods ->
            mostEatenFoods = foods as List<Food>
            adapter.updateData(mostEatenFoods)
            adapter.notifyDataSetChanged()
        })
    }

    private fun setupExternalSearch() {
        binding.subtitle.visibility = View.GONE
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s== null || s.isEmpty()) {
                    adapter.clear()
                    ignoreResults = true
                    binding.progressIndicator.visibility = View.GONE
                    return
                }
                else
                    ignoreResults = false

                if(s.length < 3)
                    return

                binding.progressIndicator.visibility = View.VISIBLE
                Thread(ProductsByQuery(s.toString(), LIMIT,this@HistoryActivity)).start()
            }
        })
    }

    companion object {
        private const val LIMIT: Int = 15
    }

    override fun onProductsByQuery(foods: List<Food>?) {
        runOnUiThread {
            if(!ignoreResults) {
                binding.progressIndicator.visibility = View.GONE
                adapter.updateData(foods)
                adapter.notifyDataSetChanged()
            }
        }
    }
}