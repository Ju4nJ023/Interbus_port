package com.juanjose.interbuspr.Myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.juanjose.interbuspr.R

class crearCuenta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crearcuenta)



        val crearBtn : Button =findViewById(R.id.crear_btn)
        val usernameInput : EditText = findViewById(R.id.username_input)
        val emailInput : EditText = findViewById(R.id.correoElecronico_input)
        val newPasswordInput : EditText =findViewById(R.id.newPassword_input)
        val confirmPasswordInput : EditText = findViewById(R.id.confirmPassword_input)

        crearBtn.setOnClickListener{
            val username =usernameInput.text.toString()
            val email = emailInput.text.toString()
            val Password = newPasswordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            val intent = Intent(this,InterBusApp::class.java)
            startActivity(intent)
        }


        }
    }
