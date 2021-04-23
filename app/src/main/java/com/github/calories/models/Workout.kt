package com.github.calories.models

class Workout(var id: Long? = null, var name: String? = null, var exercises: List<Exercise>? = null) {

    fun addExercise(exercise: Exercise) {
        exercises = exercises!!.plus(exercise)
    }

    init {
        if(exercises == null)
            exercises = ArrayList()
    }
}