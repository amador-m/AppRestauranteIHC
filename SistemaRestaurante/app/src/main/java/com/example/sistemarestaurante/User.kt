package com.example.sistemarestaurante

enum class UserType {
    CLIENT,
    EMPLOYEE,
    ADMIN
}

data class User(
    var id: String = "",
    val name: String = "",
    val email: String = "",
    val userType: UserType = UserType.CLIENT,
    val username: String = "",
    val profileImageUrl: String = "",
    val dateOfBirth: String = "",
    val phone: String = "",
    val loyaltyPoints: Int = 0, // Pontos atuais (Ex: 0 a 9)
    val coupons: Int = 0        // Cupons acumulados
) {
    constructor() : this("", "", "", UserType.CLIENT, "", "", "", "", 0, 0)
}