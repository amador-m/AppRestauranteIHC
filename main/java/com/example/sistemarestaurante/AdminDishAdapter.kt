package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemarestaurante.databinding.ItemAdminDishBinding // Você precisará criar este layout

class AdminDishAdapter(
    private val dishes: MutableList<Dish>,
    private val onEditClickListener: (Dish) -> Unit,
    private val onDeleteClickListener: (Dish) -> Unit,
    private val onToggleAvailabilityListener: (Dish, Boolean) -> Unit
) : RecyclerView.Adapter<AdminDishAdapter.AdminDishViewHolder>() {

    inner class AdminDishViewHolder(private val binding: ItemAdminDishBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dish: Dish) {
            binding.tvAdminDishName.text = dish.name
            binding.tvAdminDishPrice.text = "R$ %.2f".format(dish.price)
            binding.switchDishAvailability.isChecked = dish.isAvailable

            binding.btnEditDish.setOnClickListener { onEditClickListener(dish) }
            binding.btnDeleteDish.setOnClickListener { onDeleteClickListener(dish) }
            binding.switchDishAvailability.setOnCheckedChangeListener { _, isChecked ->
                onToggleAvailabilityListener(dish, isChecked)
            }

            // Exemplo de imagem (você integraria Glide/Picasso aqui)
            if (dish.imageUrl != null && dish.imageUrl.isNotEmpty()) {
                // Load image
            } else {
                binding.ivAdminDishImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminDishViewHolder {
        val binding = ItemAdminDishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminDishViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminDishViewHolder, position: Int) {
        holder.bind(dishes[position])
    }

    override fun getItemCount(): Int = dishes.size
}