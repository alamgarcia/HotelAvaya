package com.example.garcia76.hotelavaya.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.garcia76.hotelavaya.DataClass.LoginData
import com.example.garcia76.hotelavaya.HomeActivity
import com.example.garcia76.hotelavaya.R
import com.example.garcia76.hotelavaya.Utils.HashUtils
import com.example.garcia76.hotelavaya.Utils.useInsecureSSL
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException


class Dashboard : Fragment() {
   var  msocket = IO.socket("http://devavaya.ddns.net:3000")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_dashboard, container, false)
        return view
        socketioclient()
        obtenerdestinos()

    }



    fun obtenerdestinos(){
        useInsecureSSL()
        //Convertir a SHA1 el campo de ocntraseña
        val manager: FuelManager by lazy { FuelManager() }
        //Usamos el metodo request de FUUEL Manager, junto a la lusta de parametros
        manager.request(Method.GET, "https://devavaya.ddns.net/hotel/api.php/records/usuarios_tb").responseString { req, res, result ->
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
                        Log.d("Mensajes", "Ha ocurrido un error")

                    } else {



                    }

                }
            }
        }
    }


    fun socketioclient(){
        try
        {
            var myPreferences = "myPrefs"
            var sharedPreferences = activity?.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
            var uuid = sharedPreferences?.getString("id", "1")
            msocket.connect()
            msocket.emit("reg",uuid)

        }

        catch (e: URISyntaxException) {}
    }




    companion object {
        fun newInstance(): Dashboard = Dashboard()
    }


}
