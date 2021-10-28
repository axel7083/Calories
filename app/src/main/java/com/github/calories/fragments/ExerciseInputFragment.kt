package com.github.calories.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.calories.adapters.ExerciseInputAdapter
import com.github.calories.databinding.FragmentExerciseInputBinding
import com.github.calories.models.ExerciseInput


class ExerciseInputFragment(var exerciseId : Long) : Fragment() {

    private lateinit var binding: FragmentExerciseInputBinding
    public lateinit var exerciseInputAdapter: ExerciseInputAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentExerciseInputBinding.inflate(inflater, container, false)

        binding.rvWeight.layoutManager = LinearLayoutManager(context)
        exerciseInputAdapter = ExerciseInputAdapter(context)
        binding.rvWeight.adapter = exerciseInputAdapter

        // Define one by default
        exerciseInputAdapter.updateData(listOf(ExerciseInput(0,0, exerciseId)))

        binding.addBtn.setOnClickListener {
            exerciseInputAdapter.updateData(exerciseInputAdapter.data.plus(ExerciseInput(0,0, exerciseId)))
        }

        // Inflate the layout for this fragment
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    companion object {
        private const val TAG: String = "ExerciseInputFragment"
    }
}