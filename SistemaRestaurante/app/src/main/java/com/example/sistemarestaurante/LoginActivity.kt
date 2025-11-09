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
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Entrando..."

        FirebaseManager.loginUser(email, pass) { result ->
            // Reabilita e restaura o texto, não importa se deu certo ou falhou
            binding.btnLogin.isEnabled = true
            binding.btnLogin.text = "Entrar"

            if (result.isSuccess) {
                val user = result.getOrNull()!!

                val intent = Intent(this, WelcomeActivity::class.java)
                intent.putExtra("USER_TYPE", user.userType.name)
                startActivity(intent)
                finishAffinity()
            } else {
                Toast.makeText(this, "Falha no login: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}