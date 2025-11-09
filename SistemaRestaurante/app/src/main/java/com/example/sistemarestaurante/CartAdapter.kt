package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide 
import com.example.sistemarestaurante.databinding.ItemCartBinding

interface CartItemListener {
    fun onIncreaseClicked(cartItem: CartItem)
    fun onDecreaseClicked(cartItem: CartItem)
    fun onRemoveClicked(cartItem: CartItem)
}

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val listener: CartItemListener 
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

            tvCartItemQuantity.text = cartItem.quantity.toString()

            if (cartItem.dish.imageUrl.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(cartItem.dish.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background) 
                    .into(ivCartItemImage)
            } else {
                ivCartItemImage.setImageResource(R.drawable.ic_launcher_background)
            }

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

    fun updateItems(newItems: List<CartItem>) {
        cartItems = newItems
        notifyDataSetChanged()
    }

}
