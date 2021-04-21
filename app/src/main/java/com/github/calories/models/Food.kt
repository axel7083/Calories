package com.github.calories.models

class Food(var id: String,
           val name: String,
           val energyKCAL100g: Int,
           val fat100g: Double,
           val fiber100g: Double,
           val proteins100g: Double,
           val salt100g: Double,
           val saturatedFat100g: Double,
           val sodium100g: Double,
           val sugars100g:Double,
           var ingredients: List<Ingredient>?) {

    var quantity: Int = 100
    var unit: String = "g"
}