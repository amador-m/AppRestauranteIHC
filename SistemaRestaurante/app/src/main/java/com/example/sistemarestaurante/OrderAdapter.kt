package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemarestaurante.databinding.ItemOrderBinding

class OrderAdapter(
    private val orders: MutableList<Order>,
    private val onStatusChanged: (Order, OrderStatus) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun getItemCount() = orders.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        val context = holder.itemView.context

        holder.binding.apply {
            // --- CORREÇÃO: Usando order.orderId ---
            tvOrderId.text = "Pedido #${order.orderId.takeLast(6)}"
            // --- FIM DA CORREÇÃO ---
            tvOrderTotal.text = String.format("R$ %.2f", order.totalPrice)

            // Configurar o Spinner
            val statusOptions = OrderStatus.values().map { it.name }
            val spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, statusOptions)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOrderStatus.adapter = spinnerAdapter

            // Definir a seleção atual do Spinner
            val currentStatusIndex = OrderStatus.values().indexOf(order.status)
            spinnerOrderStatus.setSelection(currentStatusIndex, false) // false para não disparar o listener

            // Listener para quando o funcionário seleciona um novo status
            spinnerOrderStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    val newStatus = OrderStatus.values()[pos]
                    // Só chama a atualização se o status for realmente diferente
                    if (newStatus != order.status) {
                        onStatusChanged(order, newStatus)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    fun updateOrders(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }
}