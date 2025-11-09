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

    // --- MUDANÇA 1: Adicionar esta variável ---
    // Precisamos saber quem é o usuário. O padrão é CLIENTE.
    private var currentUserType: UserType = UserType.CLIENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHitsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- MUDANÇA 2: Chamar a nova função ---
        // Vamos primeiro buscar os dados do usuário e SÓ DEPOIS carregar o resto.
        loadData()
        // As linhas setupRecyclerView() e loadHits() foram MOVIDAS daqui.

        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.selectedItemId = R.id.nav_hits
    }

    // --- MUDANÇA 3: Nova função ---
    // Esta função busca o tipo de usuário antes de carregar a lista.
    private fun loadData() {
        FirebaseManager.getUserDetails { result ->
            if (result.isSuccess) {
                // Atualiza o tipo de usuário com o que veio do Firebase
                currentUserType = result.getOrNull()?.userType ?: UserType.CLIENT
            }
            // Agora que sabemos quem é o usuário, podemos carregar a tela
            setupRecyclerView()
            loadHits()
        }
    }

    // --- MUDANÇA 4: setupRecyclerView ATUALIZADO ---
    private fun setupRecyclerView() {
        hitsAdapter = HitsAdapter(mutableListOf()) { dish ->
            // A lógica de clique agora é inteligente!
            if (currentUserType == UserType.CLIENT) {
                CartManager.addItem(dish)
                // O Toast que estava na Activity foi mantido.
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
                R.id.nav_hits -> true // Já estamos aqui
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
        // (Não é mais necessário remover o listener, pois usamos addListenerForSingleValueEvent)
    }
}