package com.github.calories.models

class Food(var id: String,
           val name: String,
           val energyKCAL100g: Int? = null,
           val fat100g: Double? = null,
           val fiber100g: Double? = null,
           val proteins100g: Double? = null,
           val salt100g: Double? = null,
           val saturatedFat100g: Double? = null,
           val sodium100g: Double? = null,
           val sugars100g:Double? = null,
           var ingredients: List<Ingredient>? = null) {

    var quantity: Int = 100
    var unit: String = "g"
}