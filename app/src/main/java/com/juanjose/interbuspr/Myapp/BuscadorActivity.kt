package com.juanjose.interbuspr.Myapp

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.juanjose.interbuspr.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class BuscadorActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap

    lateinit var auth: FirebaseAuth
    lateinit var db : FirebaseFirestore


    override fun onStart() {
        super.onStart()

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance()

        // Check if the user is authenticated
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // If the user is not logged in, redirect to the login screen (InterBusApp)
            val intent = Intent(this, InterBusApp::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buscador)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val usernameText: TextView = findViewById(R.id.username_text)
        val searchIcon: ImageView = findViewById(R.id.search_icon)
        val searchInput: EditText = findViewById(R.id.search_input)


        val userId =auth.currentUser?.uid
        userId?.let { uid->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val username = document.getString("username")
                    usernameText.text= username?: "Usuario"
                }
        }
        searchIcon.setOnClickListener{
            if (searchInput.visibility == View.GONE) {
                searchInput.visibility = View.VISIBLE
            }else {
                searchInput.visibility = View.GONE
            }
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.my_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        // Configurar un marcador en una ubicación
        val location = LatLng(-34.0, 151.0) // Latitud y longitud de ejemplo
        gMap.addMarker(MarkerOptions().position(location).title("Marcador de ejemplo"))
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
    }

    }
