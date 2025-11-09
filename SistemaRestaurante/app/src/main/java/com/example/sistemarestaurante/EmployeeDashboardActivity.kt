package com.example.sistemarestaurante

import android.content.Intent 
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistemarestaurante.databinding.ActivityEmployeeDashboardBinding

class EmployeeDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeDashboardBinding
    private lateinit var orderAdapter: OrderAdapter
    private var currentUserType: UserType = UserType.EMPLOYEE 
    private val allOrdersList = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        setupOrderListener()
        setupNavigation()
    }

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
    private fun setupNavigation() {
        binding.bottomNavigationView.menu.clear()

        if (currentUserType == UserType.ADMIN) {
            binding.bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_admin)
            binding.bottomNavigationView.selectedItemId = R.id.nav_manage_orders
        } else {
            binding.bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_employee)
            binding.bottomNavigationView.selectedItemId = R.id.nav_view_orders
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_manage_menu -> {
                    startActivity(Intent(this, AdminActivity::class.java))
                    true
                }
                R.id.nav_manage_orders -> true 

                R.id.nav_view_orders -> true 
                R.id.nav_view_menu -> {
                    val intent = Intent(this, MenuActivity::class.java)
                    intent.putExtra("USER_TYPE", currentUserType.name)
                    startActivity(intent)
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

}
