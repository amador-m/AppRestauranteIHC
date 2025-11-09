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

    fun removeItem(dish: Dish) {
        val itemToRemove = cartItems.find { it.dish.id == dish.id }
        if (itemToRemove != null) {
            cartItems.remove(itemToRemove)
            Log.d(TAG, "Removido: ${dish.name}")
        }
    }

    fun updateQuantity(dish: Dish, newQuantity: Int) {
        val existingItem = cartItems.find { it.dish.id == dish.id }
        existingItem?.let {
            if (newQuantity > 0) {
                it.quantity = newQuantity
                Log.d(TAG, "Quantidade de ${dish.name} atualizada para $newQuantity")
            } else {
                removeItem(dish) 
            }
        }
    }

    fun getCartItems(): List<CartItem> {
        return cartItems.toList()
    }

    fun getCartTotal(): Double {
        return cartItems.sumOf { it.dish.price * it.quantity }
    }

    fun clearCart() {
        cartItems.clear()
        Log.d(TAG, "Carrinho limpo")
    }

}
