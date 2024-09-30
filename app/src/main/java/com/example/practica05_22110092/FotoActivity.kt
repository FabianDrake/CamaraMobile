package com.example.practica05_22110092

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FotoActivity : AppCompatActivity() {
    private lateinit var foto: ImageView
    private lateinit var btnTomar: Button
    private lateinit var btnGuardar: Button
    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto)

        foto = findViewById(R.id.foto)
        btnTomar = findViewById(R.id.btnTomar)
        btnGuardar = findViewById(R.id.button)

        btnTomar.setOnClickListener {
            if (checkPermissions()) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                responseLauncher.launch(intent)
            } else {
                requestPermissions()
            }
        }

        btnGuardar.setOnClickListener {
            if (imageBitmap != null) {
                guardarFoto(imageBitmap!!)
            } else {
                Toast.makeText(this, "No hay foto para guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val responseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            val extras = activityResult.data?.extras
            imageBitmap = extras?.get("data") as Bitmap?
            foto.setImageBitmap(imageBitmap)
        } else {
            Toast.makeText(this, "Foto no tomada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarFoto(bitmap: Bitmap) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            try {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                Toast.makeText(this, "Foto guardada exitosamente", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error al guardar la foto", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No se pudo crear el archivo de imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                responseLauncher.launch(intent)
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}
