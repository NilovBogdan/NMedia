package ru.netology.nmedia.activity


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSingUpBinding
import ru.netology.nmedia.viewmodel.PhotoModel
import ru.netology.nmedia.viewmodel.SingUpViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SingUpFragment @Inject constructor(
    private val appAuth: AppAuth
) : Fragment() {
    private val viewModel: SingUpViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentSingUpBinding.inflate(inflater, container, false)

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
                binding.addPhoto.setImageURI(uri)
            }

        binding.addPhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .maxResultSize(300, 300)
                .cameraOnly()
                .createIntent(photoResultContract :: launch)
        }

        binding.register.setOnClickListener {
            val name = binding.name.text.toString()
            val login = binding.login.text.toString()
            val password = binding.password.text.toString()
            val passwordCheck = binding.passwordReplay.text.toString()
            if (password == passwordCheck){
                viewModel.registration(login, password, name)
            }else{
                Toast.makeText(requireContext(), R.string.passwords_dont_match, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner){ state ->
            appAuth.setAuth(state ?: return@observe)
            findNavController().navigate(R.id.action_singUpFragment_to_feedFragment)
        }


        binding.orEnter.setOnClickListener {
            findNavController().navigate(R.id.action_singUpFragment_to_singInFragment2)
        }

        return binding.root
    }

    companion object {
        private const val IMAGE_MAX_SIZE = 2048
    }
}
