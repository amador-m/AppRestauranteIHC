package com.example.sistemarestaurante

import android.util.Log

object CartManager {

    private const val TAG = "CartManager"
    private val cartItems = mutableListOf<CartItem>()

    fun addItem(dish: Dish) {
        val existingItem = cartItems.find { it.dish.id == dish.id }

        if (existingItem != null) {
            existingItem.quantity++
            Log.d(TAG, "Incrementado: ${dish.name}, Qtd: ${existingItem.quantity}")
        } else {
            cartItems.add(CartItem(dish = dish, quantity = 1))
            Log.d(TAG, "Adicionado: ${dish.name}")
        }
    }

    // --- CORREÇÃO 1: 'removeItem' estava com bug ---
    // Ele deve encontrar o item pelo 'dish.id' e removê-lo da lista
    fun removeItem(dish: Dish) {
        val itemToRemove = cartItems.find { it.dish.id == dish.id }
        if (itemToRemove != null) {
            cartItems.remove(itemToRemove)
            Log.d(TAG, "Removido: ${dish.name}")
        }
    }

    // --- CORREÇÃO 2: Função 'updateQuantity' estava FALTANDO ---
    // (A CartActivity estava chamando isso, mas não existia)
    fun updateQuantity(dish: Dish, newQuantity: Int) {
        val existingItem = cartItems.find { it.dish.id == dish.id }
        existingItem?.let {
            if (newQuantity > 0) {
                it.quantity = newQuantity
                Log.d(TAG, "Quantidade de ${dish.name} atualizada para $newQuantity")
            } else {
                removeItem(dish) // Remove se a quantidade for 0 ou menor
            }
        }
    }

    fun getCartItems(): List<CartItem> {
        return cartItems.toList()
    }

    // --- CORREÇÃO 3: 'getCartTotal' estava com nome errado ('calculateTotalPrice') ---
    fun getCartTotal(): Double {
        return cartItems.sumOf { it.dish.price * it.quantity }
    }

    fun clearCart() {
        cartItems.clear()
        Log.d(TAG, "Carrinho limpo")
    }
}