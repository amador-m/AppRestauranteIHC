package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistemarestaurante.databinding.ItemOrderDetailBinding

class OrderDetailAdapter(
    private val items: List<CartItem>
) : RecyclerView.Adapter<OrderDetailAdapter.DetailViewHolder>() {

    inner class DetailViewHolder(val binding: ItemOrderDetailBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ItemOrderDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val cartItem = items[position]
        holder.binding.apply {
            tvItemQuantity.text = "${cartItem.quantity}x"
            tvItemName.text = cartItem.dish.name
            tvItemPrice.text = String.format("Unid: R$ %.2f", cartItem.dish.price)

            if (cartItem.dish.imageUrl.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(cartItem.dish.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(ivItemImage)
            } else {
                ivItemImage.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }
    }
}