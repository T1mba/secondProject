package ru.yotc.igimbaev

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import ru.yotc.myapplication.HTTP
import java.lang.Exception

class Material_activity : AppCompatActivity() {
    private lateinit var app: MyApp

    private lateinit var materialview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_activity)
        app = applicationContext as MyApp
        if (app.currentProduct != null)
            showDetailsinfo(app.currentProduct!!)
        materialview = findViewById(R.id.materialview)
        if (app.token != "") {
            HTTP.requestGET(
                    "http://s4a.kolei.ru/Material",
                    mapOf("token" to app.token)

            ) { result, error ->
                if (result != null)
                    try {
                        app.materialList.clear()
                        val json = JSONObject(result)
                        if (!json.has("notice"))
                            throw Exception("Не верный формат ответа, ожидался объект notice")
                        if (json.getJSONObject("notice").has("data")) {
                            val data = json.getJSONObject("notice").getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val item = data.getJSONObject(i)

                                app.materialList.add(
                                        Materiainfo(
                                                    item.getInt("ID"),
                                                item.getString("Title"),
                                                item.getString("CountInPack"),
                                                item.getString("Unit"),
                                                item.getString("CountInStock"),
                                                item.getInt("MinCount"),
                                                item.getString("Description"),
                                                item.getString("Cost"),
                                                item.getString("Image"),
                                                item.getInt("MaterialTypeID")
                                        )
                                )
                            }
                            runOnUiThread {
                                materialview.adapter?.notifyDataSetChanged()
                            }
                        } else {
                            throw Exception("Не верный формат ответа ожидался token")
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            AlertDialog.Builder(this)
                                    .setTitle("Ошибка")
                                    .setMessage(e.message)
                                    .setPositiveButton("OK", null)
                                    .create()
                                    .show()
                        }
                    }

            }
        } else
            Toast.makeText(this, "Не найден токен, нужно залогиниться", Toast.LENGTH_LONG)
                    .show()
        materialview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val materialAdapter = MaterialAdapter(app.materialList, this)
        materialAdapter.setItemClickListener {

        }
        materialview.adapter = materialAdapter
        HTTP.requestGET(
                "http://s4a.kolei.ru/ProductMaterial",
                mapOf("token" to app.token)

        ) { result, error ->
            if (result != null)
                try {
                    app.productmaterialList.clear()
                    val json = JSONObject(result)
                    if (!json.has("notice"))
                        throw Exception("Не верный формат ответа, ожидался объект notice")
                    if (json.getJSONObject("notice").has("data")) {
                        val data = json.getJSONObject("notice").getJSONArray("data")
                        for (i in 0 until data.length()) {
                            val item = data.getJSONObject(i)

                            app.productmaterialList.add(
                                    ProductMaterial(
                                            item.getInt("ProductID"),
                                            item.getInt("MaterialID"),
                                            item.getInt("Count")
                                    )

                            )

                        }

                    }
                    else
                    {
                        throw Exception("Ошибка")
                    }
                }
                catch (e:Exception){
                    runOnUiThread {
                        AlertDialog.Builder(this)
                                .setTitle("Ошибка")
                                .setMessage(e.message)
                                .setPositiveButton("OK", null)
                                .create()
                                .show()
                    }

                }
        }

    }

    private fun showDetailsinfo(it: Product) {
        var image:ImageView = findViewById(R.id.logo)
    }
}