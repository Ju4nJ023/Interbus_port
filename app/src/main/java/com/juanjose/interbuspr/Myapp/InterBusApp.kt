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

class InterBusApp : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var usernameInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginBtn: Button
    lateinit var crearBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inter_bus_app)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }
        auth = FirebaseAuth.getInstance()
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        crearBtn =findViewById(R.id.crear_btn)

        loginBtn.setOnClickListener {
            val email = usernameInput.text.toString()
            val password = passwordInput.text.toString()

             //validacion
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener{ task ->
                        if (task.isSuccessful){
                            val intent = Intent (this, BuscadorActivity ::class.java)
                            startActivity(intent)
                        }else {
                            Toast.makeText(this, "error de autenticacion", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        crearBtn.setOnClickListener{
            val intent = Intent(this ,crearCuenta::class.java)
            startActivity(intent)
        }

    }
}

