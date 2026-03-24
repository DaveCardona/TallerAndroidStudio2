package com.example.taller2.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.taller2.R
import com.example.taller2.ui.inicio.InicioActivity


class AventureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_aventure)

        // Delay de 3 segundos y redireccionamiento a inicio

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, InicioActivity::class.java))
            finish()
        }, 3000)
    }

}