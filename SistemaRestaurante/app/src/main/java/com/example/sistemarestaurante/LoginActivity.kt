package com.example.sistemarestaurante

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sistemarestaurante.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val pass = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnLogin.isEnabled = false

        FirebaseManager.loginUser(email, pass) { result ->
            binding.btnLogin.isEnabled = true

            if (result.isSuccess) {
                val user = result.getOrNull()!! // Sabemos que não é nulo se isSuccess

                // --- SOLUÇÃO 2: Redirecionar para WelcomeActivity ---
                val intent = Intent(this, WelcomeActivity::class.java)
                // Passa o tipo de usuário para a WelcomeActivity saber para onde ir depois
                intent.putExtra("USER_TYPE", user.userType.name)
                startActivity(intent)
                // --------------------------------------------------

                finishAffinity() // Limpa a pilha de login
            } else {
                Toast.makeText(this, "Falha no login: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}