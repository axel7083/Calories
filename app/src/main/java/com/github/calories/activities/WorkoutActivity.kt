package com.github.calories.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.calories.databinding.ActivityAddBinding
import com.github.calories.databinding.ActivityWorkoutBinding

class WorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}