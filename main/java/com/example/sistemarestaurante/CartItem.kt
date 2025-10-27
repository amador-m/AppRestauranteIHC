package com.example.sistemarestaurante

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val dish: Dish, // <-- AQUI Ele espera que Dish seja um objeto válido e Parcelable
    var quantity: Int
) : Parcelable