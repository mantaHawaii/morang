package com.gusto.pikgoogoo.data.repository

import android.content.Context
import java.io.File
import javax.inject.Inject

class CacheRepository
@Inject
constructor() {

    fun deleteCashe(context: Context) {
        val dir = context.cacheDir
        deleteDir(dir)
    }

    fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()?:throw Exception("에러")
            for (child in children) {
                val success = deleteDir(File(dir, child))
                if (!success) {
                    return false
                }
            }
            return dir.delete()
        } else if (dir!= null && dir.isFile()) {
            return dir.delete()
        } else {
            return false
        }
    }

}