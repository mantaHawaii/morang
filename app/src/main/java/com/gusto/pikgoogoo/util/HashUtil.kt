package com.gusto.pikgoogoo.util

import android.util.Base64
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class HashUtil {

    fun sha256Base64(input: String): String {
        try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray())
            return Base64.encodeToString(hash, Base64.NO_WRAP)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

}