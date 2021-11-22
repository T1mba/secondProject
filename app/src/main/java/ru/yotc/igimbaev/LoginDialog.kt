package ru.yotc.igimbaev

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import org.json.JSONObject
import ru.yotc.myapplication.HTTP

class LoginDialog(private val callback: (login: String, password: String)-> Unit): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog{
        return activity?.let    {
            val builder = AlertDialog.Builder(it)
            var username = ""
            var token = ""
            val loginLayout = layoutInflater.inflate(R.layout.dialog_registr, null)
            val loginText = loginLayout.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.login)
            val loginError = loginLayout.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.login_error)
            val password = loginLayout.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.password)
            val passwordEror = loginLayout.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.password_error)
            val loginButton = loginLayout.findViewById<TextView>(R.id.login_button)

            val myDialog = builder.setView(loginLayout)
                .setTitle("Авторизация!")
                .setIcon(R.mipmap.ico)
                .create()
            loginButton.setOnClickListener {
                var hasErrors = false
                if(loginText.text.isNullOrEmpty()){
                    hasErrors = true
                    loginError.error = "Поле должно быть заполнено"
                }
                else{
                    loginError.error = ""
                }
                if (password.text.isNullOrEmpty()){
                    hasErrors = true
                    passwordEror.error = "Поле должно быть заполнено"
                }
                else{
                    passwordEror.error = ""
                }
                if(!hasErrors){
                    myDialog.dismiss()
                    callback.invoke(
                        loginText.text.toString(),
                        password.text.toString()
                    )
                }
            }



            myDialog
        }?: throw IllegalStateException("Activity cannot be null")

    }
}