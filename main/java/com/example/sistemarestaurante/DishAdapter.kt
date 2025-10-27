package com.example.sistemarestaurante

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemarestaurante.databinding.ItemDishBinding // Importe o binding
import kotlin.text.format

class DishAdapter(
    private var dishes: List<Dish>,
    private val onAddClickListener: (Dish) -> Unit // Callback para o clique do botão "Adicionar"
) : RecyclerView.Adapter<DishAdapter.DishViewHolder>() {

    // ViewHolder: Mapeia as views do layout item_dish.xml para variáveis
    inner class DishViewHolder(private val binding: ItemDishBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dish: Dish) {
            binding.tvDishName.text = dish.name
            binding.tvDishDescription.text = dish.description
            binding.tvDishPrice.text = "R$ %.2f".format(dish.price) // Formata o preço

            // Carregar imagem (se houver). Em um projeto real, você usaria uma lib como Glide ou Picasso
            // Por enquanto, vamos ignorar a imagem ou usar um placeholder
            if (dish.imageUrl != null && dish.imageUrl.isNotEmpty()) {
                // Aqui você integraria uma biblioteca de carregamento de imagens
                // Ex: Glide.with(binding.ivDishImage.context).load(dish.imageUrl).into(binding.ivDishImage)
            } else {
                binding.ivDishImage.setImageResource(android.R.drawable.ic_menu_gallery)
            // Aqui pode continuar usando o R do Android
            }

            // Define o listener para o botão de adicionar
            binding.btnAddDish.setOnClickListener {
                onAddClickListener(dish)
            }

            // Exibir/esconder se o prato está disponível (RF16)
            if (!dish.isAvailable) {
                // Opcional: Escurecer o item, desabilitar botão, adicionar texto "Indisponível"
                binding.root.alpha = 0.5f // Torna o item semi-transparente
                binding.btnAddDish.isEnabled = false // Desabilita o botão
                binding.btnAddDish.alpha = 0.5f
                // Poderia adicionar um TextView indicando "Indisponível"
            } else {
                binding.root.alpha = 1.0f
                binding.btnAddDish.isEnabled = true
                binding.btnAddDish.alpha = 1.0f
            }
        }
    }

    // Cria e retorna um ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val binding = ItemDishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DishViewHolder(binding)
    }

    // Vincula os dados de um item a um ViewHolder
    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val dish = dishes[position]
        holder.bind(dish)
    }

    // Retorna o número total de itens na lista
    override fun getItemCount(): Int = dishes.size

    // Metodo para atualizar a lista de pratos e notificar o adaptador (para busca/filtragem)
    fun updateDishes(newDishes: List<Dish>) {
        dishes = newDishes
        notifyDataSetChanged() // Notifica o RecyclerView que os dados mudaram
    }
}