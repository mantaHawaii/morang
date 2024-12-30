package com.gusto.pikgoogoo.ui.alert

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.gusto.pikgoogoo.R

class NetworkingAlertDialog
constructor(
    context: Context,
    val onClickSetting: () -> Unit
) : Dialog(context), OnClickListener {

    override fun onStart() {
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_alert)

        val bSetting = findViewById<Button>(R.id.b_setting)
        val bOkay = findViewById<Button>(R.id.b_okay)

        bSetting.setOnClickListener(this)
        bOkay.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.b_setting -> {
                    onClickSetting.invoke()
                }
                R.id.b_okay -> {
                    dismiss()
                }
            }
        }
    }

}