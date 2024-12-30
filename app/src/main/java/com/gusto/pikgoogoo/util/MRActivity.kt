package com.gusto.pikgoogoo.util

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.gusto.pikgoogoo.ui.alert.NetworkingAlertDialog

open class MRActivity : AppCompatActivity() {

    var onCreateBoolean = false
    private lateinit var connectionData: ConnectionLiveData
    lateinit var networkStateAlertDialog: NetworkingAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkStateAlertDialog = NetworkingAlertDialog(this, {
            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(intent)
        })

        connectionData = ConnectionLiveData(this)
        connectionData.observe(this, Observer { isConnected ->
            when (isConnected) {
                true -> {
                    onNetWorkConnected()
                }
                false -> {
                    onNetworkDisconnected()
                }
            }
        })
    }

    open fun onNetWorkConnected() {
        if (networkStateAlertDialog.isShowing) {
            networkStateAlertDialog.dismiss()
        }
        if (onCreateBoolean) {
            Toast.makeText(this, "네트워크에 연결되었습니다", Toast.LENGTH_LONG).show()
        }
    }

    open fun onNetworkDisconnected() {
        networkStateAlertDialog.show()
    }

}