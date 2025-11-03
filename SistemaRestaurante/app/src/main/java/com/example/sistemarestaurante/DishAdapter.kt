package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemarestaurante.databinding.ItemDishBinding
import com.bumptech.glide.Glide

class DishAdapter(
    private var dishes: List<Dish>,
    private val onAddClickListener: (Dish) -> Unit
) : RecyclerView.Adapter<DishAdapter.DishViewHolder>() {

    // ViewHolder simples
    inner class DishViewHolder(val binding: ItemDishBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val binding = ItemDishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DishViewHolder(binding)
    }

    override fun getItemCount(): Int = dishes.size

    // Vincula os dados de um item a um ViewHolder
    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val dish = dishes[position]

        holder.binding.apply {
            tvDishName.text = dish.name
            tvDishDescription.text = dish.description
            tvDishPrice.text = String.format("R$ %.2f", dish.price)

            // --- CORREÇÃO DO AVISO AQUI ---
            if (dish.imageUrl.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(dish.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background) // (Use um placeholder seu)
                    .into(ivDishImage) // (Use o ID da sua ImageView no item_dish.xml)
            } else {
                ivDishImage.setImageResource(R.drawable.ic_launcher_foreground) // (Use um drawable seu)
            }

            // Define o listener para o botão de adicionar
            // (Assumindo que o ID é btnAddDish no item_dish.xml)
            btnAddDish.setOnClickListener {
                onAddClickListener(dish)
            }

            // Exibir/esconder se o prato está disponível (RF16)
            if (!dish.isAvailable) {
                root.alpha = 0.5f
                btnAddDish.isEnabled = false
                btnAddDish.alpha = 0.5f
            } else {
                root.alpha = 1.0f
                btnAddDish.isEnabled = true
                btnAddDish.alpha = 1.0f
            }
        }
    }

    // Método para atualizar a lista de pratos (para busca/filtragem)
    fun updateDishes(newDishes: List<Dish>) {
        dishes = newDishes
        notifyDataSetChanged()
    }
}