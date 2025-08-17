package com.example.aula01
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class ArquivadosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.arquivados_layout)

        val botaoVoltar = findViewById<Button>(R.id.button4)
        val layoutArquivados = findViewById<LinearLayout>(R.id.containerArquivados)

        botaoVoltar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val bancoDados = FirebaseDatabase.getInstance("https://projeto3bim-87ef8-default-rtdb.firebaseio.com/")
        val referenciaArquivados = bancoDados.getReference("arquivados")

        // Listener para pegar os textos arquivados
        referenciaArquivados.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dados: DataSnapshot) {
                layoutArquivados.removeAllViews() // limpa antes de redesenhar

                for (registro in dados.children) {
                    val idRegistro = registro.key
                    val textoArquivado = registro.getValue(String::class.java)

                    if (!textoArquivado.isNullOrEmpty() && idRegistro != null) {
                        // Layout horizontal para texto + botão
                        val itemLayout = LinearLayout(this@ArquivadosActivity).apply {
                            orientation = LinearLayout.HORIZONTAL
                            setPadding(8, 8, 8, 8)
                            gravity = Gravity.CENTER_VERTICAL
                        }

                        val caixaTexto = TextView(this@ArquivadosActivity).apply {
                            text = textoArquivado
                            textSize = 18f
                            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                        }

                        val botaoExcluir = Button(this@ArquivadosActivity).apply {
                            text = "Excluir"
                            setOnClickListener {
                                // Remove do Firebase
                                referenciaArquivados.child(idRegistro).removeValue()
                                Toast.makeText(this@ArquivadosActivity, "Texto excluído", Toast.LENGTH_SHORT).show()
                            }
                        }

                        // Adiciona TextView e botão no layout horizontal
                        itemLayout.addView(caixaTexto)
                        itemLayout.addView(botaoExcluir)

                        // Adiciona o layout do item no container principal
                        layoutArquivados.addView(itemLayout)
                    }
                }
            }

            override fun onCancelled(erro: DatabaseError) {
                val mensagemErro = TextView(this@ArquivadosActivity).apply {
                    text = "Erro ao carregar: ${erro.message}"
                }
                layoutArquivados.addView(mensagemErro)
            }
        })
    }
}
