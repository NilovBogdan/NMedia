package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class NewAndChangePostResultContract: ActivityResultContract<String, String?>() {
    override fun createIntent(context: Context, input: String): Intent =
        Intent(context, NewAndChangePostActivity::class.java).putExtra("editText", input)



    override fun parseResult(resultCode: Int, intent: Intent?): String? =
        if(resultCode == Activity.RESULT_OK){
            intent?.getStringExtra(Intent.EXTRA_TEXT)
        }else{
            null
        }

}