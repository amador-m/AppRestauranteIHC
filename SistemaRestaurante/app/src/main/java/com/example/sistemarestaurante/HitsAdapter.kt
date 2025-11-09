package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistemarestaurante.databinding.ItemHitBinding

class HitsAdapter(
    private val dishes: MutableList<Dish>,
    private val onItemClicked: (Dish) -> Unit
) : RecyclerView.Adapter<HitsAdapter.HitsViewHolder>() {

    inner class HitsViewHolder(val binding: ItemHitBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HitsViewHolder {
        val binding = ItemHitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HitsViewHolder(binding)
    }

    override fun getItemCount() = dishes.size

    override fun onBindViewHolder(holder: HitsViewHolder, position: Int) {
        val dish = dishes[position]
        val context = holder.itemView.context

        holder.binding.apply {
            tvRank.text = "Top ${position + 1} Venda"
            tvDishName.text = dish.name
            tvDishDescription.text = dish.description
            tvDishPrice.text = String.format("R$ %.2f", dish.price)

            if (dish.imageUrl.isNotEmpty()) {
                Glide.with(context)
                    .load(dish.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(ivDishImage)
            } else {
                ivDishImage.setImageResource(R.drawable.ic_launcher_foreground)
            }

            // Clique no item inteiro
            holder.itemView.setOnClickListener {
                onItemClicked(dish)
            }

            // Clique no botão "+"
            btnAddDish.setOnClickListener {
                onItemClicked(dish)
            }
        }
    }

    fun updateDishes(newDishes: List<Dish>) {
        dishes.clear()
        dishes.addAll(newDishes)
        notifyDataSetChanged()
    }
}