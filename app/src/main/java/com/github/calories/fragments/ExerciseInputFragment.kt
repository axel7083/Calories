package com.github.calories.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.github.calories.adapters.SliderAdapter
import com.github.calories.databinding.FragmentExerciseInputBinding
import com.github.calories.utils.ScreenUtils
import com.github.calories.utils.SliderLayoutManager


class ExerciseInputFragment : Fragment() {

    private lateinit var binding: FragmentExerciseInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentExerciseInputBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHorizontalPicker(binding.rvRepetition,1,10,2)
        setHorizontalPicker(binding.rvWeight,0,200,12)
    }

    private fun setHorizontalPicker(rv: RecyclerView, start: Int, end: Int, pos: Int) {
        // Setting the padding such that the items will appear in the middle of the screen
        val padding: Int = ScreenUtils.getScreenWidth(requireContext())/2 - ScreenUtils.dpToPx(requireContext(), 40)
        rv.setPadding(padding, 0, padding, 0)

        // Setting layout manager
        rv.layoutManager = SliderLayoutManager(requireContext()).apply {
            callback = object : SliderLayoutManager.OnItemSelectedListener {
                override fun onItemSelected(layoutPosition: Int) {
                    //tvSelectedItem.setText(data[layoutPosition])
                }
            }
        }

        // Setting Adapter
        rv.adapter = SliderAdapter().apply {
            setData(getData(start, end))
            callback = object : SliderAdapter.Callback {
                override fun onItemClicked(view: View) {
                    rv.smoothScrollToPosition(rv.getChildLayoutPosition(view))
                }
            }
        }

        rv.scrollToPosition(pos)
    }


    /*private fun setHorizontalPicker() {
        // Setting the padding such that the items will appear in the middle of the screen
        val padding: Int = ScreenUtils.getScreenWidth(requireContext())/2 - ScreenUtils.dpToPx(requireContext(), 40)
        binding.rvRepetition.setPadding(padding, 0, padding, 0)

        // Setting layout manager
        binding.rvRepetition.layoutManager = SliderLayoutManager(requireContext()).apply {
            callback = object : SliderLayoutManager.OnItemSelectedListener {
                override fun onItemSelected(layoutPosition: Int) {
                    //tvSelectedItem.setText(data[layoutPosition])
                }
            }
        }

        // Setting Adapter
        binding.rvRepetition.adapter = SliderAdapter().apply {
            setData(getData(10))
            callback = object : SliderAdapter.Callback {
                override fun onItemClicked(view: View) {
                    binding.rvRepetition.smoothScrollToPosition(binding.rvRepetition.getChildLayoutPosition(view))
                }
            }
        }
    }*/

    private fun getData(start: Int, end : Int): ArrayList<String> {
        val data: ArrayList<String> = ArrayList()
        for (i in start until end) {
            data.add(i.toString())
        }
        return data
    }

    companion object {
        private const val TAG: String = "FinishWorkoutFragment"
    }
}