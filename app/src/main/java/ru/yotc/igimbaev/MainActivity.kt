package ru.yotc.igimbaev

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
    var username = ""
    var token = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       val logoutButton = findViewById<TextView>(R.id.logout_button)
        val login = findViewById<TextView>(R.id.logs)
        val onLoginResponce: (login: String, password: String)->Unit = { login, password ->
            // первым делом сохраняем имя пользователя,
            // чтобы при необходимости можно было разлогиниться
            username = login

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
                            token = jsonResp.getJSONObject("notice").getString("token")
                            runOnUiThread {
                                // тут можно переходить на следующее окно
                                Toast.makeText(this, "Success get token: $token", Toast.LENGTH_LONG)
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
        LoginDialog(onLoginResponce)
                .show(supportFragmentManager, null)
        logoutButton.setOnClickListener{
            HTTP.requestPOST(
                    "http://s4a.kolei.ru/logout",
                    JSONObject().put("username", username),
                    mapOf(
                            "Content-Type" to "application/json"
                    )
            ){result, error ->
                // при выходе не забываем стереть существующий токен
                token = ""

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

        login.setOnClickListener{
            LoginDialog(onLoginResponce)
                    .show(supportFragmentManager, null)
        }





    }

    fun perehod(view: View) {
        startActivityForResult(Intent(this, Product_Activity::class.java),1)
    }


}