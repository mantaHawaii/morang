package com.gusto.pikgoogoo.ui.gp

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.gusto.pikgoogoo.R
import com.gusto.pikgoogoo.util.HashUtil

class GPDialog
constructor(
    context: Context,
    val onClick: (String) -> Unit
) : Dialog(context) {

    override fun onStart() {
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_gp)

        val button = findViewById<Button>(R.id.b_start)
        button.setOnClickListener {
            val id = findViewById<EditText>(R.id.et_id).text.toString()
            val pw = findViewById<EditText>(R.id.et_pw).text.toString()
            val hu = HashUtil()
            val uid = hu.sha256Base64(id+pw)
            Log.d("MR_GP", uid)
            onClick.invoke(uid)
            dismiss()
        }

    }

}