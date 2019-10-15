package com.pertamina.spbucare.ui


import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pertamina.spbucare.util.InputFilterMinMax
import com.pertamina.spbucare.databinding.FragmentEmergencyBinding
import com.pertamina.spbucare.model.Order
import com.pertamina.spbucare.model.OrderVolume
import com.pertamina.spbucare.viewmodel.OrderViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EmergencyFragment : Fragment(), TextWatcher, View.OnClickListener {
    private lateinit var binding: FragmentEmergencyBinding
    private val orderVM: OrderViewModel by viewModel{ parametersOf("") }
    private val type = "emergency MS2 manual"

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmergencyBinding.inflate(inflater, container, false)
        binding.submitButton.setOnClickListener(this)
        setRangeVolume()
        setEditTextListener()
        return binding.root
    }

    private fun setEditTextListener() {
        binding.apply {
            etPremium.addTextChangedListener(this@EmergencyFragment)
            etBio.addTextChangedListener(this@EmergencyFragment)
            etPertamax.addTextChangedListener(this@EmergencyFragment)
            etPertalite.addTextChangedListener(this@EmergencyFragment)
            etDexlite.addTextChangedListener(this@EmergencyFragment)
            etPertadex.addTextChangedListener(this@EmergencyFragment)
            etTurbo.addTextChangedListener(this@EmergencyFragment)
        }
    }

    private fun setRangeVolume() {
        val range = arrayOf<InputFilter>(InputFilterMinMax("1", "100"))
        binding.apply {
            etPremium.filters = range
            etBio.filters = range
            etPertamax.filters = range
            etPertalite.filters = range
            etDexlite.filters = range
            etPertadex.filters = range
            etTurbo.filters = range
        }
    }

    override fun afterTextChanged(s: Editable?) {
        with(binding) {
            submitButton.isEnabled = etPremium.text.isNotEmpty() || etBio.text.isNotEmpty()
                    || etPertamax.text.isNotEmpty() || etPertalite.text.isNotEmpty()
                    || etDexlite.text.isNotEmpty() || etPertadex.text.isNotEmpty()
                    || etTurbo.text.isNotEmpty()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun onClick(v: View) {
        binding.apply {
            var countType = 0
            val editTexts = listOf(etPremium, etBio, etPertamax, etPertalite, etDexlite, etPertadex, etTurbo)
            editTexts.forEach {
                if (it.text.isNotEmpty()) {
                    countType++
                }
            }

            if (countType > 4) {
                Snackbar.make(v, "Jenis bahan bakar yang dapat dipilih maksimal 4 jenis", Snackbar.LENGTH_SHORT).show()
            } else {

                val user = activity?.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
                val srId = user?.getString("salesRetailId", "missing").toString()
                val userName = user?.getString("userName", "missing").toString()
                val premium = if (etPremium.text.toString().isEmpty()) 0 else etPremium.text.toString().toInt()
                val bio = if (etBio.text.toString().isEmpty()) 0 else etBio.text.toString().toInt()
                val pertamax = if (etPertamax.text.toString().isEmpty()) 0 else etPertamax.text.toString().toInt()
                val pertalite = if (etPertalite.text.toString().isEmpty()) 0 else etPertalite.text.toString().toInt()
                val dexlite = if (etDexlite.text.toString().isEmpty()) 0 else etDexlite.text.toString().toInt()
                val pertadex = if (etPertadex.text.toString().isEmpty()) 0 else etPertadex.text.toString().toInt()
                val turbo = if (etTurbo.text.toString().isEmpty()) 0 else etTurbo.text.toString().toInt()
                MaterialAlertDialogBuilder(context)
                    .setMessage("Apakah anda yakin ingin MENGAJUKAN permintaan ini?")
                    .setNegativeButton("Tidak", null)
                    .setPositiveButton("Iya") { _, _ ->
                        orderVM.createOrder(
                            Order(
                                applicantName = userName,
                                type = type,
                                salesRetailId = srId,
                                orderVolume = OrderVolume(
                                    premium,
                                    bio,
                                    pertamax,
                                    pertalite,
                                    dexlite,
                                    pertadex,
                                    turbo
                                )
                            )
                        )
                        val action = EmergencyFragmentDirections.actionShowDashboard()
                        findNavController().navigate(action)
                        Snackbar.make(v, "Permintaan telah berhasil diajukan", Snackbar.LENGTH_SHORT).show()
                    }
                    .show()
            }
        }
    }
}
