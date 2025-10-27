package com.example.sistemarestaurante

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sistemarestaurante.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var userTypeContext: String? = null // Contexto de quem chamou (customer ou employee)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recupera o contexto (se veio de Cliente ou Funcionário)
        userTypeContext = intent.getStringExtra("user_type")

        if (userTypeContext == "employee") {
            binding.tvLoginTitle.text = "Login da Equipe"
            binding.tvLoginSubtitle.text = "Acesso para funcionários e administradores"
        }

        binding.btnLogin.setOnClickListener {
            performLogin() // RF2, RF10
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Funcionalidade de recuperação de senha em desenvolvimento!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Validações básicas
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email não pode ser vazio"
            return
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Senha não pode ser vazia"
            return
        }

        // Inicia a rotina de login
        lifecycleScope.launch {
            // Desabilita o botão e mostra feedback
            binding.btnLogin.isEnabled = false
            binding.btnLogin.text = "Entrando..."

            val result = FirebaseManager.loginUser(email, password)

            if (result.isSuccess) {
                val user = result.getOrNull()
                handleLoginSuccess(user) // RF22
            } else {
                // Login falhou (senha errada, usuário inexistente, etc.)
                val errorMessage = result.exceptionOrNull()?.message ?: "Erro desconhecido"
                Toast.makeText(this@LoginActivity, "Falha no Login: $errorMessage", Toast.LENGTH_LONG).show()

                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = "Entrar"
            }
        }
    }

    private fun handleLoginSuccess(user: User?) {
        user ?: return

        val intent: Intent = when (user.userType) {
            UserType.CLIENT -> {
                // Redireciona para o Cardápio (RF3)
                Toast.makeText(this, "Login de cliente realizado com sucesso!", Toast.LENGTH_SHORT).show()
                Intent(this, MenuActivity::class.java)
            }
            UserType.EMPLOYEE -> {
                // Redireciona para o Painel do Funcionário (RF11)
                Toast.makeText(this, "Login de funcionário realizado com sucesso!", Toast.LENGTH_SHORT).show()
                Intent(this, EmployeeDashboardActivity::class.java)
            }
            UserType.ADMIN -> {
                // Redireciona para o Painel do Administrador (RF14)
                Toast.makeText(this, "Login de administrador realizado com sucesso!", Toast.LENGTH_SHORT).show()
                Intent(this, AdminActivity::class.java)
            }
        }

        startActivity(intent)
        finish() // Finaliza a LoginActivity
    }
}
