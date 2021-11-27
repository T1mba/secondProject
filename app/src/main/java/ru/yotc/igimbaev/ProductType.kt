package ru.yotc.igimbaev

class ProductType(val id: Int, val title: String) {
    override fun toString(): String{
        return title
    }
}