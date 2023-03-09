package com.example.awscognitosignup

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.HEAD
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface IFileUpload {
    // 上传图片
    @Multipart
    @POST("base/file/upload")
    suspend fun uploadFile(
        @Part body: MultipartBody.Part,
        @Header("client") clientId: String,
        @Header("userId") userId: String
    ): NetworkResponse<String, Error>
}

class FileUploaderService {

    suspend fun uploadFile(clientId:String, userId:String, fileUrl:String, thenProcess:(String?) -> Unit) {
        val baseUrl:String = "http://54.92.87.233:30002/base/file/upload"

        if (clientId.isNullOrEmpty() || userId.isNullOrEmpty() || fileUrl.isNullOrEmpty()) {
            return
        }

        val service = ApiClientManager.client.create(IFileUpload::class.java)

        val file = File(fileUrl)
        val fileRequestBody = MultipartBody.Part.createFormData(
            "file", file.name,
            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

        when (val response = service.uploadFile(fileRequestBody, clientId,userId)) {
            is NetworkResponse.Success -> {
                Log.i("FileUploader", "success:$response.body")
                thenProcess(response.body)
            }
            is NetworkResponse.ServerError -> {
                Log.i("FileUploader","Upload Server Error")
            }
            is NetworkResponse.NetworkError -> {
                Log.i("FileUploader","Upload Network Error: ${response.error}")
            }
            is UnknownError -> {
                Log.i("FileUploader","Upload Unknown Error")
            }
            else -> {
                Log.i("FileUploader","Upload result:$response")
            }
        }
    }
}