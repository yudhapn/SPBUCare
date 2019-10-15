package com.pertamina.spbucare.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.pertamina.spbucare.R
import com.pertamina.spbucare.databinding.ActivityLoginBinding
import com.pertamina.spbucare.viewmodel.UserViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class LoginActivity : AppCompatActivity(), TextWatcher {
    private val auth by inject<FirebaseAuth>()
    private val userVM: UserViewModel by viewModel{ parametersOf("") }
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        with(binding) {
            val user = getSharedPreferences("user", MODE_PRIVATE)
            emailInput.addTextChangedListener(this@LoginActivity)
            passwordInput.addTextChangedListener(this@LoginActivity)
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (auth.currentUser != null) {
                startActivity(intent)
            } else {
                loginButton.setOnClickListener { view ->
                    loginButton.isEnabled = false
                    progressbar.visibility = View.VISIBLE
                    val email = emailInput.text.toString()
                    val password = passwordInput.text.toString()

                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Snackbar.make(view, "Kata sandi / email tidak valid", Snackbar.LENGTH_SHORT).show()
                                    progressbar.visibility = View.GONE
                                    loginButton.isEnabled = true
                                } else {
                                    userVM.user.observe(this@LoginActivity, Observer {
                                        val edit = user.edit()
                                        edit.putString("type", it.type)
                                        edit.putString("salesRetailId", it.salesRetailId)
                                        edit.putString("userName", it.name)
                                        edit.putString("position", it.position)
                                        edit.putBoolean("isAdmin", it.operator)
                                        edit.apply()
                                        startActivity(intent)
                                        progressbar.visibility = View.GONE
                                        loginButton.isEnabled = true
                                    })
                                }
                            }
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        with(binding) {
            loginButton.isEnabled = !(emailInput.text.isEmpty() || passwordInput.text.isEmpty())
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

}
