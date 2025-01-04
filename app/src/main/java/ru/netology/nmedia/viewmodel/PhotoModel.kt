package ru.netology.nmedia.viewmodel

import android.net.Uri
import java.io.File

data class PhotoModel (
    val uri: Uri,
    val file: File
)