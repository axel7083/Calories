package com.github.calories.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.Window
import android.view.inputmethod.InputMethodManager
import com.github.calories.databinding.DialogInputBinding

class InputDialog(private val activity: Activity, private val callback: (String) -> Unit, val title: String, private val inputType: Int, private val unit: String? = null) : Dialog(activity), View.OnClickListener {

    private lateinit var binding: DialogInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DialogInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            callback(binding.weightValue.text.toString())
            dismiss()
        }

        binding.title.text = title

        if(unit == null)
            binding.unitCV.visibility = GONE
        else
            binding.unit.text = unit

        binding.weightValue.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
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

    companion object {
        private const val TAG = "VersionDialog"
    }
}