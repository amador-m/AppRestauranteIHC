package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemarestaurante.databinding.ItemAdminDishBinding
import com.bumptech.glide.Glide

class AdminDishAdapter(
    private val dishes: MutableList<Dish>,
    private val onEditClickListener: (Dish) -> Unit,
    private val onDeleteClickListener: (Dish) -> Unit,
    private val onToggleAvailabilityListener: (Dish, Boolean) -> Unit
) : RecyclerView.Adapter<AdminDishAdapter.AdminDishViewHolder>() {

    inner class AdminDishViewHolder(val binding: ItemAdminDishBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminDishViewHolder {
        val binding = ItemAdminDishBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdminDishViewHolder(binding)
    }

    override fun getItemCount(): Int = dishes.size

    override fun onBindViewHolder(holder: AdminDishViewHolder, position: Int) {
        val dish = dishes[position]

        holder.binding.apply {
            tvAdminDishName.text = dish.name
            tvAdminDishPrice.text = String.format("R$ %.2f", dish.price)

            switchDishAvailability.isChecked = dish.isAvailable

            if (dish.imageUrl.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(dish.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(ivAdminDishImage) 
            } else {
                ivAdminDishImage.setImageResource(R.drawable.ic_launcher_foreground) 
            }

            btnEditDish.setOnClickListener { onEditClickListener(dish) }
            btnDeleteDish.setOnClickListener { onDeleteClickListener(dish) }

            switchDishAvailability.setOnCheckedChangeListener { _, isChecked ->
                if (switchDishAvailability.isPressed) {
                    onToggleAvailabilityListener(dish, isChecked)
                }
            }
        }
    }

}
