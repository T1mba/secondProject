package ru.yotc.igimbaev

data class Product(
    val iD: Int,
    val title: String,
    val productTypeID: Int,
    val articleNumber: Int,
    val description: String?,
    val image: String,
    val production_person_count: Int,
    val production_workshop_number: Int,
    val minCost: Int
)
