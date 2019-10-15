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
import com.pertamina.spbucare.databinding.FragmentEditProfileBinding
import com.pertamina.spbucare.model.User
import com.pertamina.spbucare.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditProfileFragment : Fragment(), TextWatcher {
    private lateinit var binding: FragmentEditProfileBinding
    private val userViewModel: UserViewModel by viewModel { parametersOf("ser") }
    private var userType = ""
    var user = User()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        binding.apply {
            arguments?.let {
                user = EditProfileFragmentArgs.fromBundle(it).userArg
            }
            lifecycleOwner = this@EditProfileFragment
            viewModel = userViewModel
            userArg = user

            etNumber.setText(user.name)
            etName.setText(user.picName)
            if (user.type != "spbu") {
                etNumber.setText(user.employeeNumber)
                etName.setText(user.name)
            }
            etName.addTextChangedListener(this@EditProfileFragment)
            etNumber.addTextChangedListener(this@EditProfileFragment)
            etPosition.addTextChangedListener(this@EditProfileFragment)
            dropdownSer.addTextChangedListener(this@EditProfileFragment)
            etPhone.addTextChangedListener(this@EditProfileFragment)
            etAddress.addTextChangedListener(this@EditProfileFragment)
            var listSER = listOf<User>()
            userViewModel.setUserTypeInput(user.type)
            userType = user.type
            userViewModel.users.observe(viewLifecycleOwner, Observer { list ->
                listSER = list
                list.forEach {
                    if (user.salesRetailId == it.userId) {
                        serName = it.name
                    }
                }
                dropdownSer.setAdapter(configureAdapter(listSER.map { it.name }))
            })

            btnSubmit.setOnClickListener { view ->
                val name = etName.text.toString().toLowerCase()
                val number = etNumber.text.toString()
                val position = etPosition.text.toString()
                val phone = etPhone.text.toString()
                val address = etAddress.text.toString()
                var salesRetailId = ""

                listSER.forEach {
                    if (dropdownSer.text.toString().equals(it.name, true)) {
                        salesRetailId = it.userId
                    }
                }

                user.position = position
                user.phone = phone
                user.adress = address

                if (user.type == "spbu") {
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
                    .setMessage("Apakah anda yakin ingin MENGUBAH profil pengguna ini?")
                    .setNegativeButton("Tidak", null)
                    .setPositiveButton("Iya") { _, _ ->
                        userViewModel.updateUser(user)
                        Snackbar.make(
                            view,
                            "Profil ${name.trim()} telah berhasil diubah",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        var action = EditProfileFragmentDirections.actionShowPertaminaDetail(user)
                        if (user.type == "spbu") {
                            action = EditProfileFragmentDirections.actionShowSpbuDetail(user)
                        }
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

    override fun afterTextChanged(s: Editable?) {
        with(binding) {
//            Log.d("testing", "${etName.text.toString().isEmpty()} || ${etNumber.text.toString().isEmpty()} || ${dropdownSer.text.isEmpty()} || ${etPhone.text.toString().isEmpty()} || ${etAddress.text.toString().isEmpty()}")
            btnSubmit.isEnabled = if (userType == "spbu")
                !(etName.text.toString().isEmpty()
                        || etNumber.text.toString().isEmpty() || dropdownSer.text.isEmpty()
                        || etPhone.text.toString().isEmpty() || etAddress.text.toString().isEmpty())
            else
                !(etName.text.toString().isEmpty()
                        || etNumber.text.toString().isEmpty() || etPosition.text.toString().isEmpty()
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