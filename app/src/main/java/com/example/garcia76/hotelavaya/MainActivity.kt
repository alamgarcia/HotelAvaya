package com.example.garcia76.hotelavaya

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

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
}
