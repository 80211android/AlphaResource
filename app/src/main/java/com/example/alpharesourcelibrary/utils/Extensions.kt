package com.movieplayer.android.utils
import android.content.Context
import android.text.InputFilter
import android.widget.EditText
import android.widget.Toast
/**
 * Created  on 18/07/2019.
 */

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}


fun EditText.setMaxLength(maxLength: Int) {
    filters = arrayOf(InputFilter.LengthFilter(maxLength))
}