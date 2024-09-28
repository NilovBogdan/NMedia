package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl
private val empty = Post(
    id = 0L,
    "",
    "",
    "",
    "",
    "",
    "",
    false,
    0,
    0
)
class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
    val edited = MutableLiveData(empty)

    fun applyChangesAndSave(newText: String){
        edited.value?.let {
            val text = newText.trim()
            if (text != it.content){
                repository.save(it.copy(content = text))
            }
            edited.value = empty
        }
    }
    fun edit(post: Post){
        if (post.id == 0L){
            edited.value = empty
        }
        edited.value = post
    }
}