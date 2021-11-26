package ru.yotc.igimbaev

import android.graphics.Bitmap

data class Product(
    val ID: Int,
    val Title: String,
    val productTypeID: Int,
    val articleNumber: Int,
    val description: String?,
    val image: String,
    val production_person_count: Int,
    val production_workshop_number: Int,
    val minCost: Int,
    var bitmap: Bitmap? = null
)
