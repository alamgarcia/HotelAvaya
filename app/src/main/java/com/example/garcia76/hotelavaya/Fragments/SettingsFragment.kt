package com.example.garcia76.hotelavaya.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.garcia76.hotelavaya.DataClass.LanguageData
import com.example.garcia76.hotelavaya.R
import com.example.garcia76.hotelavaya.Utils.useInsecureSSL
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.HashMap

var idiomav : String? = null

class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_settings, container, false)
        idiomastts()



        return view

    }

    override fun onResume() {
        super.onResume()
        guardar_btn.setOnClickListener {
            var myPreferences = "myPrefs"
            var sharedPreferences = activity?.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
            var uuid = sharedPreferences?.getString("id", "1")
            Log.d("update", idiomav)
            actualizaridioma(uuid!!, idiomav!!)
        }

        limpiar_btn.setOnClickListener {
            var myPreferences = "myPrefs"
            var sharedPreferences = activity?.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
            sharedPreferences?.edit()?.clear()?.apply()
            activity?.finish()

        }

    }


    fun idiomastts() {
        useInsecureSSL()
        //Convertir a SHA1 el campo de ocntraseña
        val manager: FuelManager by lazy { FuelManager() }
        //Usamos el metodo request de FUUEL Manager, junto a la lusta de parametros
        manager.request(Method.GET, "https://translation.googleapis.com/language/translate/v2/languages?key=AIzaSyDjFb-kHwyIdaQrjIRV_v_pJYpGWpBhKps&target=es").responseString { req, res, result ->
            val (data, error) = result
            //Si no tenemos ningun error, procedemos a hacer la llamada, ya que el servidor respondio con un 200 y tendremos el Token de LLamada
            when (error) {
                null -> {
                    //Imprimimos el Response en el LogCat solo para asegurar que se hizo bien la peticion
                    Log.d("RESPONSES", data)
                    // creamos una variable llamada gson para la Funcion GSON() para que sea mas accesible
                    var gson = Gson()
                    //Asignamos a la variable Login el metodo gson?.fromJson(data, Login.Response::class.java) y le pasamos el response JSON para su conversion a un objeto que Android puede manejar
                    var Data = gson.fromJson(data, LanguageData::class.java)
                    if (Data.data.languages.isEmpty()) {
                        Log.d("Mensajes", "Ha ocurrido un error")
                    } else {
                        var spinnerArray2 = arrayOfNulls<String>(Data.data.languages.size)

                        var spinnerMap2 = HashMap<Int, String>()
                        for (i in 0 until Data.data.languages.size) {

                            spinnerMap2[i] = Data.data.languages[i].language
                            spinnerArray2[i] = Data.data.languages[i].name
                        }

                        var destinospinner2 = activity!!.findViewById(R.id.tts_spinner) as Spinner
                        var adapter2 = ArrayAdapter<String>(activity!!.applicationContext, android.R.layout.simple_spinner_item, spinnerArray2)
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        destinospinner2.adapter = adapter2
                        /*set click listener*/
                        destinospinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                idiomav = spinnerMap2[destinospinner2.selectedItemPosition]
                                Log.d("Spinner", idiomav)


                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                                /*Do something if nothing selected*/
                            }
                        }

                    }
                }
            }
        }

    }

    fun actualizaridioma(userid: String, idioma:String){
        val manager: FuelManager by lazy { FuelManager() }
        //Usamos el metodo request de FUUEL Manager, junto a la lusta de parametros
        manager.request(Method.PUT, "https://devavaya.ddns.net/hotel/api.php/records/usuarios_tb/$userid")
                .jsonBody( "{\n" +
                        "    \"idiomatts\": \"$idioma\"\n" +
                        "}")
                .responseString { req, res, result ->

                    val (data, error) = result
                    //Si no tenemos ningun error, procedemos a hacer la llamada, ya que el servidor respondio con un 200 y tendremos el Token de LLamada
                    when (error) {
                        null -> {
                            //Imprimimos el Response en el LogCat solo para asegurar que se hizo bien la peticion
                            Log.d("RESPONSES", data)
                            val myPreferences = "myPrefs"
                            var sharedPreferences = activity?.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
                            val editor = sharedPreferences?.edit()
                            editor?.putString("idiomatts", idioma)
                            editor?.apply()
                            // creamos una variable llamada gson para la Funcion GSON() para que sea mas accesible

                            }
                        }
                    }
                }



    companion object {
        fun newInstance(): SettingsFragment = SettingsFragment()
    }
}
