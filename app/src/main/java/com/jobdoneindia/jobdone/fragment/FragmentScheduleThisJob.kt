package com.jobdoneindia.jobdone.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import com.jobdoneindia.jobdone.R
import java.util.*

class FragmentScheduleThisJob : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_schedule_this_job, container, false)

        // variables init
        val datePicker = root.findViewById<DatePicker>(R.id.datePicker)
        val timePicker = root.findViewById<TimePicker>(R.id.timePicker)

        // get date
        val today = Calendar.getInstance()
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)) {
            view, year, month, day ->
                val month = month + 1
                val msg = "You selected: $day/$month/$year"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        // get time
        timePicker.setOnTimeChangedListener { _, hour, minute ->
            var hour = hour
            var am_pm = ""
            // AM_PM decider logic
            when { hour == 0 -> {
                hour += 12
                am_pm = "AM"
            }
            hour == 12 -> am_pm = "PM"
            hour > 12 -> {
                hour -= 12
                am_pm = "PM"
            } else -> am_pm = "AM"
            }

            val strHour = if (hour < 10) "0" + hour.toString() else hour
            val min = if (minute < 10) "0" + minute else minute
            val msg = "Time: $strHour:$min $am_pm"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }


        return root
    }
}