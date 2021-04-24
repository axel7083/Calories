package com.github.calories.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class Exercise(var id: Long? = null, var name: String? = null, var categories: List<Category>? = null, var recoverTime: Int? = null, var time: Int? = null) {

    var image: Bitmap? = null

    fun addCategory(category: Category) {
        categories = categories!!.plus(category)
    }

    fun loadBitmap(context: Context) {
        try {
            val f = File(context.filesDir, "exercise_$id.jpg")
            image = BitmapFactory.decodeStream(FileInputStream(f))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    init {
        if(categories == null)
            categories = ArrayList()
    }
}