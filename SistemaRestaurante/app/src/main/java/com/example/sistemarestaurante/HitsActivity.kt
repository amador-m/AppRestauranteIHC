package com.example.sistemarestaurante

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistemarestaurante.databinding.ActivityHitsBinding

class HitsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHitsBinding
    private lateinit var hitsAdapter: HitsAdapter
    private var currentUserType: UserType = UserType.CLIENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHitsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadData()
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.selectedItemId = R.id.nav_hits
    }

    private fun loadData() {
        FirebaseManager.getUserDetails { result ->
            if (result.isSuccess) {
                currentUserType = result.getOrNull()?.userType ?: UserType.CLIENT
            }
            setupRecyclerView()
            loadHits()
        }
    }

    private fun setupRecyclerView() {
        hitsAdapter = HitsAdapter(mutableListOf()) { dish ->
            if (currentUserType == UserType.CLIENT) {
                CartManager.addItem(dish)
                Toast.makeText(this, "${dish.name} adicionado!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Apenas clientes podem adicionar itens", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvHits.layoutManager = LinearLayoutManager(this)
        binding.rvHits.adapter = hitsAdapter
    }

    private fun loadHits() {
        binding.tvToolbarTitle.text = "Mais Pedidos"

        FirebaseManager.getTopSellingDishes { result ->
            if (result.isSuccess) {
                val hits = result.getOrNull() ?: emptyList()
                if (hits.isEmpty()) {
                    Toast.makeText(this, "Ainda não há itens mais vendidos", Toast.LENGTH_SHORT).show()
                } else {
                    binding.tvToolbarTitle.text = "Mais Pedidos"
                }
                hitsAdapter.updateDishes(hits)
            } else {
                Toast.makeText(this, "Erro ao carregar destaques", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigationView.selectedItemId = R.id.nav_hits
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_hits -> true 
                R.id.nav_menu -> {
                    startActivity(Intent(this, MenuActivity::class.java))
                    true
                }
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
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
