package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemarestaurante.databinding.ItemCartBinding
import com.example.sistemarestaurante.CartItem
import com.example.sistemarestaurante.Dish
class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onRemoveItemClickListener: (CartItem) -> Unit,
    private val onQuantityChangeListener: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            binding.tvCartItemName.text = cartItem.dish.name // <-- 'dish' e 'name'
            binding.tvCartItemPrice.text = "R$ %.2f".format(cartItem.dish.price * cartItem.quantity) // <-- 'dish', 'price', 'quantity'
            binding.tvCartItemQuantity.text = "Qtd: ${cartItem.quantity}" // <-- 'quantity'

            if (cartItem.dish.imageUrl != null && cartItem.dish.imageUrl.isNotEmpty()) { // <-- 'dish', 'imageUrl'
                // ...
            } else {
                binding.ivCartItemImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            binding.btnRemoveFromCart.setOnClickListener {
                onRemoveItemClickListener(cartItem)
            }

            // Para um MVP, não implementaremos os botões de +/- aqui,
            // mas a lógica seria similar ao btnRemoveFromCart e chamaria onQuantityChangeListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    // Métodos para gerenciar a lista de itens
    fun removeItem(cartItem: CartItem) {
        val position = cartItems.indexOf(cartItem)
        if (position != -1) {
            cartItems.removeAt(position)
            notifyItemRemoved(position)
            onQuantityChangeListener() // Notifica a Activity para recalcular o total
        }
    }

    fun getItems(): List<CartItem> = cartItems
}