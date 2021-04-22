package com.github.calories.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.github.calories.R
import com.github.calories.databinding.ViewStatusBarBinding

class StatusBar(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs){

    private val binding: ViewStatusBarBinding = ViewStatusBarBinding.bind(inflate(context, R.layout.view_status_bar, this))

    init {
        val attributes: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusBar)

        // Setup Right icon (Example: setting icon)
        setDrawableOrNot(attributes.getDrawable(R.styleable.StatusBar_rightIcon),binding.rightIcon,binding.rightIconContainer)

        // Setup Left icon (Example: back arrow)
        setDrawableOrNot(attributes.getDrawable(R.styleable.StatusBar_leftIcon),binding.leftIcon,binding.leftIconContainer)

        binding.title.text = attributes.getText(R.styleable.StatusBar_title)?: context.getText(R.string.app_name)
    }

    private fun setDrawableOrNot(drawable: Drawable?, view: ImageView, container: CardView) {
        if(drawable == null)
            container.visibility = View.GONE
        else
            container.visibility = View.VISIBLE
        view.setImageDrawable(drawable)

    }

    fun setRightIconClickListener(onRightIconClickListener: OnClickListener?) {
        binding.rightIcon.setOnClickListener(onRightIconClickListener)
    }

    fun setLeftIconClickListener(onLeftIconClickListener: OnClickListener?) {
        binding.leftIcon.setOnClickListener(onLeftIconClickListener)
    }

    fun setTitle(str: String) {
        binding.title.text = str
    }
}