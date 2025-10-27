package com.example.sistemarestaurante

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sistemarestaurante.databinding.ActivityEmployeeDashboardBinding

class EmployeeDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aqui você implementaria a lógica para o dashboard do funcionário
        // RF10 (login/logout), RF11 (visualizar pedidos), RF12 (atualizar status), RF13 (detalhes do pedido)
        // Para o MVP, pode ser apenas uma mensagem de boas-vindas.
    }
}