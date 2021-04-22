package com.github.calories.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import com.github.calories.databinding.DialogWeightBinding

class WeightDialog(private val activity: Activity,private val callback: Callback) : Dialog(activity!!), View.OnClickListener {

    private lateinit var binding: DialogWeightBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DialogWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            callback.addWeight(binding.weightValue.text.toString().toFloat())
            dismiss()
        }

        binding.weightValue.requestFocus()
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

    }

    override fun onClick(v: View) {}
    override fun dismiss() {
        Log.d(TAG, "dismiss")

        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.weightValue.windowToken, 0)

        super.dismiss()
    }

    interface Callback {
        fun addWeight(weight: Float)
    }

    companion object {
        private const val TAG = "VersionDialog"
    }
}