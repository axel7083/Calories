package com.github.calories.openFoodFact

import com.github.calories.models.Food
import com.github.calories.utils.UtilsParsing
import com.github.calories.utils.UtilsParsing.parse
import org.json.JSONObject
import java.net.URL


class ProductByID(val id: String, private val callback: Callback): Runnable {

    override fun run() {
        val input: String = URL(URL + id).readText()
        val json: JSONObject = parse(input) ?: return

        val status: Int = json.get("status") as Int

        if(status != 1)
            return

        val product: JSONObject = json.getJSONObject("product")

        callback.finish(
                UtilsParsing.extractFood(product)
        )
    }

    companion object {
        const val URL : String = "https://world.openfoodfacts.org/api/v0/product/"
    }

    interface Callback {
        fun finish(food: Food)
    }
}
