package com.pertamina.spbucare.ui


import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pertamina.spbucare.R
import com.pertamina.spbucare.databinding.FragmentCreateOrderManualBinding
import com.pertamina.spbucare.model.Order
import com.pertamina.spbucare.model.OrderConfirmation
import com.pertamina.spbucare.model.OrderVolume
import com.pertamina.spbucare.model.User
import com.pertamina.spbucare.util.InputFilterMinMax
import com.pertamina.spbucare.viewmodel.OrderViewModel
import com.pertamina.spbucare.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*

class CreateOrderManualFragment : Fragment(), TextWatcher {
    private lateinit var binding: FragmentCreateOrderManualBinding
    private val orderVM: OrderViewModel by viewModel { parametersOf("") }
    private val userViewModel: UserViewModel by viewModel { parametersOf("spbu") }
    private lateinit var listType: List<String>
    private var listSpbu = listOf<User>()
    private var cal = Calendar.getInstance()
    private var dateInput = cal.time

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateOrderManualBinding.inflate(inflater, container, false)
        listType = resources.getStringArray(R.array.order_type).toList()
        setEditTextListener()
        setAdapter()
        setRangeVolume()
        setupListener()
        return binding.root
    }

    private fun setAdapter() {
        binding.apply {
            typeDropdown.setAdapter(configureAdapter(listType))
            userViewModel.users.observe(viewLifecycleOwner, Observer { list ->
                listSpbu = list
                employeeDropdown.setAdapter(configureAdapter(listSpbu.map { it.name }))
            })
        }
    }

    private fun configureAdapter(stringArray: List<String>) =
            ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_item,
                    stringArray
            )

    private fun setEditTextListener() {
        binding.apply {
            typeDropdown.addTextChangedListener(this@CreateOrderManualFragment)
            employeeDropdown.addTextChangedListener(this@CreateOrderManualFragment)
            etPremium.addTextChangedListener(this@CreateOrderManualFragment)
            etBio.addTextChangedListener(this@CreateOrderManualFragment)
            etPertamax.addTextChangedListener(this@CreateOrderManualFragment)
            etPertalite.addTextChangedListener(this@CreateOrderManualFragment)
            etDexlite.addTextChangedListener(this@CreateOrderManualFragment)
            etPertadex.addTextChangedListener(this@CreateOrderManualFragment)
            etTurbo.addTextChangedListener(this@CreateOrderManualFragment)
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

            btnSubmit.isEnabled = ((typeDropdown.text.isNotEmpty() && employeeDropdown.text.isNotEmpty() && etDate.text.toString().isNotEmpty())
                    && (etPremium.text.isNotEmpty() || etBio.text.isNotEmpty()
                    || etPertamax.text.isNotEmpty() || etPertalite.text.isNotEmpty()
                    || etDexlite.text.isNotEmpty() || etPertadex.text.isNotEmpty()
                    || etTurbo.text.isNotEmpty()
                    ))

        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    private fun hideKeyboard(view: View) {
        val inputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setupListener() {
        binding.apply {
            val dateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                updateDateInView(year, monthOfYear, dayOfMonth)
            }
            etDate.setOnClickListener {
                showCalendar(dateListener)
            }

            btnSubmit.setOnClickListener { v ->
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
                    val type = typeDropdown.text.toString()
                    var userId = ""
                    var srId = ""
                    var userName = ""
                    listSpbu.forEach {
                        if (employeeDropdown.text.toString().equals(it.name, true)) {
                            userId = it.userId
                            srId = it.salesRetailId
                            userName = it.name
                        }
                    }
                    val premium = if (etPremium.text.toString().isEmpty()) 0 else etPremium.text.toString().toInt()
                    val bio = if (etBio.text.toString().isEmpty()) 0 else etBio.text.toString().toInt()
                    val pertamax = if (etPertamax.text.toString().isEmpty()) 0 else etPertamax.text.toString().toInt()
                    val pertalite = if (etPertalite.text.toString().isEmpty()) 0 else etPertalite.text.toString().toInt()
                    val dexlite = if (etDexlite.text.toString().isEmpty()) 0 else etDexlite.text.toString().toInt()
                    val pertadex = if (etPertadex.text.toString().isEmpty()) 0 else etPertadex.text.toString().toInt()
                    val turbo = if (etTurbo.text.toString().isEmpty()) 0 else etTurbo.text.toString().toInt()

                    MaterialAlertDialogBuilder(context)
                            .setMessage("Apakah anda yakin ingin MEMBUAT pesanan (Back Date) ini?")
                            .setNegativeButton("Tidak", null)
                            .setPositiveButton("Iya") { _, _ ->
                                orderVM.createOrder(
                                        Order(
                                                userId = userId,
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
                                                ),
                                                orderConfirmation = OrderConfirmation(
                                                        true,
                                                        true,
                                                        true,
                                                        true,
                                                        true
                                                ),
                                                createdOn = dateInput,
                                                modifiedOn = dateInput,
                                                manual = true
                                        ), true
                                )
                                val action = CreateOrderManualFragmentDirections.actionManageUser()
                                findNavController().navigate(action)
                                Snackbar.make(v, "Permintaan (Back Date) telah berhasil dibuat", Snackbar.LENGTH_SHORT).show()
                            }
                            .show()
                }
            }
        }
    }

    private fun updateDateInView(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val myFormat = "dd-MM-yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        val currentDate = Calendar.getInstance()
        currentDate.add(Calendar.DATE, -1)
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        dateInput = cal.time
        binding.etDate.setText(sdf.format(cal.time))
    }

    private fun showCalendar(dateListener: DatePickerDialog.OnDateSetListener) {
        DatePickerDialog(
                requireContext(),
                dateListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}