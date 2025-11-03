package com.example.sistemarestaurante

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sistemarestaurante.databinding.ActivityMainBinding // Importe o binding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura os listeners de clique para os botões
        binding.btnCustomer.setOnClickListener {
            // Por enquanto, cliente vai para a tela de login/cadastro ou direto para o cardápio se já logado
            // Neste MVP, vamos direcionar para a tela de login por agora
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("user_type", "customer") // Passa um extra para indicar o tipo de usuário
            startActivity(intent)
        }

        binding.btnEmployee.setOnClickListener {
            // Funcionário vai para a tela de login, mas com contexto de funcionário
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("user_type", "employee") // Passa um extra para indicar o tipo de usuário
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            // Botão de cadastro
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}