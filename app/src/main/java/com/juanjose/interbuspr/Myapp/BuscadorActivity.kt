package com.juanjose.interbuspr.Myapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore


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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val usernameText: TextView = findViewById(R.id.username_text)
        val searchIcon: ImageView = findViewById(R.id.search_icon)
        val searchInput: EditText = findViewById(R.id.search_input)


        val userId = auth.currentUser?.uid
        userId?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val username = document.getString("username")
                    usernameText.text = username ?: "Usuario"
                }
        }
        searchIcon.setOnClickListener {
            if (searchInput.visibility == View.GONE) {
                searchInput.visibility = View.VISIBLE
            } else {
                searchInput.visibility = View.GONE
            }
        }
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.my_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.uiSettings.isZoomControlsEnabled = true
        gMap.uiSettings.isMyLocationButtonEnabled = true


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            gMap.isMyLocationEnabled = true
            getDeviceLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }


        val paradaLocation = LatLng(40.56512185496001, -3.61007026046637)

        googleMap.addMarker(
            MarkerOptions()
                .position(paradaLocation)
                .title("Parada Linea 152C")
                .snippet("Lineas 152C y L-7")
        )

        getDeviceLocation()

        // Configurar un marcador en una ubicación
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getDeviceLocation()
        } else {
            Toast.makeText(this,"Permiso denegado", Toast.LENGTH_SHORT).show()
        }


    }
    private fun getDeviceLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        gMap.isMyLocationEnabled = true
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    } else {
                        Log.d("LocationTest", "No se pudo obtener la ubicación.")
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }
}