package ru.yotc.igimbaev

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import ru.yotc.myapplication.HTTP
import java.lang.Exception

class Product_Activity : AppCompatActivity() {
    private val productList = ArrayList<Product>()
    var token = ""
    private lateinit var callback: (result: String?, error: String) -> Unit
    private lateinit var productRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_)
        productRecyclerView = findViewById<RecyclerView>(R.id.productView)
        val resultTextView = findViewById<TextView>(R.id.result)
        if(token.isNotEmpty()) {
            HTTP.requestGET(
                "http://s4a.kolei.ru/Product",
                mapOf(
                    "token" to token
                )
            ) { result, error ->

                if (result != null)
                    try {
                        productList.clear()
                        val json = JSONObject(result)
                        if (!json.has("notice"))
                            throw Exception("Не верный формат ответа, ожидался объект notice")
                        if(json.getJSONObject("notice").has("data")) {
                            val data = json.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val item = data.getJSONObject(i)
                                productList.add(
                                    Product(
                                        item.getInt("ID"),
                                        item.getString("Title"),
                                        item.getInt("productTypeID"),
                                        item.getInt("articleNumber"),
                                        item.getString("description"),
                                        item.getString("Image"),
                                        item.getInt("production_person_count"),
                                        item.getInt("production_workshop_number"),
                                        item.getInt("minCost")

                                    )
                                )
                            }


                            runOnUiThread {
                                productRecyclerView.adapter?.notifyDataSetChanged()
                                if (result != null) {
                                    resultTextView.text = result
                                } else
                                    resultTextView.text = "ошибка: $error"

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
        val productAdapter = ProductAdapter(productList, this)
        productAdapter.setItemClickListener {
            runOnUiThread{
                showDetalisInfo(it)
            }
        }
            productRecyclerView.adapter = productAdapter
    }

    private fun showDetalisInfo(it: Product) {

    }
}