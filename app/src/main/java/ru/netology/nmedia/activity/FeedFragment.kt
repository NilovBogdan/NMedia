package ru.netology.nmedia.activity


import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.OnInteractionListener
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.DetailsFragment.Companion.longArg
import ru.netology.nmedia.activity.NewAndChangePostFragment.Companion.textArg
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)
        val viewModel: PostViewModel by activityViewModels()
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_newAndChangePostFragment,
                    Bundle().apply { textArg = post.content })

            }

            override fun details(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_detailsFragment,
                    Bundle().apply { longArg = post.id })
            }

            override fun fullScreenImage(post: Post) {
                val urlAttachment = "http://10.0.2.2:9999/media/${post.attachment?.url}"
                findNavController().navigate(R.id.action_feedFragment_to_imageFragment,
                    Bundle().apply { textArg = urlAttachment
                        longArg = post.id},
                )
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

//            override fun onShare(post: Post) {
//                val intent = Intent().apply {
//                    action = Intent.ACTION_SEND
//                    putExtra(Intent.EXTRA_TEXT, post.content)
//                    type = "text/plain"
//                }
//                val shareIntent =
//                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
//                startActivity(shareIntent)
//                viewModel.shareById(post.id)
//            }

            override fun onLike(post: Post) {
                val authViewModel by viewModels<AuthViewModel>()
                if (viewModel.checkAuth(authViewModel.isAuthorized)) {
                    viewModel.likeById(post.id)
                }else{
                    AlertDialog.Builder(requireContext())
                        .setTitle(R.string.the_action_cannot_be_performed)
                        .setMessage(R.string.authorization_is_required_to_continue)
                        .setPositiveButton( R.string.logIn) { _: DialogInterface, _: Int ->
                            findNavController().navigate(R.id.action_feedFragment_to_singInFragment)
                        }
                        .setNegativeButton(R.string.close) {_: DialogInterface, _: Int -> }
                        .show()

                }

            }

            override fun playVideo(url: String?) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }

            override fun logicLikeAndRepost(count: Double): String {
                return viewModel.logicLikeAndRepost(count)

            }
        })


        binding.list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
               viewModel.data.collectLatest{
                   adapter.submitData(it)
               }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.retry.setOnClickListener {
                        viewModel.load()
                    }
//                    binding.swipe.setOnRefreshListener {
//                        viewModel.load()
//                        binding.swipe.isRefreshing = false
//                    }

                    binding.swipe.isRefreshing =
                        state.refresh is LoadState.Loading||
                                state.prepend is LoadState.Loading ||
                                state.append is LoadState.Loading
                    adapter.addOnPagesUpdatedListener {
                        binding.list.smoothScrollToPosition(0)
                    }
                }
            }
        }
        binding.swipe.setOnRefreshListener(adapter::refresh)





//        viewModel.data.observe(viewLifecycleOwner) { state ->
//            val newPost =
//                state.posts.size > adapter.currentList.size && adapter.currentList.isNotEmpty()
//            adapter.submitList(state.posts) {
//                if (newPost) {
//
//                }
//            }
////            binding.errorGroup.isVisible = state.error
//            binding.empty.isVisible = state.empty
//
//
//        }
//        viewModel.dataState.observe(viewLifecycleOwner) { state ->
//            binding.swipe.isRefreshing = state.loading
//            if (state.error != FeedError.NONE) {
//                Snackbar.make(binding.root, "Error: ${state.error}", Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.retry) {
//                        viewModel.load()
//                    }
//                    .show()
//            }
//        }
//        viewModel.newerCount.observe(viewLifecycleOwner) {
//            if (it > 0)
//                binding.newEntries.visibility = View.VISIBLE
//            binding.newEntries.setOnClickListener {
//                viewModel.readAll()
//                binding.newEntries.visibility = View.INVISIBLE
//                binding.newEntries.visibility = View.GONE
//            }
//        }

        viewModel.singleError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), R.string.like_try_again, Toast.LENGTH_SHORT).show()
        }
        viewModel.edited.observe(viewLifecycleOwner) { post ->
            if (post.id == 0L) {
                return@observe
            }
        }
        binding.fab.setOnClickListener {
            val authViewModel by viewModels<AuthViewModel>()
            if (viewModel.checkAuth(authViewModel.isAuthorized)) {
                findNavController().navigate(R.id.action_feedFragment_to_newAndChangePostFragment)
            }else{
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.the_action_cannot_be_performed)
                    .setMessage(R.string.authorization_is_required_to_continue)
                    .setPositiveButton( R.string.logIn) { _: DialogInterface, _: Int ->
                        findNavController().navigate(R.id.action_feedFragment_to_singInFragment)
                    }
                    .setNegativeButton(R.string.close) {_: DialogInterface, _: Int -> }
                    .show()

            }



        }

        return binding.root

    }

}