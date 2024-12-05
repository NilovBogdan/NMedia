package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.util.focusAndShowKeyboard
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

        binding.ok.setOnClickListener {
            val text = binding.edit.text.toString()
            if (text.isNotBlank()) {
                viewModel.applyChangesAndSave(text)
            }
            with(prefs?.edit()) {
                this?.putString("draft", "")
                this?.apply()
            }

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
    }
}