package com.example.sistemarestaurante

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dish(
    var id: String = "",
    var name: String = "", // MUDADO DE VAL
    var description: String = "", // MUDADO DE VAL
    var price: Double = 0.0, // MUDADO DE VAL
    var category: String = "", // MUDADO DE VAL
    var isAvailable: Boolean = true,
    var imageUrl: String = "" // MUDADO DE VAL
) : Parcelable {

    // Construtor vazio
    constructor() : this("", "", "", 0.0, "", true, "")
}