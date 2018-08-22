package com.example.gandrade.pokerclub.util

import android.app.Activity
import android.graphics.Bitmap
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix

fun showMessage(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
}

fun hideSoftKeyboard(activity: Activity) {
    var inputMethodManager: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
}

fun isEmpty(textView: TextView) = textView.text.isEmpty()
fun passwordsMatch(textView: TextView, textView1: TextView) = textView.text.toString().equals(textView1.text.toString())

@Throws(WriterException::class)
fun TextToImageEncode(value: String): Bitmap? {
    val bitMatrix: BitMatrix
    try {
        bitMatrix = MultiFormatWriter().encode(value, BarcodeFormat.QR_CODE, 500, 500, null)
    } catch (Illegalargumentexception: IllegalArgumentException) {
        return null
    }
    val bitMatrixWidth = bitMatrix.getWidth()
    val bitMatrixHeight = bitMatrix.getHeight()
    val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
    for (y in 0 until bitMatrixHeight) {
        val offset = y * bitMatrixWidth
        for (x in 0 until bitMatrixWidth) {
            pixels[offset + x] = if (bitMatrix.get(x, y)) 0x000000 else 0xffffff
        }
    }
    val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.RGB_565)
    bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
    return bitmap
}
