package com.github.calories.models

class Record(val id: Long, val date: String) {
    var foods: List<Food>? = null

    fun addFood(food: Food) {
        if(foods == null)
            foods = ArrayList<Food>()

        foods = foods!!.plus(food)
    }

    fun removeFood(food: Food): List<Food>? {
        if(foods == null)
            return null

        foods = foods!!.minus(food)
        return foods
    }
}