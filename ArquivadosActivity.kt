package com.example.aula01

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase


class ArquivadosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.arquivados_layout)  // O layout da ArquivadosActivity

        val ButtonLer = findViewById<Button>(R.id.button4)
        ButtonLer.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
//            setContentView(R.layout.activity_main)
        }

        val database = FirebaseDatabase.getInstance("https://projeto3bim-87ef8-default-rtdb.firebaseio.com/")
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")

    }
}
