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
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

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

fun pushToChat(message: String) {

    val serverURL: String = "https://fcm.googleapis.com/fcm/send"
    val url = URL(serverURL)
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.connectTimeout = 300000
    connection.connectTimeout = 300000
    connection.doOutput = true

    val postData: ByteArray = message.toByteArray(StandardCharsets.UTF_8)

    connection.setRequestProperty("charset", "utf-8")
    connection.setRequestProperty("Content-lenght", postData.size.toString())
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Authorization", "AAAAZ813xEc:APA91bHTm8_zfC-N7ywnx4TcT4rW1Uh9jjFJlTgRj2_mBpD-iKxAZw1Di87Tr11xPrTuu-aRFBznjFVW5GQt1FaSRqIxP8SaL0Rnt6wq3YgEGwazI8eVWivtWlm2Ki_jdnE7R3q9mchtwSg_RgsvpItsbHyCu9GZ8g")

    try {
        val outputStream: DataOutputStream = DataOutputStream(connection.outputStream)
        outputStream.write(postData)
        outputStream.flush()
    } catch (exception: Exception) {

    }
    /**
    if (connection.responseCode != HttpURLConnection.HTTP_OK && connection.responseCode != HttpURLConnection.HTTP_CREATED) {
        try {


            val reader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
            val output: String = reader.readLine()

            println("There was error while connecting the chat $output")
            System.exit(0)

        } catch (exception: Exception) {
            throw Exception("Exception while push the notification  $exception.message")
        }
    }
    */

}

