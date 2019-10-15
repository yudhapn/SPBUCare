package com.pertamina.spbucare.ui


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.pertamina.spbucare.databinding.FragmentLoginBinding
import com.pertamina.spbucare.viewmodel.UserViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class LoginFragment : Fragment(), TextWatcher {
    private val auth by inject<FirebaseAuth>()
    private val userVM: UserViewModel by sharedViewModel{ parametersOf("") }
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false).apply {
            //            val stateVM = ViewModelProviders.of(requireActivity(), SavedStateViewModelFactory(requireActivity()))
//                .get(UserStateViewModel::class.java)
            emailInput.addTextChangedListener(this@LoginFragment)
            passwordInput.addTextChangedListener(this@LoginFragment)
            loginButton.setOnClickListener { view ->
                progressbar.visibility = View.VISIBLE
                val email = emailInput.text.toString()
                val password = passwordInput.text.toString()

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (!it.isSuccessful) {
                                Snackbar.make(view, "Kata sandi / email tidak valid", Snackbar.LENGTH_SHORT).show()
                            }
                            progressbar.visibility = View.GONE
                        }
            }
        }
        return binding.root
    }

    override fun afterTextChanged(s: Editable?) {
        with(binding) {
            loginButton.isEnabled = !(emailInput.text.isEmpty() || passwordInput.text.isEmpty())
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

}