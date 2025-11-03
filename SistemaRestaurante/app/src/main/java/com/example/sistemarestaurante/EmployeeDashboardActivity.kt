package com.example.sistemarestaurante

import android.content.Intent // <-- VERIFIQUE SE O IMPORT ESTÁ AQUI
import android.os.Bundle
import android.view.View // <-- VERIFIQUE SE O IMPORT ESTÁ AQUI
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistemarestaurante.databinding.ActivityEmployeeDashboardBinding

class EmployeeDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeDashboardBinding
    private lateinit var orderAdapter: OrderAdapter
    private val allOrdersList = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        setupOrderListener()

        // --- SOLUÇÃO 4: Funcionário Vê Cardápio ---
        binding.btnViewMenu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("IS_GUEST_MODE", true) // Avisa a MenuActivity para esconder o carrinho
            startActivity(intent)
        }
        // --- SOLUÇÃO 1: Perfil do Funcionário ---
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    // (O resto do seu código: setupRecyclerView, setupOrderListener, etc... não muda)
    // ... (copie o resto do seu código original aqui)
    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            mutableListOf(),
            onStatusChanged = { order, newStatus ->
                FirebaseManager.updateOrderStatus(order.orderId, newStatus) { result ->
                    if (result.isSuccess) {
                        Toast.makeText(this, "Status atualizado!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Falha ao atualizar status.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
        binding.rvEmployeeOrders.layoutManager = LinearLayoutManager(this)
        binding.rvEmployeeOrders.adapter = orderAdapter
    }

    private fun setupOrderListener() {
        FirebaseManager.addAllOrdersListener { result ->
            if (result.isSuccess) {
                val orders = result.getOrNull() ?: emptyList()
                allOrdersList.clear()
                allOrdersList.addAll(orders)
                filterAndDisplayOrders()
            } else {
                Toast.makeText(this, "Erro ao carregar pedidos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterAndDisplayOrders() {
        val pendingOrders = allOrdersList.filter {
            it.status != OrderStatus.ENTREGUE && it.status != OrderStatus.CANCELADO
        }.sortedByDescending { it.timestamp }
        orderAdapter.updateOrders(pendingOrders)
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseManager.removeAllOrdersListener()
    }
}