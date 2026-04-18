package com.example.taller2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.example.taller2.R
import com.example.taller2.SupabaseClient
import com.example.taller2.data.CredencialeslManager
import com.example.taller2.ui.main.MainActivity
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.launch
import kotlin.jvm.java




class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_login)

        // INICIALIZAR tvIngresarconHuella (esto faltaba)
        tvIngresarconHuella = findViewById(R.id.id_in_huella)




        // Manejo del teclado para Android 15/16
        val rootView = findViewById<android.view.ViewGroup>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = maxOf(systemBars.bottom, imeInsets.bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right,
                bottomPadding)
            insets
        }
        // Listeners de los botones

        findViewById<android.widget.Button>(R.id.id_boton_ingresar)
            .setOnClickListener { iniciarSesion() }
        findViewById<android.widget.TextView>(R.id.id_registrate_login)
            .setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        findViewById<android.widget.TextView>(R.id.id_recupera_contraseña)
            .setOnClickListener {
                Toast.makeText(this, "Proximamente", Toast.LENGTH_SHORT).show()
            }
        findViewById<android.widget.Button>(R.id.btn_IngresoGoogle)
            .setOnClickListener { iniciarSesionConGoogle() }

        tvIngresarconHuella.setOnClickListener { mostrarDialogHuella() }
    }

    private lateinit var tvIngresarconHuella : TextView

    override fun onResume() {
        super.onResume()
        configurarVisibilidadHuella()

    }

    private fun configurarVisibilidadHuella(){
        // Verificar si hay credenciales guardadas localmente
        val huellaActiva = CredencialeslManager.huellaActiva(this)

        // Verificar si el disppsitivo tiene sensor de huella

        val biometricManager = BiometricManager.from(this)
        val huellaDisponible = biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
        tvIngresarconHuella.visibility = if (huellaActiva && huellaDisponible) View.VISIBLE else View.GONE

    }
    private fun iniciarSesion() {
        val correo = findViewById<android.widget.EditText>(R.id.id_nombre_usuario_login)
            .text.toString().trim()
        val contrasena = findViewById<android.widget.EditText>(R.id.id_contraseña_usuario_login)
            .text.toString()

        // Validaciones locales
        if (correo.isEmpty() || contrasena.isEmpty()) {

            Toast.makeText(this, "Por favor completa todos los campos",
                Toast.LENGTH_SHORT).show()
            return
        }
        if (contrasena.length < 6) {
            Toast.makeText(this, "La contrasena debe tener minimo 6 caracteres",
                Toast.LENGTH_SHORT).show()
            return
        }
        // Llamada a Supabase Auth
        lifecycleScope.launch {
            try {
                SupabaseClient.client.auth.signInWith(Email) {
                    email = correo
                    password = contrasena
                }
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finishAffinity()
            } catch (e: Exception) {
                val mensaje = when {
                    e.message?.contains("Invalid login credentials") == true ->
                        "Correo o contrasena incorrectos"
                    else -> "Error al iniciar sesion: ${e.message}"
                }
                Toast.makeText(this@LoginActivity, mensaje,
                    Toast.LENGTH_LONG).show()
            }
        }
        CredencialeslManager.guardarCredenciales(this, correo, contrasena,false)
    }

    private fun iniciarSesionConGoogle() {
        lifecycleScope.launch {
            try {
                // 1. Configurar la solicitud de Google
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("458604528897-1ringdjaod0ddjbsfdl30f1jvs4fjv0m.apps.googleusercontent.com")
                    .setAutoSelectEnabled(false)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                // 2. Mostrar el selector de cuentas de Google
                val credentialManager =
                    CredentialManager.create(this@LoginActivity)
                val result = credentialManager.getCredential(
                    this@LoginActivity, request
                )
                // 3. Obtener el token de Google
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(result.credential.data)
                // 4. Enviar el token a Supabase
                SupabaseClient.client.auth.signInWith(IDToken) {
                    idToken = googleIdTokenCredential.idToken
                    provider = Google
                }

                irAPantallaPrincipal()

            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Error al iniciar con Google: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun irAPantallaPrincipal(){
        Toast.makeText(this, "Welcome :)", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finishAffinity()

    }

    private fun mostrarDialogHuella(){
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                val correo = CredencialeslManager.obtenerCorreo(this@LoginActivity)
                val contrasena = CredencialeslManager.obtenerContrasena(this@LoginActivity)
                if (correo != null && contrasena != null) {
                    lifecycleScope.launch {
                        try {
                            SupabaseClient.client.auth.signInWith(Email) {
                                email = correo
                                password = contrasena
                            }
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finishAffinity()
                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Error al iniciar sesion: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                } else {
                    //No se ha logueado previamente
                    Toast.makeText(this@LoginActivity,"Inicia sesión con Email y Correo",Toast.LENGTH_SHORT).show()
                    CredencialeslManager.limpiarCredenciales(this@LoginActivity)
                }
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                    errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                ) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error de autenticacion biométrico: $errString",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(this@LoginActivity,"Autenticacion biometrica fallida",Toast.LENGTH_SHORT).show()
            }
        })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso con huella digital")
            .setSubtitle("Autenticate con tu huella digital")
            .setNegativeButtonText("Cancelar")
            .build()
        biometricPrompt.authenticate(promptInfo)

    }
}









