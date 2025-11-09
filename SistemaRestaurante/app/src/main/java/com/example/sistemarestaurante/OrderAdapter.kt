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
        // Criamos um "Callback" para o DiffUtil entender o que mudou
        val diffCallback = OrderDiffCallback(this.orders, newOrders)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        // Atualizamos a lista interna
        this.orders.clear()
        this.orders.addAll(newOrders)

        // Em vez de notifyDataSetChanged(), usamos o resultado do DiffUtil
        // Isso é muito mais eficiente e não quebra a UI do Spinner!
        diffResult.dispatchUpdatesTo(this)
    }

    // 3. ADICIONE ESTA CLASSE INTERNA (ou pode ser um arquivo separado)
    // Ela ensina o DiffUtil a comparar seus pedidos
    class OrderDiffCallback(
        private val oldList: List<Order>,
        private val newList: List<Order>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        // Compara se o item é o MESMO (pelo ID)
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].orderId == newList[newItemPosition].orderId
        }

        // Compara se o CONTEÚDO do item mudou (ex: o status)
        // (Para isso funcionar bem, sua classe "Order" deve ser um "data class")
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}