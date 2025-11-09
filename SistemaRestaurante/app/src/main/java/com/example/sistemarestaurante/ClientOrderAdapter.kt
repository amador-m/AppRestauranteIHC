package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemarestaurante.databinding.ItemClientOrderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClientOrderAdapter(
    private val orders: MutableList<Order>,
    private val onItemClicked: (Order) -> Unit
) : RecyclerView.Adapter<ClientOrderAdapter.OrderViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())

    inner class OrderViewHolder(val binding: ItemClientOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemClientOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun getItemCount() = orders.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.binding.apply {
            tvOrderId.text = "Pedido realizado em ${dateFormat.format(Date(order.timestamp))}"
            tvOrderTotal.text = String.format("R$ %.2f", order.totalPrice)
            tvOrderStatus.text = "Status: ${order.status.name}"

            // --- LINHA ADICIONADA ---
            tvPaymentMethod.text = "Pagamento: ${order.paymentMethod.ifEmpty { "Não informado" }}"
        }

        holder.itemView.setOnClickListener {
            onItemClicked(order)
        }
    }

    fun updateOrders(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        orders.sortByDescending { it.timestamp }
        notifyDataSetChanged()
    }
}