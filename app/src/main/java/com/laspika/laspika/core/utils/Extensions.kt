package com.laspika.laspika.core.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.laspika.laspika.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}
fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createTemporaryFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

private const val FILENAME_FORMAT = "dd-MMM-yyyy"
val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createTemporaryFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

private const val MAXIMAL_SIZE = 1000000
fun reduceFileSize(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun getFileType(uri: Uri, context: Context): String? {
    val contentResolver: ContentResolver = context.contentResolver
    val mimeTypeMap = MimeTypeMap.getSingleton()
    val extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    return when (extension) {
        "jpg", "jpeg", "png", "gif", "bmp" -> "Image"
        "mp4", "mkv", "avi", "webm" -> "Video"
        "mp3", "wav", "ogg", "m4a" -> "Audio"
        else -> null
    }
}

fun getFileName(uri: Uri, context: Context): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                result = it.getString(it.getColumnIndexOrThrow("_display_name"))
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1) {
            result = result?.substring(cut!! + 1)
        }
    }
    return result
}

fun showDatePicker(context: Context, func: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val dateString = dateFormat.format(selectedDate.time)
            func(dateString)
        },
        year,
        month,
        day
    )

    datePickerDialog.show()
}

fun showTimePicker(context: Context, func: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
            val timeString = String.format(Locale("id", "ID"), "%02d:%02d", selectedHour, selectedMinute)
            func(timeString)
        },
        hour,
        minute,
        true
    )

    timePickerDialog.show()
}

fun ImageView.glide(url: String) {
    Glide.with(this).load(url).into(this)
}

@SuppressLint("InflateParams", "MissingInflatedId")
fun Activity.setupLogoutDialog(
    title: String,
    message: String,
    btnActionText: String,
    onYesClick: () -> Unit
) {
    val dialog = Dialog(this, android.R.style.Theme_Dialog)
    val view = layoutInflater.inflate(R.layout.logout_dialog, null)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(view)
    dialog.window?.setGravity(Gravity.CENTER)
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()

    val tvTitle = view.findViewById<TextView>(R.id.tv_dialog_title)
    val tvMessage = view.findViewById<TextView>(R.id.tv_dialog_message)
    tvTitle.text = title
    tvMessage.text = message

    val btnDismiss = view.findViewById<Button>(R.id.btn_dialog_dismiss)
    val btnYes = view.findViewById<Button>(R.id.btn_dialog_yes)
    btnYes.text = btnActionText

    btnDismiss.setOnClickListener {
        dialog.dismiss()
    }
    btnYes.setOnClickListener {
        onYesClick()
        dialog.dismiss()
    }
}

fun getDate() : Map<String, String> {
    val currentDate = Date()
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    val timeFormat = SimpleDateFormat("HH.mm", Locale("id", "ID"))

    val formattedDate = dateFormat.format(currentDate)
    val formattedTime = timeFormat.format(currentDate)

    return mapOf(
        Pair("date", formattedDate),
        Pair("time", formattedTime)
    )
}


@SuppressLint("InflateParams")
fun Fragment.setUpForgotPasswordDialog(
    onSendClick: (String) -> Unit
) {
    val dialog = Dialog(requireContext(), android.R.style.Theme_Dialog)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(view)
    dialog.window?.setGravity(Gravity.CENTER)
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()

    val etEmail = view.findViewById<EditText>(R.id.etEmailReset)
    val btnCancel = view.findViewById<Button>(R.id.btnCancelReset)
    val btnSend = view.findViewById<Button>(R.id.btnReset)

    btnSend.setOnClickListener {
        val email = etEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }
}
