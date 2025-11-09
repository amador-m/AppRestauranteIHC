package com.example.sistemarestaurante

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sistemarestaurante.databinding.ActivityOrderDetailBinding

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var adapter: OrderDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- CORREÇÃO DO BUILD ERROR AQUI ---
        // Troca o 'binding.toolbar' pelo ID correto do ImageButton ('ibBack')
        binding.ibBack.setOnClickListener {
            finish() // Ação de voltar
        }
        // --- FIM DA CORREÇÃO ---

        val order = getOrderFromIntent()

        if (order == null) {
            Toast.makeText(this, "Erro: Pedido não encontrado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupRecyclerView(order)
        bindOrderData(order)
    }

    private fun getOrderFromIntent(): Order? {
        // (Esta função não muda)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("ORDER_DETAILS", Order::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("ORDER_DETAILS")
        }
    }

    private fun setupRecyclerView(order: Order) {
        // (Esta função não muda)
        adapter = OrderDetailAdapter(order.items)
        binding.rvOrderItems.layoutManager = LinearLayoutManager(this)
        binding.rvOrderItems.adapter = adapter
    }

    private fun bindOrderData(order: Order) {
        // (Esta função não muda)
        binding.tvOrderStatus.text = order.status.name
        binding.tvPaymentMethod.text = order.paymentMethod.ifEmpty { "Não informado" }
        binding.tvOrderTotal.text = String.format("R$ %.2f", order.totalPrice)
    }
}