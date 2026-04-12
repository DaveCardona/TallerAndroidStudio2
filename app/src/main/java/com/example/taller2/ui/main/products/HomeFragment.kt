package com.example.taller2.ui.main.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taller2.R

class HomeFragment : Fragment() {

    private val listaProductos = listOf(
        Product(nombre = "Camisa Casual", precio = 10.0, imagenRes = R.drawable.camisa),
        Product(nombre = "Pantalón Slim Fit", precio = 15.0, imagenRes = R.drawable.camisa),
        Product(nombre = "Zapatos Deportivos", precio = 20.0, imagenRes = R.drawable.camisa),
        Product(nombre = "Bolso de Viaje", precio = 12.0, imagenRes = R.drawable.camisa),
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_products)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = ProductoAdapter(listaProductos)
        return view
    }

}