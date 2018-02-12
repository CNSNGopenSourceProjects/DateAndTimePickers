package br.com.conseng.dateandtimepickers

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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

/**
 * Exemplo da funcionalida de janela de diálogo para obter data e hora do androide.
 * @see [https://developer.android.com/guide/topics/ui/controls/pickers.html]
 */
class MainActivity : AppCompatActivity() {

    private lateinit var editTextDate: AppCompatEditText
    private lateinit var editTextTime: AppCompatEditText
    private lateinit var textLayoutDate: TextInputLayout
    private lateinit var textLayoutTime: TextInputLayout
    private lateinit var textFinalDateTime: TextView
    private lateinit var textCurrentTheme: TextView
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
        textCurrentTheme = txt_pick_theme
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
     * @param [showError] indica se deve mostrar a mensagem de erro (true) ou não.
     * @return se a data for inválida, retorna null.
     *         se a data for válida, retorna com a nova data.
     */
    private fun validateDate(showError: Boolean = true): Calendar? {
        var date: Calendar? = null
        val datePattern = Pattern.compile("(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/((19|20)[0-9][0-9])")
        val newDate: String = editTextDate.text.toString()

        if (newDate.isEmpty()) {
            showErrorMessage(showError, textLayoutDate, R.string.error_no_date)
        } else if (!datePattern.matcher(newDate).matches()) {
            showErrorMessage(showError, textLayoutDate, R.string.error_invalid_date_pattern)
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
                showErrorMessage(showError, textLayoutDate, R.string.error_invalid_day_of_month, monthLimit[month - 1])
            } else if ((2 == month) and (29 == day) and !leapYear) {
                showErrorMessage(showError, textLayoutDate, R.string.error_no_leap_year, year)
            } else {
                if (showError) textLayoutDate.isErrorEnabled = false
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
     * @param [showError] indica se deve mostrar a mensagem de erro (true) ou não.
     * @return se a hora for inválida, retorna null.
     *         se a hora for válida, retorna com a nova hora.
     */
    private fun validateTime(showError: Boolean = true): Calendar? {
        var time: Calendar? = null
        val timePattern = Pattern.compile("([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]")
        val newTime: String = editTextTime.text.toString()

        if (newTime.isEmpty()) {
            showErrorMessage(showError, textLayoutTime, R.string.error_no_time)
        } else if (!timePattern.matcher(newTime).matches()) {
            showErrorMessage(showError, textLayoutTime, R.string.error_invalid_time_pattern)
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

            if (showError) textLayoutTime.isErrorEnabled = false
        }

        return time
    }

    /**
     * Mostra a mensagem de erro da data no [view] da tela.
     * @param [showError] indica se deve mostrar a mensagem de erro (true) ou não.
     * @param [view] campo de leiaute onde a mensagem de erro deve ser apresentada.
     * @param [msgId] código da string com a mensagem de erro.
     * @param [args] parâmetros para a formatação da mensagem de erro (se houver)
     */
    private fun showErrorMessage(showError: Boolean, view: TextInputLayout, msgId: Int, vararg args: Any?) {
        if (showError) {
            view.isErrorEnabled = true
            view.error = if (null == args) getString(msgId) else getString(msgId).format(args)
        }
    }

    /**
     * Lista as várias opções de tema disponíveis para o PICK-DATE.
     * @see [https://android--code.blogspot.com.br/2015/08/android-timepickerdialog-theme.html]
     * @see [https://android--code.blogspot.com.br/2015/08/android-datepickerdialog-theme.html]
     */
    private val pickThemeOptions = intArrayOf(
            android.R.style.Theme_DeviceDefault_Dialog_Alert,
            android.R.style.Theme_DeviceDefault_Light_Dialog_Alert,
            android.R.style.Theme_Material_Dialog_Alert,
            android.R.style.Theme_Material_Light_Dialog_Alert,
            android.R.style.Theme_Material_Dialog_Presentation,
            android.R.style.Theme_Dialog,
            android.R.style.Theme_Holo_Dialog,
            android.R.style.Theme_Holo_Dialog_MinWidth,
            android.R.style.Theme_Holo_Light_Dialog,
            android.R.style.Theme_Holo_Light_DialogWhenLarge)

    /**
     * Lista as várias opções de tema disponíveis para o PICK-DATE para apresentação .
     * @see [https://android--code.blogspot.com.br/2015/08/android-timepickerdialog-theme.html]
     * @see [https://android--code.blogspot.com.br/2015/08/android-datepickerdialog-theme.html]
     */
    private val datePickThemeOptionsText = arrayListOf(
            "android.R.style.Theme_DeviceDefault_Dialog_Alert",
            "android.R.style.Theme_DeviceDefault_Light_Dialog_Alert",
            "android.R.style.Theme_Material_Dialog_Alert",
            "android.R.style.Theme_Material_Light_Dialog_Alert",
            "android.R.style.Theme_Material_Dialog_Presentation",
            "android.R.style.Theme_Dialog",
            "android.R.style.Theme_Holo_Dialog",
            "android.R.style.Theme_Holo_Dialog_MinWidth",
            "android.R.style.Theme_Holo_Light_Dialog",
            "android.R.style.Theme_Holo_Light_DialogWhenLarge")

    /**
     * Aponta para o último tema utilizado.
     * @see [https://android--code.blogspot.com.br/2015/08/android-timepickerdialog-theme.html]
     * @see [https://android--code.blogspot.com.br/2015/08/android-datepickerdialog-theme.html]
     */
    private var lastPickThemeOption = -1

    /**
     * Retorna a identificação do próximo tema a ser utilizado no diálogo para data e hora.
     * Atualiza o [textCurrentTheme] com o nome do tema utilizado.
     * @param [forDate] indica se o tema será utilizado para o diálogo de data (true) ou de hora (false).
     * @return código do tema no recurso do antroide.
     * @see [https://android--code.blogspot.com.br/2015/08/android-timepickerdialog-theme.html]
     * @see [https://android--code.blogspot.com.br/2015/08/android-datepickerdialog-theme.html]
     */
    private fun getNextPickTheme(forDate: Boolean): Int {
        val who = if (forDate) "DatePicker: " else "TimePicker: "
        if ((lastPickThemeOption < 0) or (lastPickThemeOption >= datePickThemeOptionsText.size - 1))
            lastPickThemeOption = 0
        else
            lastPickThemeOption++
        textCurrentTheme.text = who + datePickThemeOptionsText[lastPickThemeOption]
        return pickThemeOptions[lastPickThemeOption]
    }

    /**
     * Abre uma da janela de diálogo padrão do Androide para definir uma data.
     */
    fun onClickPickDate(view: View) {
        val currentDate = validateDate(false) ?: Calendar.getInstance()
        val day = currentDate.get(Calendar.DAY_OF_MONTH)
        val month = currentDate.get(Calendar.MONTH)
        val year = currentDate.get(Calendar.YEAR)
        val dpd = DatePickerDialog(this, getNextPickTheme(true),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Considera Janeiro = 0, por isso de incrementar o mês!
                    val newDate = "%02d/%02d/%04d".format(dayOfMonth, monthOfYear + 1, year)
                    editTextDate.setText(newDate)
                }, year, month, day)
        dpd.show()
    }

    /**
     * Abre uma da janela de diálogo padrão do Androide para definir uma hora.
     */
    fun onClickPickTime(view: View) {
        val currentTime = validateTime(false) ?: Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)
//        val second = currentTime.get(Calendar.SECOND)
        val tpd = TimePickerDialog(this, getNextPickTheme(false),
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    val newTime = "%02d:%02d:00".format(hourOfDay, minute)
                    editTextTime.setText(newTime)
                }, hour, minute, true)
        tpd.show()
    }
}
