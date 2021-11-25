package ru.yotc.igimbaev

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import org.json.JSONObject
import ru.yotc.myapplication.HTTP
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var counter = 0
    var ready = false
    private lateinit var app: MyApp



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        app = applicationContext as MyApp
        val loginLayout = layoutInflater.inflate(R.layout.dialog_registr, null)
        val loginText = loginLayout.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.login)
        val password = loginLayout.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.password)
        val splash = findViewById<ImageView>(R.id.splash)
       val logoutButton = findViewById<TextView>(R.id.logout_button)
        val logint = findViewById<TextView>(R.id.logs)
        val onLoginResponce: (login: String, password: String)->Unit = { login, password ->
            // первым делом сохраняем имя пользователя,
            // чтобы при необходимости можно было разлогиниться
            app.username = login

            // затем формируем JSON объект с нужными полями
            val json = JSONObject()
            json.put("username", login)
            json.put("password", password)

            // и вызываем POST-запрос /login
            // в параметрах не забываем указать заголовок Content-Type
            HTTP.requestPOST(
                    "http://s4a.kolei.ru/login",
                    json,
                    mapOf(
                            "Content-Type" to "application/json"
                    )
            ){result, error ->
                if(result!=null){
                    try {
                        // анализируем ответ
                        val jsonResp = JSONObject(result)

                        // если нет объекта notice
                        if(!jsonResp.has("notice"))
                            throw Exception("Не верный формат ответа, ожидался объект notice")

                        // есть какая-то ошибка
                        if(jsonResp.getJSONObject("notice").has("answer"))
                            throw Exception(jsonResp.getJSONObject("notice").getString("answer"))

                        // есть токен!!!
                        if(jsonResp.getJSONObject("notice").has("token")) {
                            app.token = jsonResp.getJSONObject("notice").getString("token")
                            runOnUiThread {

                                // тут можно переходить на следующее окно
                                Toast.makeText(this, "Success get token: ${app.token}", Toast.LENGTH_LONG)
                                        .show()



                            }
                        }
                        else
                            throw Exception("Не верный формат ответа, ожидался объект token")
                    } catch (e: Exception){
                        runOnUiThread {
                            AlertDialog.Builder(this)
                                    .setTitle("Ошибка")
                                    .setMessage(e.message)
                                    .setPositiveButton("OK", null)
                                    .create()
                                    .show()
                        }
                    }
                } else
                    runOnUiThread {
                        AlertDialog.Builder(this)
                                .setTitle("Ошибка http-запроса")
                                .setMessage(error)
                                .setPositiveButton("OK", null)
                                .create()
                                .show()
                    }
            }




        }
        object : CountDownTimer(5000,1000){
            override fun onTick(millisUntilFinished: Long) {
                // заставляем пялиться на нашу заставку как минимум 3 секунды
                counter++
                if(counter>3 && ready){
                    // данные получены - скрываем заставку
                    splash.elevation = 0F
                    this.cancel()
                }
            }

            override fun onFinish(){
                splash.elevation = 0F
            }
        }.start()

        logoutButton.setOnClickListener{

            HTTP.requestPOST(
                    "http://s4a.kolei.ru/logout",
                    JSONObject().put("username", app.username),
                    mapOf(
                            "Content-Type" to "application/json"
                    )
            ){result, error ->
                // при выходе не забываем стереть существующий токен
                app.token = ""

                // каких-то осмысленных действий дальше не предполагается
                // разве что снова вызвать форму авторизации
                runOnUiThread {
                    if(result!=null) {
                        Toast.makeText(this, "Logout success!", Toast.LENGTH_LONG).show()
                    }
                    else {
                        AlertDialog.Builder(this)
                                .setTitle("Ошибка http-запроса")
                                .setMessage(error)
                                .setPositiveButton("OK", null)
                                .create()
                                .show()
                    }
                }
            }
        }

        logint.setOnClickListener{
            LoginDialog(onLoginResponce)
                    .show(supportFragmentManager, null)
        }


        LoginDialog(onLoginResponce)
                .show(supportFragmentManager, null)



    }

    fun perehod(view: View) {
        startActivityForResult(Intent(this, Product_Activity::class.java),1)
    }


}