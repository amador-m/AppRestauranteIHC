package com.example.sistemarestaurante

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
        setupRealtimeOrderListener() // (RF7, RF8)
    }

    private fun setupRecyclerView() {
        clientOrderAdapter = ClientOrderAdapter(ordersList)
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