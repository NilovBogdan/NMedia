package ru.netology.nmedia.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewAndChangePostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.viewmodel.AuthViewModel


class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }
            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Snackbar.make(
                    binding.root,
                    R.string.error_empty_content,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(android.R.string.ok) {
                        finish()
                    }
                    .show()
            }
            findNavController(R.id.navHost).navigate(R.id.action_feedFragment_to_newAndChangePostFragment,
                Bundle().apply { textArg = text })
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 0)
        }

        val viewModel by viewModels<AuthViewModel>()

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.auth_menu, menu)

                    viewModel.data.observe(this@AppActivity) {
                        menu.setGroupVisible(R.id.authorized, viewModel.isAuthorized)
                        menu.setGroupVisible(R.id.unauthorized, !viewModel.isAuthorized)

                    }
                }
                val navController = findNavController(R.id.navHost)
                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.singIn -> {
                            findNavController(R.id.navHost).navigate(R.id.action_feedFragment_to_singInFragment)
                            true
                        }
                        R.id.singUp ->{
                            findNavController(R.id.navHost).navigate(R.id.action_feedFragment_to_singUpFragment)
                            true
                        }
                        R.id.logout -> {
                            if (navController.currentDestination?.id == R.id.newAndChangePostFragment){
                                AlertDialog.Builder(this@AppActivity)
                                    .setTitle(R.string.logout)
                                    .setMessage(R.string.are_you_sure)
                                    .setPositiveButton( R.string.yes) { _: DialogInterface, _: Int ->
                                        AppAuth.getInstance().clear()
                                        findNavController(R.id.navHost).popBackStack()
                                    }
                                    .setNegativeButton(R.string.no) { _: DialogInterface, _: Int -> }
                                    .show()
                            }else{
                                AppAuth.getInstance().clear()
                            }
                            true
                        }
                        else -> false
                }
            }
        )
    }
}