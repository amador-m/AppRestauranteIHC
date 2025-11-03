package com.example.sistemarestaurante

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dish(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    var isAvailable: Boolean = true, // <--- MUDANÇA AQUI (de val para var)
    val imageUrl: String = ""
) : Parcelable {

    // Construtor vazio (atualizado com o campo imageUrl da última vez)
    constructor() : this("", "", "", 0.0, "", true, "")
}