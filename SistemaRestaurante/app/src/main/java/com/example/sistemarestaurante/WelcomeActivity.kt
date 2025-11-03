package com.example.sistemarestaurante

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sistemarestaurante.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pega o TIPO de usuário que o LoginActivity enviou
        val userTypeString = intent.getStringExtra("USER_TYPE")
        val userType = UserType.valueOf(userTypeString ?: UserType.CLIENT.name)

        // 1. Mostra a mensagem de boas-vindas
        FirebaseManager.getUserDetails { result ->
            binding.progressBarWelcome.visibility = View.GONE
            if (result.isSuccess) {
                val user = result.getOrNull()
                // Se tiver username, usa. Se não, usa o 'name'. Se não, usa "Bem-vindo!"
                val welcomeName = user?.username?.takeIf { it.isNotEmpty() }
                    ?: user?.name?.takeIf { it.isNotEmpty() }
                    ?: "Bem-vindo(a)!"

                binding.tvWelcomeMessage.text = "Olá, $welcomeName!\nEstamos preparando tudo..."
            } else {
                binding.tvWelcomeMessage.text = "Bem-vindo(a)!"
            }

            // 2. Aguarda 2.5 segundos e redireciona
            Handler(Looper.getMainLooper()).postDelayed({
                redirectToDashboard(userType)
            }, 2500) // 2.5 segundos
        }
    }

    private fun redirectToDashboard(userType: UserType) {
        val intent = when (userType) {
            UserType.CLIENT -> Intent(this, MenuActivity::class.java)
            UserType.ADMIN -> Intent(this, AdminActivity::class.java)
            UserType.EMPLOYEE -> Intent(this, EmployeeDashboardActivity::class.java)
        }
        startActivity(intent)
        finish() // Fecha esta tela para que o usuário não possa "voltar" para ela
    }
}