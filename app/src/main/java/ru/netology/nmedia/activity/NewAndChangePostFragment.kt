package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.util.focusAndShowKeyboard
import ru.netology.nmedia.viewmodel.PhotoModel
import ru.netology.nmedia.viewmodel.PostViewModel

class NewAndChangePostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val prefs = context?.getSharedPreferences("draft", Context.MODE_PRIVATE)
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        binding.edit.setText(prefs?.getString("draft", ""))
        arguments?.textArg?.let {
            binding.edit.apply {
                setText(it)
                requestFocus()
                focusAndShowKeyboard()
            }

        }
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.new_post_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    if (menuItem.itemId == R.id.save) {
                        val text = binding.edit.text.toString()
                        if (text.isNotBlank()) {
                            viewModel.applyChangesAndSave(text)
                        }
                        with(prefs?.edit()) {
                            this?.putString("draft", "")
                            this?.apply()
                        }
                        true
                    } else {
                        false
                    }
            },
            viewLifecycleOwner,
        )
        viewModel.photo.observe(viewLifecycleOwner){
            if (it == null){
                binding.previewContainer.isGone = true
                return@observe
            }
            binding.previewContainer.isVisible = true
            binding.preview.setImageURI(it.uri)
        }
        val photoResultContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(
                        requireContext(),
                        R.string.image_picker_error,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@registerForActivityResult
                }
                val uri = it.data?.data ?: return@registerForActivityResult
                viewModel.savePhoto(PhotoModel(uri, uri.toFile()))
            }
        binding.pickPhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .maxResultSize(IMAGE_MAX_SIZE, IMAGE_MAX_SIZE)
                .galleryOnly()
                .createIntent(photoResultContract :: launch)
        }
        binding.clear.setOnClickListener {
            viewModel.savePhoto(null)
        }
        binding.takePhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .maxResultSize(IMAGE_MAX_SIZE, IMAGE_MAX_SIZE)
                .cameraOnly()
                .createIntent(photoResultContract :: launch)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
            viewModel.load()
        }
        viewModel.singleError.observe(viewLifecycleOwner) {
            val dialogBinding = layoutInflater.inflate(R.layout.error_frame, container, false)
            val dialog = Dialog(requireContext())
            dialog.setContentView(dialogBinding)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val close = dialogBinding.findViewById<Button>(R.id.close)
            val tryAgain = dialogBinding.findViewById<Button>(R.id.tryAgain)
            dialog.show()
            close.setOnClickListener {
                dialog.dismiss()
                findNavController().navigateUp()
                viewModel.load()
            }
            tryAgain.setOnClickListener {
                dialog.dismiss()
                val text = binding.edit.text.toString()
                if (text.isNotBlank()) {
                    viewModel.applyChangesAndSave(text)
                }
                with(prefs?.edit()) {
                    this?.putString("draft", "")
                    this?.apply()
                }

            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (arguments?.textArg == null) {
                with(prefs?.edit()) {
                    this?.putString("draft", binding.edit.text.toString())
                    this?.apply()
                }
            } else viewModel.cancelEdit()

            findNavController().navigateUp()
        }

        return binding.root
    }

    companion object {
        var Bundle.textArg by StringArg
        private const val IMAGE_MAX_SIZE = 2048
    }
}