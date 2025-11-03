package com.example.sistemarestaurante

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val dish: Dish = Dish(),
    var quantity: Int = 0
) : Parcelable {

    constructor() : this(Dish(), 0)
}