package com.juanjose.interbuspr.Myapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.juanjose.interbuspr.R

class BuscadorActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

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
        val usernameTextView = findViewById<TextView>(R.id.username_input)
        val passwordTextView = findViewById<TextView>(R.id.password_input)

        val username =intent.getStringExtra("username")
        val password = intent.getStringExtra("password")

        usernameTextView.text ="Usuario: $username"
        passwordTextView.text = "Contraseña:$password"



    }
}