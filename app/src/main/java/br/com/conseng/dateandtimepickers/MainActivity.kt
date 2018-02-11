package br.com.conseng.dateandtimepickers

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private lateinit var editTextDate: AppCompatEditText
    private lateinit var editTextTime: AppCompatEditText
    private lateinit var textLayoutDate: TextInputLayout
    private lateinit var textLayoutTime: TextInputLayout
    private lateinit var textFinalDateTime: TextView
    private lateinit var btnValidate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextDate = et_date
        editTextTime = et_time
        textLayoutDate = txtlayout_date
        textLayoutTime = txtlayout_time
        btnValidate = btn_validate
        textFinalDateTime = txt_new_date_time
    }

    /**
     * Valida a data e a hora definida e mostra na tela.
     */
    fun validateForm(view: View) {
        val validDate = validateDate()
        val validTime = validateTime()
        var newDate = "??/??/????"
        var newTime = "??:??:??"

        if (null != validDate) {
            val formatDate = SimpleDateFormat("dd/MM/yyyy")
            newDate = formatDate.format(validDate.time)
        }
        if (null != validTime) {
            val formatTime = SimpleDateFormat("HH:mm:ss")
            newTime = formatTime.format(validTime.time)
        }

        textFinalDateTime.text = "$newDate $newTime"
    }

    /**
     * Valida o formato e o valor da data definida na entrada [textLayoutDate].
     * Se inválida, atualiza a mensagem de erro em [textLayoutDate].
     * @return se a data for inválida, retorna null.
     *         se a data for válida, retorna com a nova data.
     */
    private fun validateDate(): Calendar? {
        var date: Calendar? = null
        val datePattern = Pattern.compile("(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/((19|20)[0-9][0-9])")
        val newDate: String = editTextDate.text.toString()

        if (newDate.isEmpty()) {
            textLayoutDate.isErrorEnabled = true
            textLayoutDate.error = getString(R.string.error_no_date)
        } else if (!datePattern.matcher(newDate).matches()) {
            textLayoutDate.isErrorEnabled = true
            textLayoutDate.error = getString(R.string.error_invalid_date_pattern)
        } else {
            var day: Int = 0
            var month: Int = 0
            var year: Int = 0
            try {
                day = newDate.substring(0, 2).toInt()
                month = newDate.substring(3, 5).toInt()
                year = newDate.substring(6, 10).toInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // Possui mensagem especial para ano bissexto
            val monthLimit = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
            val leapYear = if (0 != (year % 4)) {
                false
            } else if (0 != (year % 100)) {
                true
            } else {
                (0 == (year % 400))
            }

            if (day > monthLimit[month - 1]) {
                textLayoutDate.isErrorEnabled = true
                textLayoutDate.error = getString(R.string.error_invalid_day_of_month).format(monthLimit[month - 1])
            } else if ((2 == month) and (29 == day) and !leapYear) {
                textLayoutDate.isErrorEnabled = true
                textLayoutDate.error = getString(R.string.error_no_leap_year).format(year)
            } else {
                textLayoutDate.isErrorEnabled = false
                date = Calendar.getInstance()
                // No calendário, Janeiro = 0!
                date.set(year, month - 1, day)
            }
        }

        return date
    }

    /**
     * Valida o formato e o valor da hora definida na entrada [editTextTime].
     * Se inválida, atualiza a mensagem de erro em [editTextTime].
     * @return se a hora for inválida, retorna null.
     *         se a hora for válida, retorna com a nova hora.
     */
    private fun validateTime(): Calendar? {
        var time: Calendar? = null
        val timePattern = Pattern.compile("([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]")
        val newTime: String = editTextTime.text.toString()

        if (newTime.isEmpty()) {
            textLayoutTime.isErrorEnabled = true
            textLayoutTime.error = getString(R.string.error_no_time)
        } else if (!timePattern.matcher(newTime).matches()) {
            textLayoutTime.isErrorEnabled = true
            textLayoutTime.error = getString(R.string.error_invalid_time_pattern)
        } else {
            var hours: Int = 0
            var minutes: Int = 0
            var seconds: Int = 0
            try {
                hours = newTime.substring(0, 2).toInt()
                minutes = newTime.substring(3, 5).toInt()
                seconds = newTime.substring(6, 8).toInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            time = Calendar.getInstance()
            time.set(Calendar.HOUR_OF_DAY, hours)       // HOUR is strictly for 12 hours!
            time.set(Calendar.MINUTE, minutes)
            time.set(Calendar.SECOND, seconds)

            textLayoutTime.isErrorEnabled = false
        }

        return time
    }
}
