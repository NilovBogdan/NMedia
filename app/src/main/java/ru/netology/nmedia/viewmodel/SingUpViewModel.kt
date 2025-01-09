package ru.netology.nmedia.viewmodel

import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException

class SingUpViewModel: ViewModel() {

    private val _data = MutableLiveData<Token?>(null)
    val data: LiveData<Token?>
        get() = _data
    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?> = _photo
    fun registration(login: String, password: String, name: String){
        viewModelScope.launch {
            try {
                _photo.value?.let {
                    val uri = it.uri
                    registrationWithAvatar(login, password, name, PhotoModel(uri, uri.toFile()))
                }?: registrationWithoutAvatar(login, password, name)

            } catch (e: Exception) {
                e.message
            }
        }
    }

    private fun registrationWithoutAvatar(login: String, password: String, name: String){
        viewModelScope.launch {
            try {
                val response = PostsApi.service.registrationWithoutAvatars(login, password, name)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                _data.value = response.body() ?: throw ApiError(response.code(), response.message())
            } catch (e: ApiError) {
//
            } catch (e: IOException) {
                throw NetworkError
            } catch (_: Exception) {
                throw UnknownError
            }
        }

    }

    private fun registrationWithAvatar(login: String, password: String, name: String, file: PhotoModel){
        viewModelScope.launch {
        val media = MultipartBody.Part.createFormData(
            "file",
            filename = "image.png",
            file.file.asRequestBody())

        val response = PostsApi.service.registrationWithAvatar(
            login.toRequestBody("text/plain".toMediaType()),
            password.toRequestBody("text/plain".toMediaType()),
            name.toRequestBody("text/plain".toMediaType()),
            media)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        _data.value = response.body() ?: throw ApiError(response.code(), response.message())
            }
    }

    fun savePhoto(photoModel: PhotoModel) {
        _photo.value = photoModel

    }

}