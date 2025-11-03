package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemarestaurante.databinding.ItemClientOrderBinding // Importe o binding correto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClientOrderAdapter(
    private val orders: MutableList<Order>
) : RecyclerView.Adapter<ClientOrderAdapter.OrderViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())

    // Garante que o binding seja público para o onBindViewHolder
    inner class OrderViewHolder(val binding: ItemClientOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemClientOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun getItemCount() = orders.size
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.binding.apply {
            // Converte o timestamp (Long) para uma data legível
            tvOrderId.text = "Pedido realizado em ${dateFormat.format(Date(order.timestamp))}"
            tvOrderTotal.text = String.format("R$ %.2f", order.totalPrice)
            // (RF7) Mostra o status atual
            tvOrderStatus.text = "Status: ${order.status.name}"
        }
    }

    // Atualiza a lista de pedidos no adapter
    fun updateOrders(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        // Ordena para mostrar os mais novos primeiro
        orders.sortByDescending { it.timestamp }
        notifyDataSetChanged()
    }
}