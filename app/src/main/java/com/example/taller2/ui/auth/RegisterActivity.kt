package com.example.taller2.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.taller2.R
import com.example.taller2.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import io.github.jan.supabase.auth.providers.builtin.Email




class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var etReContrasena: EditText
    private lateinit var checkTerminos: CheckBox
    private lateinit var btnRegistro: Button
    private lateinit var tvCuenta: TextView

    @SuppressLint("UnsafeOptInUsageError")
    @Serializable
    data class UsuarioData(
        val id: String,
        val nombres: String,
        val apellidos: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etNombres = findViewById(R.id.id_nombre_usuario_registro)
        etApellidos = findViewById(R.id.id_apellidos_usuario_registro)
        etCorreo = findViewById(R.id.id_correo_usuario_registro)
        etContrasena = findViewById(R.id.id_contraseña_usuario_registro)
        etReContrasena = findViewById(R.id.id_conf_contraseña_usuario_registro)
        checkTerminos = findViewById(R.id.id_checkTerminos)
        btnRegistro = findViewById(R.id.id_boton_registrarse)
        tvCuenta = findViewById(R.id.id_ir_a_login)

        //Escuchar el boton de registro
        btnRegistro.setOnClickListener {
            val nombres = etNombres.text.toString()
            val apellidos = etApellidos.text.toString()
            val correo = etCorreo.text.toString()
            val contrasena = etContrasena.text.toString()
            val reContrasena = etReContrasena.text.toString()

            // Validaciones

            if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || reContrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (contrasena.length < 8) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 8 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (contrasena != reContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!checkTerminos.isChecked) {
                Toast.makeText(this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Registro en Supabase

            lifecycleScope.launch {
                try {
                    // Paso 1: Registrar el usuario en Supabase
                    val resultado = SupabaseClient.client.auth.signUpWith(Email){
                        email = correo
                        password = contrasena
                    }

                    // Paso 2: Guardar los datos adicionales
                    val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: ""
                    SupabaseClient.client.postgrest["usuarios"].insert(
                        UsuarioData(
                            id = userId,
                            nombres = nombres,
                            apellidos = apellidos
                        )
                    )

                    // Paso 3: Dirigir al usuario al LoginActivity
                    runOnUiThread {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registro exitoso",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error en el registro: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


            tvCuenta.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        //
            val rootView = findViewById<ViewGroup>(R.id.main)
            ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
                val bottomPadding = maxOf(systemBars.bottom, imeInsets.bottom)
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
                insets
            }


            val btnVolverLogin = findViewById<TextView>(R.id.id_ir_a_login)

            btnVolverLogin.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

                finish()
            }
        }
    }
