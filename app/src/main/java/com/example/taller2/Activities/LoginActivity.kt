package com.example.taller2.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.taller2.R
import com.example.taller2.activity_register // Verifica que el paquete sea correcto

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val layoutRegistro = findViewById<LinearLayout>(R.id.layout_ir_registro)
        layoutRegistro.setOnClickListener {
            val intent = Intent(this, activity_register::class.java)
            startActivity(intent)
        }
    }
}