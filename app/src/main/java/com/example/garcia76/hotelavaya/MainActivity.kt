package com.example.garcia76.hotelavaya

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.security.cert.X509Certificate
import javax.net.ssl.*


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
    fun useInsecureSSL() {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate>? = null
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
        })
        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, java.security.SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        val allHostsValid = HostnameVerifier { _, _ -> true }
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
    }


    //Check Array está vacío o no


    fun isNullOrEmpty(str: String?): Boolean {
        if (str != null && !str.isEmpty())
            return false
        return true
    }


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
                    var Login = gson?.fromJson(data, Record::class.java)
                    if (isNullOrEmpty(Login.toString()))
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Contraseña o correo incorrecto", Toast.LENGTH_SHORT).show()
                            email_txt.text.clear()
                            pass_txt.text.clear()
                        }
                    else
                        println("Se encontró usuario")
                    val intent = Intent(this, HomeActivity::class.java)
                    // Pasar Datos a la siguiente actividad
                    // intent.putExtra("keyIdentifier", value)
                    // Iniciar la Actividad
                    startActivity(intent)

                }
            }
        }
    }
}
