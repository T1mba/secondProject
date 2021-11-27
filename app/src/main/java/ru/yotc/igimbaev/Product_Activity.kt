package ru.yotc.igimbaev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import ru.yotc.myapplication.HTTP
import java.lang.Exception

class Product_Activity : AppCompatActivity() {

    private lateinit var app: MyApp
    val productTypeList = ArrayList<ProductType>()
    private lateinit var ProductTypeSpinner: Spinner
    private lateinit var callback: (result: String?, error: String) -> Unit
    private lateinit var productRecyclerView: RecyclerView
    private val filteredProductList = ArrayList<Product>()
    var productFilterID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_)
        app = applicationContext as MyApp
        ProductTypeSpinner = findViewById(R.id.productTypeSpinner)
        productRecyclerView = findViewById<RecyclerView>(R.id.productView)


        productRecyclerView.layoutManager =
            LinearLayoutManager(this)

        val productAdapter = ProductAdapter(filteredProductList, this)
        productAdapter.setItemClickListener {
            runOnUiThread {
                app.currentProduct = it
                startActivity(
                    Intent(this, Material_activity::class.java)
                )
            }


        }
        productRecyclerView.adapter = productAdapter

        ProductTypeSpinner.adapter = ArrayAdapter(
            this,
            R.layout.producttype_spinner,
            productTypeList
        )




        ProductTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                productFilterID = productTypeList[position].id
                updateProductList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                
            }


        }

        getProduct()
        getProductType()
    }

    private fun getProductType() {
        HTTP.requestGET(
            "http://s4a.kolei.ru/ProductType",
            mapOf(
                "token" to app.token
            )
        ) { result, error ->
            if (result != error)
                try {
                    productTypeList.clear()
                    val json = JSONObject(result)
                    if (!json.has("notice"))
                        throw Exception("Не верный формат, ожидался объект Notice")
                    if (json.getJSONObject("notice").has("data")) {
                        val data = json.getJSONObject("notice").getJSONArray("data")
                        for (i in 0 until data.length()) {
                            val item = data.getJSONObject(i)
                            productTypeList.add(
                                ProductType(
                                    item.getInt("ID"),
                                    item.getString("Title")

                                )
                            )
                        }
                        productTypeList.add(0, ProductType(0, "Показать всё"))
                        runOnUiThread {
                            (ProductTypeSpinner.adapter as ArrayAdapter<*>).notifyDataSetChanged()
                        }
                    } else {
                        throw Exception("Не верный формат ответа, ожидался объект token")
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
    }

    private fun getProduct() {
        if (app.token.isNotEmpty()) {
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
                        if (json.getJSONObject("notice").has("data")) {
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
                        } else {
                            throw Exception("Не верный формат ответа, ожидался объект token")
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

    }

    private fun updateProductList() {
        filteredProductList.clear()
        for (i in 0 until app.productList.size) {
            if (productFilterID == 0 || productFilterID == app.productList[i].productTypeID)
                filteredProductList.add(app.productList[i])
        }
        runOnUiThread {
            productRecyclerView.adapter?.notifyDataSetChanged()
        }




    }
}