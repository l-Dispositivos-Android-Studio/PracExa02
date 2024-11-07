package com.sprzd.a12_firebaseaccess

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.sprzd.a12_firebaseaccess.ui.users.LoginActivity
import com.google.firebase.auth.FirebaseAuth




class MainActivity : AppCompatActivity() {

    var auth = FirebaseAuth.getInstance()
    var email: String? = null
    var contra: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


// intenta obtener el token del usuario del local storage, sino llama a la ventana de registro
        val prefe = getSharedPreferences("appData", Context.MODE_PRIVATE)
        email = prefe.getString("email","")
        contra = prefe.getString("contra","")

        if(email.toString().trim { it <= ' ' }.length == 0){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }else {
            val uid: String = auth.uid.toString()
            if (uid == "null"){
                auth.signInWithEmailAndPassword(email.toString(), contra.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this,"Autenticación correcta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            obtenerDatos()
        }




    }
    private fun obtenerDatos() {
        Toast.makeText(this,"Esperando hacer algo importante", Toast.LENGTH_LONG).show()
    }

}