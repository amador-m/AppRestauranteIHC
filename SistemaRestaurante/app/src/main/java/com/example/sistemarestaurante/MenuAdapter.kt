package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast // Importe o Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sistemarestaurante.databinding.ItemDishBinding

class MenuAdapter(
    private val dishes: MutableList<Dish>,
    private val onItemClicked: (Dish) -> Unit // Lambda para lidar com o clique (Adicionar ao carrinho)
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(val binding: ItemDishBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemDishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun getItemCount() = dishes.size

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val dish = dishes[position]

        holder.binding.apply {
            // Preenche os dados do prato no layout
            tvDishName.text = dish.name
            tvDishDescription.text = dish.description
            tvDishPrice.text = String.format("R$ %.2f", dish.price)

            // Carrega a imagem do prato
            if (dish.imageUrl.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(dish.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background) // Imagem padrão
                    .into(ivDishImage)
            }

            // Configura o clique no item (na caixa)
            holder.itemView.setOnClickListener {
                onItemClicked(dish)
            }

            // Adiciona o listener para o botão de "+"
            btnAddDish.setOnClickListener {
                onItemClicked(dish)
                // Adiciona um feedback visual
                Toast.makeText(holder.itemView.context, "${dish.name} adicionado!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Atualiza a lista de pratos e notifica o adapter
    fun updateDishes(newDishes: List<Dish>) {
        dishes.clear()
        dishes.addAll(newDishes)
        notifyDataSetChanged()
    }
}