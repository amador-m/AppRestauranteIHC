package com.example.sistemarestaurante

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CartItem(
    var dish: Dish = Dish(),
    var quantity: Int = 0
) : Parcelable {

    constructor() : this(Dish(), 0)
}