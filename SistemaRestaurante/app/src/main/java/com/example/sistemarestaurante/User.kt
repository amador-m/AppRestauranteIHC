package com.example.sistemarestaurante

enum class UserType {
    CLIENT,
    EMPLOYEE,
    ADMIN
}

data class User(
    val email: String = "",
    val userType: UserType = UserType.CLIENT,

    // Novos campos de perfil
    val name: String = "",
    val username: String = "",
    val profileImageUrl: String = "",
    val dateOfBirth: String = "",
    val phone: String = ""
)