package com.example.sistemarestaurante

// Enum para os tipos de usuário (RF22)
enum class UserType {
    CLIENT,
    ADMIN,
    EMPLOYEE
}

/**
     * Modelo de dados para armazenar informações do usuário no Firestore (RF21)
     * @param id UID do Firebase Authentication
     * @param name Nome completo do usuário
     * @param email Email do usuário
     * @param userType Permissão de acesso (Cliente, Funcionário, Admin)
 */

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val userType: UserType = UserType.CLIENT // O padrão continua CLIENT (RF22)
) {
    // Construtor vazio necessário para o Firebase Realtime Database
    constructor() : this("", "", "", UserType.CLIENT)
}