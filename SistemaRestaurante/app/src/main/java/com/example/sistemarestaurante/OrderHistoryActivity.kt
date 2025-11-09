package com.example.sistemarestaurante

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistemarestaurante.databinding.ActivityOrderHistoryBinding

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var clientOrderAdapter: ClientOrderAdapter
    private val ordersList = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupRealtimeOrderListener()
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        // Garante que o item correto está selecionado
        binding.bottomNavigationView.selectedItemId = R.id.nav_orders
    }

    private fun setupNavigation() {
        binding.bottomNavigationView.selectedItemId = R.id.nav_orders
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_hits -> {
                    startActivity(Intent(this, HitsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_menu -> {
                    startActivity(Intent(this, MenuActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_orders -> true // Já estamos aqui
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        clientOrderAdapter = ClientOrderAdapter(ordersList) { order ->
            // Inicia a nova tela de Detalhes do Pedido
            val intent = Intent(this, OrderDetailActivity::class.java)
            intent.putExtra("ORDER_DETAILS", order) // Passa o objeto Order
            startActivity(intent)
        }
        binding.rvClientOrders.layoutManager = LinearLayoutManager(this)
        binding.rvClientOrders.adapter = clientOrderAdapter
    }

    // Se inscreve no listener que filtra pelos pedidos do cliente
    private fun setupRealtimeOrderListener() {
        FirebaseManager.addClientOrdersListener { result ->
            if (result.isSuccess) {
                val orders = result.getOrNull() ?: emptyList()
                clientOrderAdapter.updateOrders(orders)
            } else {
                Toast.makeText(this, "Erro ao carregar histórico", Toast.LENGTH_SHORT).show()
                // (Se o erro for "Usuário não logado", redirecionar para Login)
            }
        }
    }

    // Limpa o listener ao fechar a tela
    override fun onDestroy() {
        super.onDestroy()
        FirebaseManager.removeClientOrdersListener()
    }
}