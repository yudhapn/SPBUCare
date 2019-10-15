package com.pertamina.spbucare.ui


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pertamina.spbucare.databinding.FragmentChangePasswordBinding
import com.pertamina.spbucare.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private val userViewModel: UserViewModel by viewModel { parametersOf("") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        binding.apply {
            passwordInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (passwordInput.text.toString().trim().length < 6) {
                        passwordInputlayout.error = "Kata sandi minimal terdiri dari 6 karakter"
                    } else {
                        passwordInputlayout.error = null
                    }
                }
            })
            passwordConfirmInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (passwordConfirmInput.text.toString().trim() != passwordInput.text.toString().trim()) {
                        passwordConfirmInputlayout.error = "Kata sandi tidak sama"
                        submitButton.isEnabled = false
                    } else {
                        passwordConfirmInputlayout.error = null
                        submitButton.isEnabled = true
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            submitButton.setOnClickListener { view ->
                MaterialAlertDialogBuilder(context)
                    .setMessage("Apakah anda yakin ingin MENGGANTI password dengan yang baru?")
                    .setNegativeButton("Tidak", null)
                    .setPositiveButton("Iya") { _, _ ->
                        submitButton.isEnabled = false
                        progressbar.visibility = View.VISIBLE
                        userViewModel.changePassword(passwordConfirmInput.text.toString())
                        userViewModel.accountChanged.observe(
                            this@ChangePasswordFragment,
                            Observer {
                                if (it) {
                                    Snackbar.make(
                                        view,
                                        "Kata sandi telah berhasil diubah",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                    submitButton.isEnabled = true
                                    progressbar.visibility = View.GONE
                                }
                            })
                    }
                    .show()
            }
        }
        return binding.root
    }
}