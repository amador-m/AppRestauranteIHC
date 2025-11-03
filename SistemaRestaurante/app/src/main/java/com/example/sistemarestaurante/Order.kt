package com.example.sistemarestaurante
import com.google.firebase.database.PropertyName // <--- IMPORTE ISSO

enum class OrderStatus {
    PENDENTE,
    EM_PREPARO,
    PRONTO,
    ENTREGUE,
    CANCELADO
}

data class Order(
    @get:PropertyName("id") // <--- ADICIONE ISSO
    @set:PropertyName("id") // <--- ADICIONE ISSO
    var orderId: String = "",

    var userId: String = "",
    val items: List<CartItem> = listOf(),
    val totalPrice: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: OrderStatus = OrderStatus.PENDENTE,

    var shippingFee: Double = 0.0
)