package com.pertamina.spbucare.ui


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pertamina.spbucare.R
import com.pertamina.spbucare.databinding.FragmentCreateUserBinding
import com.pertamina.spbucare.model.User
import com.pertamina.spbucare.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CreateUserFragment : Fragment(), TextWatcher {
    private lateinit var binding: FragmentCreateUserBinding
    private val userViewModel: UserViewModel by viewModel { parametersOf("ser") }
    private var userType = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateUserBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = this@CreateUserFragment
            viewModel = userViewModel
            typeDropdown.addTextChangedListener(this@CreateUserFragment)
            etName.addTextChangedListener(this@CreateUserFragment)
            etNumber.addTextChangedListener(this@CreateUserFragment)
            etPosition.addTextChangedListener(this@CreateUserFragment)
            dropdownSer.addTextChangedListener(this@CreateUserFragment)
            etPhone.addTextChangedListener(this@CreateUserFragment)
            etAddress.addTextChangedListener(this@CreateUserFragment)
            etEmail.addTextChangedListener(this@CreateUserFragment)
            etPassword.addTextChangedListener(this@CreateUserFragment)
            etPasswordConfirm.addTextChangedListener(this@CreateUserFragment)
            val listType = resources.getStringArray(R.array.user_type).toList()
            typeDropdown.setAdapter(configureAdapter(listType))
            var listSER = listOf<User>()
            userViewModel.users.observe(viewLifecycleOwner, Observer { list ->
                listSER = list
                dropdownSer.setAdapter(configureAdapter(listSER.map { it.name }))

            })
            typeDropdown.setOnItemClickListener { _, _, position, _ ->
                userViewModel.setUserTypeInput(listType[position])
                userType = listType[position]
            }

            btnSubmit.setOnClickListener { view ->
                val type = typeDropdown.text.toString()
                val name = etName.text.toString().toLowerCase()
                val number = etNumber.text.toString()
                val position = etPosition.text.toString()
                val phone = etPhone.text.toString()
                val address = etAddress.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                var salesRetailId = ""

                listSER.forEach {
                    if (dropdownSer.text.toString().equals(it.name, true)) {
                        salesRetailId = it.userId
                    }
                }

                val user = User(
                    type = type,
                    position = position,
                    phone = phone,
                    adress = address
                )
                if (type == "spbu") {
                    user.name = number
                    user.picName = name
                    user.salesRetailId = salesRetailId
                } else {
                    user.picName = name
                    user.name = name
                    user.position = position
                    user.employeeNumber = number
                    user.pertamina = true
                }
                MaterialAlertDialogBuilder(context)
                    .setTitle("Apakah anda yakin?")
                    .setMessage("Apakah anda yakin ingin MENAMBAHKAN pengguna baru ini?")
                    .setNegativeButton("Tidak", null)
                    .setPositiveButton("Iya") { _, _ ->
                        userViewModel.createUser(email, password, user)
                        Snackbar.make(
                            view,
                            "${name.trim()} telah berhasil ditambahkan menajadi pengguna baru",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        val action = CreateUserFragmentDirections.actionManageUser()
                        findNavController().navigate(action)

                    }
                    .show()
            }
        }
        return binding.root
    }

    private fun configureAdapter(stringArray: List<String>) =
        ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            stringArray
        )
    private var notSamePassword = true
    override fun afterTextChanged(s: Editable?) {
        with(binding) {
            if (etPassword.text.toString().trim().length < 6) {
                if (etPassword.isFocused) {
                    tilPassword.error = "Kata sandi minimal terdiri dari 6 karakter"
                    tilPasswordConfirm.isEnabled = false
                }
            } else {
                tilPasswordConfirm.isEnabled = true
                if (etPasswordConfirm.isFocused) {
                    if (etPasswordConfirm.text.toString() != etPassword.text.toString()) {
                        tilPasswordConfirm.error = "Kata sandi tidak sama"
                        notSamePassword = true
                    } else {
                        tilPasswordConfirm.error = null
                        notSamePassword = false
                    }
                }
                tilPassword.error = null
            }

            btnSubmit.isEnabled = if (userType == "spbu")
                !(typeDropdown.text.toString().isEmpty() || etName.text.toString().isEmpty()
                        || etNumber.text.toString().isEmpty() || dropdownSer.text.isEmpty()
                        || etPhone.text.toString().isEmpty() || etAddress.text.toString().isEmpty()
                        || etEmail.text.toString().isEmpty() || etPasswordConfirm.text.toString().isEmpty()
                        || notSamePassword)
            else
                !(typeDropdown.text.toString().isEmpty() || etName.text.toString().isEmpty()
                        || etNumber.text.toString().isEmpty() || etPosition.text.toString().isEmpty()
                        || etPhone.text.toString().isEmpty() || etAddress.text.toString().isEmpty()
                        || etEmail.text.toString().isEmpty() || etPasswordConfirm.text.toString().isEmpty()
                        || notSamePassword)
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