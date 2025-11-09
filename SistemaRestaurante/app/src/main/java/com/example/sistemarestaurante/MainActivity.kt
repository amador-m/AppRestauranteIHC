package com.example.sistemarestaurante

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sistemarestaurante.databinding.ActivityMainBinding 

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCustomer.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("user_type", "customer") 
            startActivity(intent)
        }

        binding.btnEmployee.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("user_type", "employee")
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
