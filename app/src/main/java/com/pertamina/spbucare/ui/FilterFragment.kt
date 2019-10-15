package com.pertamina.spbucare.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pertamina.spbucare.R
import kotlinx.android.synthetic.main.fragment_dialog_filter.*
import java.text.SimpleDateFormat
import java.util.*




class FilterFragment(private val listener: IFilterListener, private var beginDate: Date, private var endDate: Date) : DialogFragment(), TextWatcher {
    private val myFormat = "dd-MM-yyyy" // mention the format you need
    private val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

    private var cal = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_filter, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val strBeginDate = sdf.format(beginDate.time)
        val strEndDate = sdf.format(endDate.time)
        date_begin_input.setText(strBeginDate)
        date_end_input.setText(strEndDate)
        setupListener(strBeginDate, strEndDate)
    }

    private fun setupListener(strBeginDate: String, strEndDate: String) {
        val dateBeginListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            updateDateInView("begin", year, monthOfYear, dayOfMonth)
        }
        val dateEndListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            updateDateInView("end", year, monthOfYear, dayOfMonth)
        }
        date_begin_input.setOnClickListener {
            showCalendar(dateBeginListener, strBeginDate)
        }

        date_end_input.setOnClickListener {
            showCalendar(dateEndListener, strEndDate)
        }

        btn_filter.setOnClickListener {
            listener.onFilterOrder(beginDate, endDate)
            dismiss()
        }
    }

    private fun showCalendar(dateListener: DatePickerDialog.OnDateSetListener, dateParam: String) {
        val date = dateParam.split("-")
        DatePickerDialog(
            requireContext(),
            dateListener,
            // set DatePickerDialog to point to today's date when it loads up
            date[2].toInt(),
            date[1].toInt() - 1,
            date[0].toInt()
        ).show()

    }

    private fun updateDateInView(type: String, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val currentDate = Calendar.getInstance()
        currentDate.add(Calendar.DATE, -1)
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        when (type) {
            "begin" -> {
                beginDate = cal.time
                date_begin_input.setText(sdf.format(cal.time))
            }
            "end" -> {
                endDate = cal.time
                date_end_input.setText(sdf.format(cal.time))
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        btn_filter.isEnabled = date_begin_input.text.isNotEmpty() && date_end_input.text.isNotEmpty()
    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    interface IFilterListener {
        fun onFilterOrder(beginDate: Date, endDate: Date)
    }
}