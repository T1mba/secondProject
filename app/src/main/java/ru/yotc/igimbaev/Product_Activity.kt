package ru.yotc.igimbaev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import ru.yotc.myapplication.HTTP
import java.lang.Exception

class Product_Activity : AppCompatActivity() {

    private lateinit var app: MyApp

    private lateinit var callback: (result: String?, error: String) -> Unit
    private lateinit var productRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_)
        app = applicationContext as MyApp
        val productAdapter = ProductAdapter(app.productList, this)
        productAdapter.setItemClickListener {
            runOnUiThread {
                app.currentProduct = it
                startActivity(
                        Intent(this, Material_activity::class.java)
                )
            }


        }
        productRecyclerView.adapter = productAdapter
        productRecyclerView = findViewById<RecyclerView>(R.id.productView)

        if(app.token.isNotEmpty()) {
            HTTP.requestGET(
                "http://s4a.kolei.ru/Product",
                mapOf(
                    "token" to app.token
                )
            ) { result, error ->

                if (result != null)
                    try {
                        app.productList.clear()
                        val json = JSONObject(result)
                        if (!json.has("notice"))
                            throw Exception("Не верный формат ответа, ожидался объект notice")
                        if(json.getJSONObject("notice").has("data")) {
                            val data = json.getJSONObject("notice").getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val item = data.getJSONObject(i)
                                app.productList.add(
                                    Product(
                                        item.getInt("ID"),
                                        item.getString("Title"),
                                        item.getInt("ProductTypeID"),
                                        item.getInt("ArticleNumber"),
                                        item.getString("Description"),
                                        item.getString("Image"),
                                        item.getInt("ProductionPersonCount"),
                                        item.getInt("ProductionWorkshopNumber"),
                                        item.getInt("MinCostForAgent")

                                    )
                                )
                            }


                            runOnUiThread {
                                productRecyclerView.adapter?.notifyDataSetChanged()
                            }
                        }
                        else
                        {
                            throw Exception("Не верный формат ответа, ожидался объект token")
                        }
                    }
                    catch (e: Exception){
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
        else
            Toast.makeText(this, "Не найден токен, нужно залогиниться", Toast.LENGTH_LONG)
                .show()
            productRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

    }


}