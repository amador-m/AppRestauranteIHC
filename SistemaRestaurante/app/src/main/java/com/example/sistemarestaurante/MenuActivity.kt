package com.example.sistemarestaurante

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistemarestaurante.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var menuAdapter: DishAdapter
    private var currentUserType: UserType = UserType.CLIENT 
    private val fullDishList = mutableListOf<Dish>()

    private var selectedCategory: String = "Todos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userTypeString = intent.getStringExtra("USER_TYPE")
        currentUserType = UserType.valueOf(userTypeString ?: UserType.CLIENT.name)

        if (currentUserType == UserType.EMPLOYEE) {
            binding.tvWelcomeMessage.text = "Cardápio (Visão)"
            setupNavigation()
        } else {
            binding.tvWelcomeMessage.text = "Cardápio"
            setupNavigation()
        }
        
        setupRecyclerView()
        setupSearchView()
        setupCategoryFilterListeners()

        FirebaseManager.addMenuListener { result ->
            if (result.isSuccess) {
                val dishes = result.getOrNull() ?: emptyList()
                fullDishList.clear()
                fullDishList.addAll(dishes)
                filterDishes()
            } else {
                Toast.makeText(this, "Erro ao carregar cardápio", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!intent.getBooleanExtra("IS_GUEST_MODE", false)) {
            binding.bottomNavigationView.selectedItemId = R.id.nav_menu
        }
        filterDishes() 
    }

    private fun setupRecyclerView() {
        menuAdapter = DishAdapter(mutableListOf()) { dish -> 
            val isGuestMode = intent.getBooleanExtra("IS_GUEST_MODE", false)
            if (isGuestMode || currentUserType != UserType.CLIENT) {
                Toast.makeText(this, "Apenas clientes podem adicionar itens", Toast.LENGTH_SHORT).show()
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
                filterDishes()
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterDishes()
                return true
            }
        })
    }

    private fun setupCategoryFilterListeners() {
        binding.btnCategoryAll.setOnClickListener {
            selectedCategory = "Todos"
            filterDishes()
        }
        binding.btnCategoryLanches.setOnClickListener {
            selectedCategory = "Lanches"
            filterDishes()
        }
        binding.btnCategorySalgados.setOnClickListener {
            selectedCategory = "Salgados"
            filterDishes()
        }
        binding.btnCategoryPasteis.setOnClickListener {
            selectedCategory = "Pasteis"
            filterDishes()
        }
        binding.btnCategoryBebidas.setOnClickListener {
            selectedCategory = "Bebidas"
            filterDishes()
        }
        binding.btnCategoryRaspadinhas.setOnClickListener {
            selectedCategory = "Raspadinhas"
            filterDishes()
        }
        binding.btnCategoryMatinais.setOnClickListener {
            selectedCategory = "Matinais"
            filterDishes()
        }
    }

    private fun filterDishes() {
        val query = binding.searchView.query?.toString()
        val availableDishes = fullDishList.filter { it.isAvailable }
        val byCategory = if (selectedCategory == "Todos") {
            availableDishes
        } else {
            availableDishes.filter {
                it.category.equals(selectedCategory, ignoreCase = true)
            }
        }

        val filteredList = if (query.isNullOrEmpty()) {
            byCategory
        } else {
            byCategory.filter {
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

    private fun setupNavigation() {
        binding.bottomNavigationView.menu.clear()

        if (currentUserType == UserType.CLIENT) {
            binding.bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_client)
            binding.bottomNavigationView.selectedItemId = R.id.nav_menu

            binding.bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_hits -> {
                        startActivity(Intent(this, HitsActivity::class.java))
                        true
                    }
                    R.id.nav_menu -> true 
                    R.id.nav_cart -> {
                        startActivity(Intent(this, CartActivity::class.java))
                        true
                    }
                    R.id.nav_orders -> {
                        startActivity(Intent(this, OrderHistoryActivity::class.java))
                        true
                    }
                    R.id.nav_profile -> {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        true
                    }
                    else -> false
                }
            }

        } else if (currentUserType == UserType.EMPLOYEE) {
            binding.bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_employee)
            binding.bottomNavigationView.selectedItemId = R.id.nav_view_menu

            binding.bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_view_orders -> {
                        startActivity(Intent(this, EmployeeDashboardActivity::class.java))
                        true
                    }
                    R.id.nav_view_menu -> true
                    R.id.nav_profile -> {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
        }
    }

}
