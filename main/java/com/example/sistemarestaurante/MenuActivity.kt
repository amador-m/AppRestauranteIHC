package com.example.sistemarestaurante

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistemarestaurante.databinding.ActivityMenuBinding // Importe o binding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var dishAdapter: DishAdapter
    private val allDishes = mutableListOf<Dish>() // Lista de todos os pratos
    private val cartItems = mutableListOf<Dish>() // Carrinho de compras

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMockDishes() // Preenche com dados de exemplo
        setupRecyclerView()
        setupSearchView()
        setupCategoryFilters()

        binding.fabCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            // Passa os itens do carrinho para a CartActivity
            // Você pode usar Parcelable ou Serializable para objetos complexos
            // Para este MVP, passaremos um arraylist de Nomes (simplificado) ou IDs
            // Idealmente, passaria o objeto Dish ou seus IDs e carregaria na CartActivity
            intent.putStringArrayListExtra("cart_items_names", ArrayList(cartItems.map { it.name }))
            startActivity(intent)
        }
    }

    private fun setupMockDishes() {
        // Exemplo de pratos (RF3)
        allDishes.add(Dish("1", "Pizza Margherita", "Molho de tomate, mussarela e manjericão fresco.", 45.00, "Principal"))
        allDishes.add(Dish("2", "Salada Caesar", "Alface americana, croutons, parmesão e molho caesar.", 32.50, "Entrada"))
        allDishes.add(Dish("3", "Cheeseburger Clássico", "Pão brioche, hambúrguer de 180g, queijo cheddar, alface, tomate e picles.", 38.90, "Principal", isAvailable = true))
        allDishes.add(Dish("4", "Brownie com Sorvete", "Brownie de chocolate quentinho com uma bola de sorvete de creme.", 22.00, "Sobremesa"))
        allDishes.add(Dish("5", "Coca-Cola", "Lata 350ml", 8.00, "Bebida", isAvailable = false)) // Exemplo indisponível
        allDishes.add(Dish("6", "Suco de Laranja Natural", "Preparado na hora com laranjas frescas.", 12.00, "Bebida"))
        allDishes.add(Dish("7", "Lasanha à Bolonhesa", "Camadas de massa, molho à bolonhesa, presunto e queijo.", 55.00, "Principal"))
        allDishes.add(Dish("8", "Tiramisu", "Sobremesa italiana com café, queijo mascarpone e cacau.", 28.00, "Sobremesa"))
    }

    private fun setupRecyclerView() {
        dishAdapter = DishAdapter(allDishes) { dish ->
            // Callback para quando o botão "Adicionar" é clicado em um item do RecyclerView (RF5)
            if (dish.isAvailable) {
                cartItems.add(dish)
                Toast.makeText(this, "${dish.name} adicionado ao carrinho!", Toast.LENGTH_SHORT).show()
                // Poderíamos atualizar um contador no FAB do carrinho aqui
            } else {
                Toast.makeText(this, "${dish.name} está indisponível no momento.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvMenu.layoutManager = LinearLayoutManager(this)
        binding.rvMenu.adapter = dishAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterDishes(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterDishes(newText)
                return true
            }
        })
    }

    private fun setupCategoryFilters() {
        // Mapeia os botões para suas categorias
        val categoryButtons = mapOf(
            binding.btnCategoryAll to "Todos",
            binding.btnCategoryMain to "Principal",
            binding.btnCategoryDessert to "Sobremesa",
            binding.btnCategoryDrinks to "Bebida"
            // Adicione mais botões e categorias conforme necessário
        )

        categoryButtons.forEach { (button, category) ->
            button.setOnClickListener {
                if (category == "Todos") {
                    dishAdapter.updateDishes(allDishes)
                } else {
                    filterDishesByCategory(category)
                }
                // Opcional: Mudar a aparência do botão selecionado
                updateButtonSelection(it as Button, categoryButtons.keys.toList())
            }
        }
        // Seleciona o botão "Todos" por padrão ao iniciar
        (categoryButtons.keys.first() as? Button)?.let {
            updateButtonSelection(it, categoryButtons.keys.toList())
        }
    }

    private fun filterDishes(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            allDishes // Se a busca está vazia, mostra todos os pratos
        } else {
            allDishes.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
            }
        }
        dishAdapter.updateDishes(filteredList)
    }

    private fun filterDishesByCategory(category: String) {
        val filteredList = allDishes.filter { it.category == category }
        dishAdapter.updateDishes(filteredList)
    }

    private fun updateButtonSelection(selectedButton: Button, allButtons: List<Button>) {
        allButtons.forEach { button ->
            if (button == selectedButton) {
                button.setBackgroundResource(com.example.sistemarestaurante.R.drawable.rounded_button_filled) // Use o R do seu projeto
                button.setTextColor(
                    resources.getColor(
                        android.R.color.white,
                        theme
                    )
                ) // white é do android padrão
            } else {
                button.setBackgroundResource(com.example.sistemarestaurante.R.drawable.rounded_button_outline) // Use o R do seu projeto
                button.setTextColor(
                    resources.getColor(
                        com.example.sistemarestaurante.R.color.purple_500,
                        theme
                    )
                ) // Use o R do seu projeto
            }
        }
                // Você precisará criar esses drawables:
        // rounded_button_filled.xml e rounded_button_outline.xml em res/drawable
    }
}