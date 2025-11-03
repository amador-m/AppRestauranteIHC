package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Importe o Glide
import com.example.sistemarestaurante.databinding.ItemCartBinding

// Interface para comunicar cliques de volta para a Activity
interface CartItemListener {
    fun onIncreaseClicked(cartItem: CartItem)
    fun onDecreaseClicked(cartItem: CartItem)
    fun onRemoveClicked(cartItem: CartItem)
}

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val listener: CartItemListener // Recebe o listener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount() = cartItems.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.binding.apply {
            tvCartItemName.text = cartItem.dish.name
            tvCartItemPrice.text = String.format("R$ %.2f", cartItem.dish.price * cartItem.quantity)

            // (Req 3) Mostra apenas o número da quantidade
            tvCartItemQuantity.text = cartItem.quantity.toString()

            // --- SOLUÇÃO 2: Carregar a Imagem ---
            if (cartItem.dish.imageUrl.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(cartItem.dish.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background) // Imagem padrão
                    .error(R.drawable.ic_launcher_background) // Imagem de erro
                    .into(ivCartItemImage)
            } else {
                // Caso não haja URL, usa a imagem padrão
                ivCartItemImage.setImageResource(R.drawable.ic_launcher_background)
            }

            // --- SOLUÇÃO 3: Configurar os botões ---
            btnIncrease.setOnClickListener {
                listener.onIncreaseClicked(cartItem)
            }
            btnDecrease.setOnClickListener {
                listener.onDecreaseClicked(cartItem)
            }
            btnRemoveFromCart.setOnClickListener {
                listener.onRemoveClicked(cartItem)
            }
        }
    }

    // (Não precisamos mais dos métodos setOn...Listener)

    fun updateItems(newItems: List<CartItem>) {
        cartItems = newItems
        notifyDataSetChanged()
    }
}