package com.example.aula01

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var imageView: ImageView
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        val button: Button = findViewById(R.id.button)
        val buttonLayoutArquivados = findViewById<Button>(R.id.button3)

/*        button.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, 100)
            } else {
                Toast.makeText(this, "Câmera não disponível", Toast.LENGTH_SHORT).show()
            }
        }
*/

        var idbanco = 0;

        buttonLayoutArquivados.setOnClickListener{


        val database = FirebaseDatabase.getInstance("https://projeto3bim-87ef8-default-rtdb.firebaseio.com/")
        val myRef = database.getReference(idbanco.toString())
            val textViewResult = findViewById<TextView>(R.id.textViewResult)
            myRef.setValue(textViewResult.text.toString())

            idbanco += 1
            val intent = Intent(this, ArquivadosActivity::class.java)
            startActivity(intent)
//            setContentView(R.layout.arquivados_layout)
        }

        button.setOnClickListener {
            imageView.setImageResource(R.drawable.img) // nome do arquivo sem extensão
        }

        /* adicionar " implementation("com.google.mlkit:text-recognition:16.0.0") " no build.grandle(Module:)
            adicionar uma imagem para tester na pasta "res/drawble" com o nome de "image_test.jpg"
            */

        // Recupera os componentes da interface
        val textViewResult = findViewById<TextView>(R.id.textViewResult)
        val btnSelectImage = findViewById<Button>(R.id.button2)

        textToSpeech = TextToSpeech(this, this)
        val speakButton = findViewById<Button>(R.id.button2)
        val textInput = findViewById<TextView>(R.id.textViewResult)

        speakButton.setOnClickListener {
            val text = textInput.text.toString()
            if (text.isNotEmpty()) {
                speakText(text)
            }
        }

        // Quando clicar no botão, processa a imagem que já está no ImageView
        btnSelectImage.setOnClickListener {
            processImageFromImageView()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val photo = data?.extras?.get("data") as? Bitmap
            if (photo != null) {
                imageView.setImageBitmap(photo)
            } else {
                Toast.makeText(this, "Erro ao capturar imagem", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Função que lê a imagem do ImageView e faz o reconhecimento de texto
    private fun processImageFromImageView() {
        // Verifica se há uma imagem no ImageView
        val drawable = imageView.drawable
        val textViewResult = findViewById<TextView>(R.id.textViewResult)

        if (drawable != null && drawable is BitmapDrawable) {
            // Converte o Drawable em Bitmap
            val bitmap: Bitmap = drawable.bitmap

            // Cria o objeto InputImage necessário para o ML Kit
            val image = InputImage.fromBitmap(bitmap, 0)

            // Inicializa o reconhecedor de texto
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            // Processa a imagem
            recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        // Exibe o texto reconhecido

                        textViewResult.text = visionText.text
                        textToSpeech.speak(visionText.text, TextToSpeech.QUEUE_FLUSH,null,null)


                    }
                    .addOnFailureListener { e ->
                        // Mostra mensagem de erro caso falhe
                        textViewResult.text = "Erro: ${e.message}"
                    }
        } else {
            // Caso não haja imagem, mostra uma mensagem
            textViewResult.text = "Nenhuma imagem carregada no ImageView."
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale.getDefault()
            val result = textToSpeech.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    private fun speakText(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }

}
