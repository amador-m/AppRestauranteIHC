package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
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
            tvOrderId.text = "Pedido #${order.orderId.takeLast(6)}"
            tvOrderTotal.text = String.format("R$ %.2f", order.totalPrice)

            val statusOptions = OrderStatus.values().map { it.name }
            val spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, statusOptions)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOrderStatus.adapter = spinnerAdapter

            val currentStatusIndex = OrderStatus.values().indexOf(order.status)
            spinnerOrderStatus.setSelection(currentStatusIndex, false)
            
            spinnerOrderStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    val newStatus = OrderStatus.values()[pos]
                    if (newStatus != order.status) {
                        onStatusChanged(order, newStatus)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    fun updateOrders(newOrders: List<Order>) {
        val diffCallback = OrderDiffCallback(this.orders, newOrders)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.orders.clear()
        this.orders.addAll(newOrders)

        diffResult.dispatchUpdatesTo(this)
    }

    class OrderDiffCallback(
        private val oldList: List<Order>,
        private val newList: List<Order>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].orderId == newList[newItemPosition].orderId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

}
