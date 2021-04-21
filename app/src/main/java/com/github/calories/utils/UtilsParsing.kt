package com.github.calories.utils

import com.github.calories.models.Food
import com.github.calories.models.Ingredient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object UtilsParsing {
    fun extractFood(product: JSONObject): Food {
        val nutriments: JSONObject = product.getJSONObject("nutriments")
        val ingredients: JSONArray? = product.optJSONArray("ingredients")

        return Food(
                product.get("_id") as String,
                product.get("product_name") as String,
                getEnergy(nutriments),
                nutriments.optDouble("fat_100g", 0.0),
                nutriments.optDouble("fiber_100g", 0.0),
                nutriments.optDouble("proteins_100g", 0.0),
                nutriments.optDouble("salt_100g", 0.0),
                nutriments.optDouble("saturated-fat_100g", 0.0),
                nutriments.optDouble("sodium_100g", 0.0),
                nutriments.optDouble("sugars_100g", 0.0),
                extractIngredients(ingredients)
        )
    }

    private fun getEnergy(nutriments: JSONObject): Int {

        var value = nutriments.optInt("energy-kcal_100g", -1)

        if(value == -1) {
            value = nutriments.optInt("energy-kj_100g", -1)
            if(value == -1)
                return 0

            val unit = nutriments.optString("energy-kj_unit")
            value = when(unit) {
                "kJ" -> (value*0.239006).toInt()
                else -> 0
            }
        }


        return value
    }

    private fun extractIngredients(array: JSONArray?): List<Ingredient> {
        var ingredients: List<Ingredient> = ArrayList()

        if(array == null)
            return ingredients

        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            ingredients = ingredients.plus(Ingredient(item.optString("id"),item.optString("text"), item.optDouble("percent_estimate")))
        }

        return ingredients
    }

    fun parse(json: String): JSONObject? {
        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(json)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }
}