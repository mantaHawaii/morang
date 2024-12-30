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

class CustomAlertDialog
constructor(
    context: Context,
    val onClick: (Any?) -> Unit,
    val contentMap: MutableMap<String, String>
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
        val tvContent = findViewById<TextView>(R.id.tv_notification_content)

        bSetting.setOnClickListener(this)
        bOkay.setOnClickListener(this)

        if (contentMap.get("LEFT") != null) {
            bSetting.text = contentMap.get("LEFT")
        }

        if (contentMap.get("RIGHT") != null) {
            bOkay.text = contentMap.get("RIGHT")
        }

        if (contentMap.get("CONTENT") != null) {
            tvContent.text = contentMap.get("CONTENT")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.b_setting -> {
                onClick.invoke(null)
            }
            R.id.b_okay -> {
                dismiss()
            }
        }
    }

}