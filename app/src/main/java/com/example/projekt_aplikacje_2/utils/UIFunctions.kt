package com.example.projekt_aplikacje_2.utils

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import java.lang.Exception

object UIFunctions {
    fun showAlert(context: Context, msg: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage(msg)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val alert: AlertDialog = builder.create()
        try {
            alert.show()
        } catch (e: Exception) {
            Log.i("UI", "Coś złego sie stalo z watkami")
        }
    }
}