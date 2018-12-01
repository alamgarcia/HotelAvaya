package com.example.garcia76.hotelavaya

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.example.garcia76.hotelavaya.DataClass.LoginData
import com.example.garcia76.hotelavaya.Utils.HashUtils
import com.example.garcia76.hotelavaya.Utils.useInsecureSSL
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {/* ... */
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {/* ... */
                    }
                }).check()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        var myPreferences = "myPrefs"
        var sharedPreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
        var firstrun = sharedPreferences.getBoolean("firstlogin", true)
        if (firstrun) {
           Log.d("Mensajes", "No s eha configurado")
        } else {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        //LIsteners Botones

        // Obtenemos referencias
        val login_btn_click = findViewById<Button>(R.id.login_btn)
        val facelogin_btn_click = findViewById<Button>(R.id.facelogin_btn)
        val recoverypwd_btn_click = findViewById<Button>(R.id.recoverpwd_btn)
        // Creamos Listener
        login_btn_click.setOnClickListener {
            val user = email_txt.text.toString()
            val pass = pass_txt.text.toString()
            login(user,pass)

        }
        facelogin_btn_click.setOnClickListener {
            val intent = Intent(this, DetectFaceLogin::class.java)
            // Pasar Datos a la siguiente actividad
           // intent.putExtra("keyIdentifier", value)
            // Iniciar la Actividad
            startActivity(intent)
        }
        recoverpwd_btn.setOnClickListener {
        }

        newuser_btn.setOnClickListener {
            val intent = Intent(this, NuevoRegistro::class.java)
            startActivity(intent)
        }


    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    //Funciones Extras


    //Bypass Certificados SSL


    //Funcion Login



    fun login(user: String, password:String){
        useInsecureSSL()
        //Convertir a SHA1 el campo de ocntraseña
        var passsha1 = HashUtils.sha1(password)
        //Imprimimos Debug
        Log.d("Login", password)
        //Imprimimos Debug
        Log.d("Login", passsha1)
        val manager: FuelManager by lazy { FuelManager() }
        //Usamos el metodo request de FUUEL Manager, junto a la lusta de parametros
        manager.request(Method.GET, "https://devavaya.ddns.net/hotel/api.php/records/usuarios_tb?filter=correo,eq,$user&filter=password,eq,$passsha1").responseString { req, res, result ->
            val (data, error) = result
            //Si no tenemos ningun error, procedemos a hacer la llamada, ya que el servidor respondio con un 200 y tendremos el Token de LLamada
            when (error) {
                null -> {
                    //Imprimimos el Response en el LogCat solo para asegurar que se hizo bien la peticion
                    Log.d("RESPONSES", data)
                    // creamos una variable llamada gson para la Funcion GSON() para que sea mas accesible
                    var gson = Gson()
                    //Asignamos a la variable Login el metodo gson?.fromJson(data, Login.Response::class.java) y le pasamos el response JSON para su conversion a un objeto que Android puede manejar
                    var Login = gson?.fromJson(data, LoginData::class.java)
                    if (Login.records.isEmpty()) {
                        Toast.makeText(this@MainActivity, "Datos Incorrecta. Revisa tu correo y/o contraseña ", Toast.LENGTH_LONG).show()
                    } else {

                        val myPreferences = "myPrefs"
                        val sharedPreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("id", Login.records[0].id.toString())
                        editor.putString("nombre", Login.records[0].nombre)
                        editor.putString("apellidos", Login.records[0].apellidos)
                        editor.putString("hotel", Login.records[0].hotel.toString())
                        editor.putString("usuario", Login.records[0].usuario)
                        editor.putString("wifi", Login.records[0].wifi)
                        editor.putString("correo", Login.records[0].correo)
                        editor.putString("password", Login.records[0].password)
                        editor.putString("mac", Login.records[0].mac)
                        editor.putBoolean("firstlogin", false)
                        editor.apply()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                    }

                }
            }
        }
    }
}
