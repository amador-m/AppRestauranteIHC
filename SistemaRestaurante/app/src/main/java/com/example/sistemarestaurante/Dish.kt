package com.example.sistemarestaurante

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dish(
    var id: String = "",
    var name: String = "", 
    var description: String = "", 
    var price: Double = 0.0, 
    var category: String = "", 
    var isAvailable: Boolean = true,
    var imageUrl: String = ""
) : Parcelable {
    constructor() : this("", "", "", 0.0, "", true, "")
}
