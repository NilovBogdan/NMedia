package ru.netology.nmedia.activity

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewAndChangePostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {
    @Inject
    lateinit var appAuth: AppAuth
    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging
    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability
    val viewModel:AuthViewModel by viewModels()

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



       firebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }

        checkGoogleApiAvailability()


        requestNotificationsPermission()





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
                                        appAuth.clear()
                                        findNavController(R.id.navHost).popBackStack()
                                    }
                                    .setNegativeButton(R.string.no) { _: DialogInterface, _: Int -> }
                                    .show()
                            }else{
                                appAuth.clear()
                            }
                            true
                        }
                        else -> false
                }
            }
        )
    }
    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = android.Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }

    private fun checkGoogleApiAvailability() {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }
    }
}
