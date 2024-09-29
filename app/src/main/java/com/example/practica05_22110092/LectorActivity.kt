package com.example.practica05_22110092

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.zxing.integration.android.IntentIntegrator

class LectorActivity : AppCompatActivity() {
    private lateinit var codigo: EditText
    private lateinit var descripcion: EditText
    private lateinit var precio: EditText
    private lateinit var cantidad: EditText
    private lateinit var btnEscanear: Button
    private lateinit var btnCapturar: Button
    private lateinit var btnLimpiar: Button
    private lateinit var btnBuscar: Button

    private val registros = Array(10) { Registro("", "", 0.0, 0) }
    private var registroIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lector)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        codigo = findViewById(R.id.edtcodigo)
        descripcion = findViewById(R.id.edtDescripcion)
        precio = findViewById(R.id.edtPrecio)
        cantidad = findViewById(R.id.edtCantidad)
        btnEscanear = findViewById(R.id.btnEscanear)
        btnCapturar = findViewById(R.id.btnRegistrar)
        btnLimpiar = findViewById(R.id.btnLimpiar)
        btnBuscar = findViewById(R.id.btnBuscar)

        btnEscanear.setOnClickListener { escanearCodigo() }
        btnCapturar.setOnClickListener { capturarDatos() }
        btnLimpiar.setOnClickListener { limpiar() }
        btnBuscar.setOnClickListener { buscarRegistro() }
    }

    private fun escanearCodigo() {
        val intentIntegrator = IntentIntegrator(this@LectorActivity)
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        intentIntegrator.setPrompt("Lector de códigos")
        intentIntegrator.setCameraId(0)
        intentIntegrator.setBeepEnabled(true)
        intentIntegrator.setBarcodeImageEnabled(true)
        intentIntegrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Escaneado: " + intentResult.contents, Toast.LENGTH_LONG).show()
                codigo.setText(intentResult.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun capturarDatos() {
        val codigoText = codigo.text.toString().trim()
        val descripcionText = descripcion.text.toString().trim()
        val precioText = precio.text.toString().trim()
        val cantidadText = cantidad.text.toString().trim()

        if (codigoText.isEmpty() || descripcionText.isEmpty() || precioText.isEmpty() || cantidadText.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show()
            return
        }

        val precioValue = precioText.toDoubleOrNull()
        val cantidadValue = cantidadText.toIntOrNull()

        if (precioValue == null || precioValue <= 0) {
            Toast.makeText(this, "Ingrese un precio válido", Toast.LENGTH_LONG).show()
            return
        }

        if (cantidadValue == null || cantidadValue <= 0) {
            Toast.makeText(this, "Ingrese una cantidad válida", Toast.LENGTH_LONG).show()
            return
        }

        if (registroIndex < registros.size) {
            registros[registroIndex] = Registro(codigoText, descripcionText, precioValue, cantidadValue)
            registroIndex++
            Toast.makeText(this, "Datos capturados correctamente", Toast.LENGTH_SHORT).show()
            limpiar()
        } else {
            Toast.makeText(this, "Registro lleno, no se pueden agregar más datos", Toast.LENGTH_LONG).show()
        }
    }

    private fun limpiar() {
        codigo.setText("")
        descripcion.setText("")
        precio.setText("")
        cantidad.setText("")
        Toast.makeText(this, "Campos limpiados", Toast.LENGTH_SHORT).show()
    }

    private fun buscarRegistro() {
        val codigoBuscar = codigo.text.toString().trim()
        if (codigoBuscar.isEmpty()) {
            Toast.makeText(this, "Ingrese un código para buscar", Toast.LENGTH_LONG).show()
            return
        }

        val registro = registros.find { it.codigo == codigoBuscar }
        if (registro != null) {
            descripcion.setText(registro.descripcion)
            precio.setText(registro.precio.toString())
            cantidad.setText(registro.cantidad.toString())
            Toast.makeText(this, "Registro encontrado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Registro no encontrado", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Toast.makeText(this, "Regreso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}