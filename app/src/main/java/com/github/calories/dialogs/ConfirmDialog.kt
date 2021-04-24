package com.github.calories.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.Window
import android.view.inputmethod.InputMethodManager
import com.github.calories.databinding.DialogConfirmBinding
import com.github.calories.databinding.DialogInputBinding

class ConfirmDialog(activity: Activity,
                    private val callback: (Boolean) -> Unit,
                    private val title: String,
                    private val description: String,
                    private val pos: String = "Yes",
                    private val neg: String = "No") : Dialog(activity), View.OnClickListener {

    private lateinit var binding: DialogConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DialogConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPos.text = pos
        binding.btnPos.setOnClickListener {
            callback(true)
            dismiss()
        }

        binding.btnNeg.text = neg
        binding.btnNeg.setOnClickListener {
            callback(false)
            dismiss()
        }

        binding.title.text = title
        binding.description.text = description
    }

    override fun onClick(v: View) {}
}