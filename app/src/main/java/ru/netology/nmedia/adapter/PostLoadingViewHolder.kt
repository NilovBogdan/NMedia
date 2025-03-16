package ru.netology.nmedia.adapter

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.ItemLoadingBinding

class PostLoadingViewHolder(
    private val itemLoadingBinding: ItemLoadingBinding,
    private val retryListener: () -> Unit
) : RecyclerView.ViewHolder(itemLoadingBinding.root) {

    fun bind(loadState: LoadState) {
        itemLoadingBinding.apply {
            itemLoadingBinding.progress.isVisible = loadState is LoadState.Loading
            retryButton.isVisible = loadState is LoadState.Error
            retryButton.setOnClickListener {
                retryListener()
            }
        }
    }


}