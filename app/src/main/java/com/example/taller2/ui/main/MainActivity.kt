package com.example.taller2.ui.main

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.taller2.R
import com.example.taller2.ui.main.perfil.PerfilFragment
import com.example.taller2.ui.main.products.CatalogoFragment
import com.example.taller2.ui.main.products.HomeFragment
import com.example.taller2.ui.main.products.FavoritosFragment
import com.example.taller2.ui.main.products.BuscarFragment
import com.example.taller2.ui.main.products.MensajesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // El ID en activity_main.xml es drawwer_layout
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawwer_layout)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        val navView: NavigationView = findViewById(R.id.nav_view)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        cargarFragment(HomeFragment())
        bottomNav.selectedItemId = R.id.nav_home

        // Configuración de navegación inferior
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> cargarFragment(HomeFragment())
                R.id.nav_catalogo -> cargarFragment(CatalogoFragment())
                R.id.nav_search -> cargarFragment(BuscarFragment())
                R.id.nav_profile -> cargarFragment(PerfilFragment())
                R.id.nav_close -> finish()
            }
            true
        }


        // Configuración de navegación lateral
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> cargarFragment(HomeFragment())
                R.id.nav_search -> cargarFragment(BuscarFragment())
                R.id.nav_favorites -> cargarFragment(FavoritosFragment())
                R.id.nav_messages-> cargarFragment(MensajesFragment())
                R.id.nav_profile -> cargarFragment(PerfilFragment())
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

    }

    private fun cargarFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


}
