package ru.netology.nmedia.activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.util.focusAndShowKeyboard
import ru.netology.nmedia.viewmodel.PostViewModel

class NewAndChangePostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefs = context?.getSharedPreferences("draft", Context.MODE_PRIVATE)
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        binding.edit.setText(prefs?.getString("draft",""))
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
            with(prefs?.edit()){
                this?.putString("draft", "")
                this?.apply()
            }

        }
        viewModel.postCreated.observe(viewLifecycleOwner){
            findNavController().navigateUp()
            viewModel.load()
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (arguments?.textArg == null){
                with(prefs?.edit()){
                    this?.putString("draft", binding.edit.text.toString())
                    this?.apply()
                }
            }else viewModel.cancelEdit()

            findNavController().navigateUp()
        }

        return binding.root
    }
    companion object{
        var Bundle.textArg by StringArg
    }
}