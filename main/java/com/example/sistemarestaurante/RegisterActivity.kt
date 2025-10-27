package com.example.sistemarestaurante

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sistemarestaurante.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            performRegistration() // RF1
        }

        binding.tvLoginLink.setOnClickListener {
            // Volta para a tela de Login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Finaliza a RegisterActivity
        }
    }

    // --- FUNÇÃO CORRIGIDA ---
    private fun performRegistration() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Validações básicas
        if (name.isEmpty() || email.isEmpty() || password.length < 6 || password != confirmPassword) {
            Toast.makeText(this, "Verifique os dados. Senha precisa ter 6+ caracteres", Toast.LENGTH_LONG).show()
            if (password != confirmPassword) binding.tilConfirmPassword.error = "As senhas não coincidem"
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) binding.tilEmail.error = "Email inválido"
            return
        }

        // Inicia a rotina de cadastro
        lifecycleScope.launch { // Inicia na Main Thread
            // Atualiza a UI antes da chamada de rede
            binding.btnRegister.isEnabled = false
            binding.btnRegister.text = "Cadastrando..."

            // 3. MUDA PARA A THREAD DE I/O (BACKGROUND)
            val result = withContext(Dispatchers.IO) {
                FirebaseManager.registerUser(name, email, password)
            }

            // O código aqui volta automaticamente para a Main Thread

            // 4. Atualiza a UI com o resultado
            if (result.isSuccess) {
                Toast.makeText(this@RegisterActivity, "Cadastro de $name realizado com sucesso! Faça login", Toast.LENGTH_LONG).show()

                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Cadastro falhou
                val errorMessage = result.exceptionOrNull()?.message ?: "Erro desconhecido ao cadastrar"
                Toast.makeText(this@RegisterActivity, "Falha no Cadastro: $errorMessage", Toast.LENGTH_LONG).show()
                binding.btnRegister.isEnabled = true
                binding.btnRegister.text = "Cadastrar"
            }
        }
    }
}