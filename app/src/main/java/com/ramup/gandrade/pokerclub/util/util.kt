package com.example.gandrade.pokerclub.util

import android.app.Activity
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

fun showMessage(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
}

fun hideSoftKeyboard(activity: Activity){
    var inputMethodManager:InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken,0)
}

fun isEmpty(textView:TextView)=textView.text.isEmpty()
fun passwordsMatch(textView:TextView,textView1:TextView)=textView.text.toString().equals(textView1.text.toString())