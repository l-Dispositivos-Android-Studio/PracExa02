package com.sprzd.a12_firebaseaccess.ui.users

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sprzd.a12_firebaseaccess.R

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

const val valorIntentSignup = 1

class LoginActivity : AppCompatActivity() {

    var auth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()

    private lateinit var btnAutenticar: Button
    private lateinit var txtEmail: EditText
    private lateinit var txtContra: EditText
    private lateinit var txtRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        btnAutenticar = findViewById(R.id.btnAutenticar)
        txtEmail = findViewById(R.id.txtEmail)
        txtContra = findViewById(R.id.txtContra)
        txtRegister = findViewById(R.id.txtRegister)

        txtRegister.setOnClickListener {
            goToSignup()
        }

        btnAutenticar.setOnClickListener {
            if (txtEmail.text.isNotEmpty() && txtContra.text.isNotEmpty()) {
                auth.signInWithEmailAndPassword(txtEmail.text.toString(), txtContra.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val dt: Date = Date()
                        val user = hashMapOf("ultAcceso" to dt.toString())

                        db.collection("datosUsuarios").whereEqualTo("idemp", it.result?.user?.uid.toString()).get()
                            .addOnSuccessListener { documentReference ->
                                documentReference.forEach { document ->
                                    db.collection("datosUsuarios").document(document.id).update(user as Map<String, Any>)
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al actualizar los datos del usuario", Toast.LENGTH_SHORT).show()
                            }

                        // Register the data into the local storage
                        val prefe = this.getSharedPreferences("appData", Context.MODE_PRIVATE)
                        val editor = prefe.edit()

                        editor.putString("email", txtEmail.text.toString())
                        editor.putString("contra", txtContra.text.toString())
                        editor.commit()

                        // Call back to main activity
                        Intent().let {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    } else {
                        showAlert("Error", "Al autenticar el usuario")
                    }
                }
            } else {
                showAlert("Error", "El correo electrónico y contraseña no pueden estar vacíos")
            }
        }
    }

    private fun goToSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivityForResult(intent, valorIntentSignup)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Validate control variables
        if (resultCode == Activity.RESULT_OK) {
            // Call back to main activity
            Intent().let {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun showAlert(titu: String, mssg: String) {
        val diagMessage = AlertDialog.Builder(this)
        diagMessage.setTitle(titu)
        diagMessage.setMessage(mssg)
        diagMessage.setPositiveButton("Aceptar", null)

        val diagVentana: AlertDialog = diagMessage.create()
        diagVentana.show()
    }
}
