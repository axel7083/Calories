package com.github.calories.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.calories.activities.WorkoutActivity
import com.github.calories.adapters.DetailedExerciseAdapter
import com.github.calories.databinding.FragmentWorkoutBinding
import com.github.calories.models.Exercise

class WorkoutFragment : Fragment() {

    private lateinit var binding: FragmentWorkoutBinding
    lateinit var exercises: List<Exercise>
    private lateinit var detailedExerciseAdapterTodo: DetailedExerciseAdapter
    private lateinit var detailedExerciseAdapterDone: DetailedExerciseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentWorkoutBinding.inflate(inflater, container, false);

        binding.todo.layoutManager = LinearLayoutManager(context)
        binding.done.layoutManager = LinearLayoutManager(context)

        detailedExerciseAdapterTodo = DetailedExerciseAdapter(context, (activity as WorkoutActivity))
        detailedExerciseAdapterDone = DetailedExerciseAdapter(context, null)

        binding.todo.adapter = detailedExerciseAdapterTodo
        binding.done.adapter = detailedExerciseAdapterDone

        // Inflate the layout for this fragment
        return binding.root
    }


    fun updateAdapters() {
        val listTodo = ArrayList<Exercise>()
        val listDone = ArrayList<Exercise>()

        for(exercise in exercises) {
            if(exercise.doneToday)
                listDone.add(exercise)
            else
                listTodo.add(exercise)
        }

        binding.titleTodo.visibility = if(listTodo.size == 0) View.GONE else View.VISIBLE
        detailedExerciseAdapterTodo.updateData(listTodo)

        binding.titleDone.visibility = if(listDone.size == 0) View.GONE else View.VISIBLE

        detailedExerciseAdapterDone.updateData(listDone)

    }

    public fun updateExercises(exercises: List<Exercise>) {
        this.exercises = exercises
        if(::binding.isInitialized) {
            updateAdapters()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(::exercises.isInitialized) {
            updateAdapters()
        }
    }

    companion object {
        private const val TAG: String = "WorkoutFragment"
    }
}