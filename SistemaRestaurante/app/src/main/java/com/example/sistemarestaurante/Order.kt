package com.example.sistemarestaurante

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

enum class OrderStatus {
    PENDENTE,
    EM_PREPARO,
    PRONTO,
    ENTREGUE,
    CANCELADO
}

@Parcelize
data class Order(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var orderId: String = "",
    var userId: String = "",
    var items: List<CartItem> = listOf(),
    var totalPrice: Double = 0.0,
    var timestamp: Long = System.currentTimeMillis(),
    var status: OrderStatus = OrderStatus.PENDENTE,
    var shippingFee: Double = 0.0,
    var paymentMethod: String = ""
) : Parcelable {
    constructor() : this("", "", listOf(), 0.0, 0L, OrderStatus.PENDENTE, 0.0, "")
}