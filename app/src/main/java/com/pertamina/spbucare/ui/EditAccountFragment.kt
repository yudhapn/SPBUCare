package com.pertamina.spbucare.ui


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pertamina.spbucare.databinding.FragmentEditAccountBinding
import com.pertamina.spbucare.model.User
import com.pertamina.spbucare.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditAccountFragment : Fragment(), TextWatcher {
    private lateinit var binding: FragmentEditAccountBinding
    private val userViewModel: UserViewModel by viewModel{ parametersOf("")}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditAccountBinding.inflate(inflater, container, false).apply {
            var user = User()
            arguments?.let {
                user = EditAccountFragmentArgs.fromBundle(it).userArg
            }
            Log.d("testing", "${user.userId}, ${user.name}")
            etEmail.addTextChangedListener(this@EditAccountFragment)
            etPassword.addTextChangedListener(this@EditAccountFragment)
            btnSubmit.setOnClickListener { view ->
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                MaterialAlertDialogBuilder(context)
                    .setMessage("Apakah anda yakin ingin MENGUBAH akun pengguna ini?")
                    .setNegativeButton("Tidak", null)
                    .setPositiveButton("Iya") { _, _ ->
                        userViewModel.updateAccount(user.userId, email, password)
                        Snackbar.make(
                            view,
                            "Akun ${user.name.trim()} telah berhasil diubah",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                        var action = EditAccountFragmentDirections.actionShowPertaminaDetail(user)
                        if (user.type == "spbu") {
                            action = EditAccountFragmentDirections.actionShowSpbuDetail(user)
                        }
                        findNavController().navigate(action)
                    }
                    .show()
            }
        }
        return binding.root
    }

    override fun afterTextChanged(s: Editable?) {
        with(binding) {
            btnSubmit.isEnabled = !(etEmail.text.toString().isEmpty()
                    || etPassword.text.toString().isEmpty() || etPassword.text.toString().trim().length < 6)
            if (etPassword.text.toString().trim().length < 6) {
                tilPassword.error = "Kata sandi minimal terdiri dari 6 karakter"
            } else {
                tilPassword.error = null
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}