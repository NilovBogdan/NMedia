package ru.netology.nmedia.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad

class AdViewHolder (
    private val binding: CardAdBinding,
):RecyclerView.ViewHolder(binding.root){

    fun bind(ad: Ad){
        Glide.with(binding.image)
            .load("http://10.0.2.2:9999/media/${ad.image}")
            .placeholder(R.drawable.ic_loading_100dp)
            .error(R.drawable.ic_error_100dp)
            .timeout(10_000)
            .into(binding.image)
    }
}