package com.example.rudonews.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.system.Os.close
import com.example.rudonews.R
import com.example.rudonews.modules.login.LoginActivity

object Utils {

    fun createDialog(context: Context, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.apply {
            setPositiveButton(
                R.string.ok,
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                }
            )
        }
        builder.create().show()
    }

    fun createGuestDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(context.getString(R.string.guest_dialog))
        builder.apply {
            setPositiveButton(
                R.string.go_to_beginning,
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                    context.startActivity(Intent(context, LoginActivity::class.java))
                }
            )
            setNegativeButton(
                R.string.close,
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                }
            )
        }
        val dialog = builder.create()
        dialog.apply {
            setCanceledOnTouchOutside(false)
            show()
        }
    }
}
