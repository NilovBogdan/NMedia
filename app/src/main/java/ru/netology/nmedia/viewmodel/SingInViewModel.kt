package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException


class SingInViewModel:ViewModel() {


    private val _data = MutableLiveData<Token?>(null)
    val data: LiveData<Token?>
        get() = _data
    private val _singleError = SingleLiveEvent<Unit>()
    val singleError: LiveData<Unit>
        get() = _singleError

fun authentication(login: String, password: String){
    viewModelScope.launch {
        try {
            val response = PostsApi.service.authentication(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            _data.value = response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: ApiError) {
            _singleError.value = Unit

        } catch (e: IOException) {
            throw NetworkError
        } catch (_: Exception) {
            throw UnknownError
        }

    }
}


}