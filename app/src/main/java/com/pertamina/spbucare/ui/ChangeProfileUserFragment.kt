package com.pertamina.spbucare.ui


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pertamina.spbucare.R
import com.pertamina.spbucare.databinding.FragmentChangeProfileUserBinding
import com.pertamina.spbucare.databinding.FragmentEditProfileBinding
import com.pertamina.spbucare.model.User
import com.pertamina.spbucare.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ChangeProfileUserFragment : Fragment(), TextWatcher {
    private lateinit var binding: FragmentChangeProfileUserBinding
    private val userViewModel: UserViewModel by viewModel { parametersOf("") }
    private var userType = ""
    var user = User()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeProfileUserBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = this@ChangeProfileUserFragment
            viewModel = userViewModel
            userViewModel.user.observe(this@ChangeProfileUserFragment, Observer {
                user = it
                userViewModel.setUserTypeInput(it.type)
            })
            etName.addTextChangedListener(this@ChangeProfileUserFragment)
            etPhone.addTextChangedListener(this@ChangeProfileUserFragment)
            etAddress.addTextChangedListener(this@ChangeProfileUserFragment)

            btnSubmit.setOnClickListener { view ->
                val name = etName.text.toString().toLowerCase()
                val phone = etPhone.text.toString()
                val address = etAddress.text.toString()
                user.phone = phone
                user.adress = address

                if (user.type == "spbu") {
                    user.picName = name
                } else {
                    user.picName = name
                    user.name = name
                }
                MaterialAlertDialogBuilder(context)
                    .setMessage("Apakah anda yakin ingin MENGUBAH profil?")
                    .setNegativeButton("Tidak", null)
                    .setPositiveButton("Iya") { _, _ ->
                        btnSubmit.isEnabled = false
                        progressbar.visibility = View.VISIBLE
                        userViewModel.updateUser(user)
                        userViewModel.accountChanged.observe(this@ChangeProfileUserFragment, Observer {
                                if (it) {
                                    Snackbar.make(
                                        view,
                                        "Profil telah berhasil diubah",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                    btnSubmit.isEnabled = true
                                    progressbar.visibility = View.GONE
                                }
                            })
                    }
                    .show()
            }
        }
        return binding.root
    }


    override fun afterTextChanged(s: Editable?) {
        with(binding) {
            btnSubmit.isEnabled = !(etName.text.toString().isEmpty()
                        || etPhone.text.toString().isEmpty() || etAddress.text.toString().isEmpty())
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}