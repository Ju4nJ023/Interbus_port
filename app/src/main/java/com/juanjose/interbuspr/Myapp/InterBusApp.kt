package com.juanjose.interbuspr.Myapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.juanjose.interbuspr.R
import com.juanjose.interbuspr.R.id.password_input
import com.juanjose.interbuspr.R.id.username_input
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InterBusApp : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var usernameInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginBtn: Button
    lateinit var crearBtn: Button
    lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inter_bus_app)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()  // Initialize Firestore

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        crearBtn = findViewById(R.id.crear_btn)

        // boton de login
        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Encontra el usaraio dentro de la firestore
                db.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            val email = documents.documents[0].getString("email")

                            //use el email para autenticacion
                            auth.signInWithEmailAndPassword(email!!, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val intent = Intent(this, BuscadorActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Nombre de usuario no encontrado", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al buscar el nombre de usuario", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Por favor ingresa el nombre de usuario y la contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        // Create account button click listener
        crearBtn.setOnClickListener {
            val intent = Intent(this, crearCuenta::class.java)
            startActivity(intent)
        }
    }
}

