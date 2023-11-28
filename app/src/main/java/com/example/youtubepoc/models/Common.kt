package com.example.youtubepoc.models

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar


object Common {

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    fun showSnackMessage(view: View?, message: String?, isFragment: Boolean) {
        try {
            val snackBar = Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.BLACK)
                .setActionTextColor(Color.WHITE)
            val snackBarView = snackBar.view
            val layoutParams = snackBarView.layoutParams as MarginLayoutParams
            if (isFragment) {
                layoutParams.bottomMargin = 210
            } else {
                layoutParams.bottomMargin = 50
            }
            snackBarView.layoutParams = layoutParams
            snackBar.setTextMaxLines(4)
            snackBar.setAction("Dismiss") { snackBar.dismiss() }
            snackBar.show()
        } catch (e: Exception) {
            Log.e("Agnesh | Common -> SnackBar issue: " , e.message.toString())
        }
    }

    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }
}