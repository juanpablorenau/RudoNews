package com.example.rudonews.utils

import android.app.Activity
import android.content.Context
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun String.isValidUsername() = this.length > 1
fun String.isValidPasswordLength() = this.length > 7
fun String.hasUpperCase() = matches(".*[A-Z].*".toRegex())
fun String.hasLowerCase() = matches(".*[a-z].*".toRegex())
fun String.hasNumber() = matches(".*[1-9].*".toRegex())
fun String.hasSpecialCharacter() = matches(".*[!@#$%&*()._+=|<>?{}~-].*".toRegex())
fun String.isValidEmail() = matches(Patterns.EMAIL_ADDRESS.toRegex())

fun Fragment.hideKeyboard() { view?.let { activity?.hideKeyboard(it) } }

fun Activity.hideKeyboard() { hideKeyboard(currentFocus ?: View(this)) }

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
