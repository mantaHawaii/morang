package com.gusto.pikgoogoo.data.repository

import com.gusto.pikgoogoo.data.Response

abstract class ParentRepository {

    fun isStatusCodeSuccess(response: Response): Boolean {
        return response.status.code == "111"
    }

    fun formatErrorFromStatus(response: Response): Exception {
        return Exception(response.status.code+":"+response.status.message)
    }


}