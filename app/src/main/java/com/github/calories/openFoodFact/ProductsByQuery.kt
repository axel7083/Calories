package com.github.calories.openFoodFact

import com.github.calories.models.Food
import com.github.calories.utils.UtilsParsing.extractFood
import com.github.calories.utils.UtilsParsing.parse
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL


class ProductsByQuery(private val query: String, private val callback: Callback): Runnable {

    override fun run() {
        val input: String = URL(URL + query).readText()
        val json: JSONObject = parse(input) ?: return

        val status: Int = json.getInt("count")

        if(status == 0) {
            callback.onProductsByQuery(null)
        }

        val products: JSONArray = json.getJSONArray("products")

        var foods: List<Food> = ArrayList()

        for (i in 0 until products.length()) {
            val item = products.getJSONObject(i)
            foods = foods.plus(extractFood(item))
        }

        callback.onProductsByQuery(foods)
    }

    companion object {
        const val URL : String = "https://world.openfoodfacts.org/cgi/search.pl?json=1&page_size=5&search_terms="
    }

    interface Callback {
        fun onProductsByQuery(foods: List<Food>?)
    }
}
