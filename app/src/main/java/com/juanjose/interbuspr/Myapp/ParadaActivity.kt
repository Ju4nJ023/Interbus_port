package com.juanjose.interbuspr.Myapp


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.juanjose.interbuspr.R


import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class ParadaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var btnCalcular: Button

    private var start:String =""
    private var end:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parada)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Botón para retroceder
        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.uiSettings.isZoomControlsEnabled = true

        // Inicializar puntos de inicio y destino
        start = "-3.6092340949572033,40.56695700931106 " // Coordenadas de inicio (longitud, latitud)
        end = "-3.6890255117461237,40.46700229089188 "   // Coordenadas de destino (longitud, latitud)

        // Dibujar la ruta automáticamente
        createRoute()
        dibujarParadas()
    }

    // Crear la ruta usando la API de OpenRouteService
    fun createRoute() {
        // Validar que start y end no estén vacíos
        if (start.isEmpty() || end.isEmpty()) {
            Log.e("createRoute", "Puntos de inicio o destino vacíos")
            return
        }

        // Llamada a la API en un hilo de IO
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = getRetrofit().create(apiService::class.java)
                    .getRoute(
                        "5b3ce3597851110001cf62483882c81565344873ab21bf2adf855b6b",
                        start,
                        end
                    )

                if (call.isSuccessful) {
                    call.body()?.let { drawRoute(it) }
                } else {
                    Log.e("createRoute", "Error en la respuesta de la API")
                }
            } catch (e: Exception) {
                Log.e("createRoute", "Error: ${e.message}")
            }
        }
    }

    // Dibujar la ruta en el mapa
    private fun drawRoute(rutaResponse: RutaResponse) {
        val polylineOptions = PolylineOptions()
            .color(Color.BLUE) // Color de la línea
            .width(10f)        // Grosor de la línea

        // Recorrer los puntos de la ruta y agregarlos a las opciones de la polilínea
        rutaResponse.features.firstOrNull()?.geometry?.coordinates?.forEach {
            polylineOptions.add(LatLng(it[1], it[0]))
        }

        // Dibujar la ruta en el mapa en el hilo principal
        runOnUiThread {
            map.addPolyline(polylineOptions)
            Log.i("drawRoute", "Ruta dibujada correctamente")
        }
    }

    private fun cargarParadas(context: Context): RutaResponse {
        val json = context.assets.open("Paradas_152C.json").bufferedReader().use { it.readText() }
        val tipo = object : TypeToken<RutaResponse>() {}.type // Deserializa en RutaResponse, no en List<Parada>
        val rutaResponse: RutaResponse = Gson().fromJson(json, tipo)

        Log.d("cargarParadas", "Paradas cargadas: $rutaResponse")
        return rutaResponse
    }

    // Dibujar las paradas en el mapa
    private fun dibujarParadas() {
        val rutaResponse = cargarParadas(this)

        for (parada in rutaResponse.paradas) { // Accedemos a rutaResponse.paradas
            // Crear un marcador para cada parada
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(parada.lat, parada.lng))
                    .title(parada.nombre)
            )
        }
        Log.i("dibujarParadas", "Paradas añadidas al mapa correctamente")
    }

    // Configurar Retrofit
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}



