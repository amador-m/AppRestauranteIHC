package com.example.sistemarestaurante

import android.content.Intent
// IMPORTAÇÃO CORRETA PARA A NOVA ANIMAÇÃO
import android.view.animation.AnimationUtils
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.sistemarestaurante.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    // O delay deve ser o mesmo (ou um pouco maior) que a duração da animação
    private val SPLASH_DELAY: Long = 2100 // 2.1 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Carrega a animação de "zoom/fade" do res/anim
        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation)

        // 2. Aplica a animação no CardView da logo (para o card e a sombra crescerem)
        binding.cvLogo.startAnimation(splashAnimation)

        // 3. O Handler agora só espera a animação terminar
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }
}