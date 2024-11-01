package com.juanjose.interbuspr.Myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.juanjose.interbuspr.R

class crearCuenta : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crearcuenta)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val crearBtn: Button = findViewById(R.id.crear_btn)
        val usernameInput: EditText = findViewById(R.id.username_input)
        val emailInput: EditText = findViewById(R.id.correoElecronico_input)
        val newPasswordInput: EditText = findViewById(R.id.newPassword_input)
        val confirmPasswordInput: EditText = findViewById(R.id.confirmPassword_input)

        crearBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val email = emailInput.text.toString()
            val password = newPasswordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "por favor rellene todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, " las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                //crear cuenta en firebase
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val uid = auth.currentUser?.uid
                            if (uid != null) {
                                //guardar el usaurio
                                val user = hashMapOf(
                                    "username" to username,
                                    "email" to email,
                                    "password" to password
                                )
                                val userId = auth.currentUser?.uid ?: ""
                                db.collection("users").document(uid).set(user)
                                    .addOnSuccessListener {
                                        // Usuario guardado correctamente
                                        Toast.makeText(
                                            this,
                                            "Cuenta creada exitosamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(this, InterBusApp::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        // Error al guardar el usuario
                                        Toast.makeText(
                                            this,
                                            "Error al guardar el usuario en la base de datos",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                // Hubo un error al crear la cuenta
                                Toast.makeText(this, "Error al crear la cuenta", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
            }


        }
    }
}
