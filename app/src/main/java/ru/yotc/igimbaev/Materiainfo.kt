package ru.yotc.igimbaev

data class Materiainfo(
        val ID: Int,
    val Title: String,
    val CountInPack: String,
    val Unit: String,
    val CountInStock: String,
        val MinCount: Int,
        val Description: String?,
    val Cost: String,
    val Image: String?,
        val MaterialTypeID: Int
)
