package com.example.sistemarestaurante

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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

        val userTypeString = intent.getStringExtra("USER_TYPE")
        val userType = UserType.valueOf(userTypeString ?: UserType.CLIENT.name)

        binding.progressBarWelcome.visibility = View.VISIBLE

        FirebaseManager.getUserDetails { result ->
            if (result.isSuccess) {
                val user = result.getOrNull()
                val welcomeName = user?.username?.takeIf { it.isNotEmpty() }
                    ?: user?.name?.takeIf { it.isNotEmpty() }
                    ?: "Bem-vindo(a)!"

                binding.tvWelcomeMessage.text = "Olá, $welcomeName :)\nEstamos preparando tudo..."
            } else {
                binding.tvWelcomeMessage.text = "Bem-vindo(a)!"
            }

            Handler(Looper.getMainLooper()).postDelayed({
                binding.progressBarWelcome.visibility = View.GONE 
                redirectToDashboard(userType)
            }, 2500)
        }
    }

    private fun redirectToDashboard(userType: UserType) {
        val intent = when (userType) {
            UserType.CLIENT -> Intent(this, HitsActivity::class.java)
            UserType.ADMIN -> Intent(this, AdminActivity::class.java)
            UserType.EMPLOYEE -> Intent(this, EmployeeDashboardActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

}
