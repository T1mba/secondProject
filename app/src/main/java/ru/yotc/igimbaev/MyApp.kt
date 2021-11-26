package ru.yotc.igimbaev

import android.app.Application
import org.json.JSONObject

class MyApp : Application() {
    var token = ""
    var username = ""
    var materialList = ArrayList<Materiainfo>()
    private  var size: Int = 0
    val productList = ArrayList<Product>()
    var productmaterialList = ArrayList<ProductMaterial>()
    val filtredMaterialList = ArrayList<Materiainfo>()
    var currentProduct: Product?=null
}
