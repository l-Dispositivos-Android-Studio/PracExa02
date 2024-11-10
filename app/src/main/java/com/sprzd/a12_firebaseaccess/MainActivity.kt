package com.sprzd.a12_firebaseaccess

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.widget.ListView
import android.util.Log
import com.sprzd.a12_firebaseaccess.ui.users.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sprzd.a12_firebaseaccess.entities.cls_Category
import com.sprzd.a12_firebaseaccess.ui.categories.CategoryAdapter

const val valorIntentLogin = 1

class MainActivity : AppCompatActivity() {

    var auth = FirebaseAuth.getInstance()
    var email: String? = null
    var contra: String? = null
    var db = FirebaseFirestore.getInstance()
    var TAG = "YorkTestingApp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Intenta obtener el token del usuario del almacenamiento local; si no, llama a la ventana de registro
        val prefe = getSharedPreferences("appData", Context.MODE_PRIVATE)
        email = prefe.getString("email", "")
        contra = prefe.getString("contra", "")

        if (email.toString().trim { it <= ' ' }.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, valorIntentLogin)
        } else {
            val uid: String = auth.uid.toString()
            if (uid == "null") {
                auth.signInWithEmailAndPassword(email.toString(), contra.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Autenticación correcta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            obtenerDatos()
        }
    }

    private fun obtenerDatos() {
        val coleccion: ArrayList<cls_Category?> = ArrayList()
        val listaView: ListView = findViewById(R.id.lstCategories)

        db.collection("Categories").orderBy("CategoryID").get()
            .addOnCompleteListener { docc ->
                if (docc.isSuccessful) {
                    for (document in docc.result!!) {
                        Log.d(TAG, document.id + " => " + document.data)
                        val datos = cls_Category(
                            document.data["CategoryID"].toString().toInt(),
                            document.data["CategoryName"].toString(),
                            document.data["Description"].toString(),
                            document.data["urlImage"].toString()
                        )
                        coleccion.add(datos)
                    }
                    val adapter = CategoryAdapter(this, coleccion)
                    listaView.adapter = adapter
                } else {
                    Log.w(TAG, "Error getting documents.", docc.exception)
                }
            }
    }
}
