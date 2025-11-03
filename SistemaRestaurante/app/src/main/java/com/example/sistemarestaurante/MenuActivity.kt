package com.example.sistemarestaurante

import android.content.Intent
import android.os.Bundle
import android.view.View // <-- VERIFIQUE SE O IMPORT ESTÁ AQUI
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistemarestaurante.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var menuAdapter: MenuAdapter
    private val dishList = mutableListOf<Dish>()
    private val fullDishList = mutableListOf<Dish>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // (Removido o loadWelcomeMessage daqui - Req 2)

        // --- SOLUÇÃO 4: Modo "Convidado" (Funcionário/Admin vendo o menu) ---
        val isGuestMode = intent.getBooleanExtra("IS_GUEST_MODE", false)
        if (isGuestMode) {
            // Esconde o carrinho e o histórico de pedidos
            binding.fabCart.visibility = View.GONE
            binding.btnOrderHistory.visibility = View.GONE
            // Muda o título para "Visualizar Cardápio"
            binding.tvMenuTitle.text = "Visualizar Cardápio"
        }
        // ------------------------------------------------------------------

        setupRecyclerView() // (Chamada movida para ANTES de addMenuListener)
        setupSearchView()

        FirebaseManager.addMenuListener { result ->
            if (result.isSuccess) {
                val dishes = result.getOrNull() ?: emptyList()
                fullDishList.clear()
                fullDishList.addAll(dishes)
                filterDishes(null)
            } else {
                Toast.makeText(this, "Erro ao carregar cardápio.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.fabCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        binding.btnOrderHistory.setOnClickListener {
            val intent = Intent(this, OrderHistoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        filterDishes(binding.searchView.query?.toString())
    }

    private fun setupRecyclerView() {
        menuAdapter = MenuAdapter(dishList) { dish ->
            // (Req 4) Verifica se está em "modo convidado" antes de add ao carrinho
            val isGuestMode = intent.getBooleanExtra("IS_GUEST_MODE", false)
            if (isGuestMode) {
                Toast.makeText(this, "Apenas clientes podem adicionar itens.", Toast.LENGTH_SHORT).show()
            } else {
                CartManager.addItem(dish)
                Toast.makeText(this, "${dish.name} adicionado ao carrinho", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvMenu.layoutManager = LinearLayoutManager(this)
        binding.rvMenu.adapter = menuAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterDishes(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterDishes(newText)
                return true
            }
        })
    }

    private fun filterDishes(query: String?) {
        val availableDishes = fullDishList.filter { it.isAvailable }

        val filteredList = if (query.isNullOrEmpty()) {
            availableDishes
        } else {
            availableDishes.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }
        menuAdapter.updateDishes(filteredList)
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseManager.removeMenuListener()
    }
}