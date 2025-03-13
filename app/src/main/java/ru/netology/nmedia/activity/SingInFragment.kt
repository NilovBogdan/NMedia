package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSingInBinding
import ru.netology.nmedia.viewmodel.SingInViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SingInFragment: Fragment() {
    @Inject
    lateinit var appAuth: AppAuth
    private val viewModel: SingInViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentSingInBinding.inflate(inflater, container, false)

        viewModel.data.observe(viewLifecycleOwner, Observer { state ->
            appAuth.setAuth(state ?: return@Observer)
            val previousFragment = findNavController().previousBackStackEntry?.destination?.id
            previousFragment?.let {
                when (previousFragment) {
                    R.id.singUpFragment -> findNavController().navigate(R.id.action_singInFragment_to_feedFragment)
                    else -> findNavController().navigateUp()
                }
            }
        })
        viewModel.singleError.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), R.string.invalid_username_or_password, Toast.LENGTH_SHORT,).show()
        }

        binding.enter.setOnClickListener {
            val login = binding.login.text.toString()
            val password = binding.password.text.toString()
            viewModel.authentication(login, password)
        }
        binding.orRegister.setOnClickListener {
            findNavController().navigate(R.id.action_singInFragment_to_singUpFragment)
        }
        return binding.root
    }
}



