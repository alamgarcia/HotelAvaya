package com.example.garcia76.hotelavaya.Fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import com.example.garcia76.hotelavaya.DataClass.UsersData
import com.example.garcia76.hotelavaya.R
import com.example.garcia76.hotelavaya.Utils.AudioEmitter
import com.example.garcia76.hotelavaya.Utils.useInsecureSSL
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.google.api.gax.rpc.ApiStreamObserver
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.speech.v1.*
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicBoolean
import android.widget.ArrayAdapter






private const val TAG = "Speech"


class Dashboard : Fragment() {


    private var mPermissionToRecord = true
    private var mAudioEmitter: AudioEmitter? = null
    private lateinit var mTextView: EditText
    var destinospinner: Spinner? = null
    var destino: String? =null

    private val mSpeechClient by lazy {
        activity?.applicationContext?.resources?.openRawResource(R.raw.sa).use {
            SpeechClient.create(SpeechSettings.newBuilder()
                    .setCredentialsProvider { GoogleCredentials.fromStream(it) }
                    .build())
        }
    }

    override fun onPause() {
        super.onPause()

        // ensure mic data stops
        mAudioEmitter?.stop()
        mAudioEmitter = null
    }

    override fun onDestroy() {
        super.onDestroy()

        // cleanup
        mSpeechClient.shutdown()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {



        val view: View = inflater.inflate(R.layout.fragment_dashboard, container, false)
        obtenerdestinos()

        return view


    }

    override fun onResume() {
        super.onResume()

        var myPreferences = "myPrefs"
        var sharedPreferences = activity?.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
        var uuid = sharedPreferences?.getString("id", "1")
        var socket: Socket

        var opts = IO.Options()
        opts.forceNew = true
        opts.reconnection = true
        socket = IO.socket("https://devavaya.ddns.net:3000", opts)

        socket.connect()

        socket.on(Socket.EVENT_CONNECT) {
            socket.emit("reg", uuid)
        }.on("p2p") { args -> val obj = args[0] as JSONObject
        Log.d("Eventos",obj["message"].toString())


        }
        btn1.setOnClickListener {
            Log.d("Test", destino)
            var texto = resultados_tts.text.toString()

            val obj = JSONObject()
            obj.put("to", destino)
            obj.put("message", texto)
            socket.emit("p2p", obj)
            Log.d("Eventos",texto)

        }

        rec_btn.setOnClickListener{
            // Get the toggle button state programmatically
            if(rec_btn.isChecked){
                // If toggle button is checked/on then
                if (mPermissionToRecord) {
                    val isFirstRequest = AtomicBoolean(true)
                    mAudioEmitter = AudioEmitter()
                    // start streaming the data to the server and collect responses
                    val requestStream = mSpeechClient.streamingRecognizeCallable()
                            .bidiStreamingCall(object : ApiStreamObserver<StreamingRecognizeResponse> {
                                override fun onNext(value: StreamingRecognizeResponse) {
                                    activity?.runOnUiThread {
                                        resultados_tts.text.clear()
                                        when {
                                            value.resultsCount > 0 -> resultados_tts.setText(value.getResults(0).getAlternatives(0).transcript)
                                            else -> Log.d(TAG,"Ha ocurrido un error")
                                        }
                                    }
                                }

                                override fun onError(t: Throwable) {
                                    Log.e(TAG, "an error occurred", t)
                                }

                                override fun onCompleted() {
                                    Log.d(TAG, "stream closed")
                                }
                            })

                    mAudioEmitter!!.start { bytes ->
                        val builder = StreamingRecognizeRequest.newBuilder()
                                .setAudioContent(bytes)
                        if (isFirstRequest.getAndSet(false)) {
                            builder.streamingConfig = StreamingRecognitionConfig.newBuilder()
                                    .setConfig(RecognitionConfig.newBuilder()
                                            .setLanguageCode("es_MX")
                                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                                            .setSampleRateHertz(16000)
                                            .build())
                                    .setInterimResults(false)
                                    .setSingleUtterance(false)
                                    .build()
                        }

                        // send the next request
                        requestStream.onNext(builder.build())
                    }
                } else {
                    Log.e(TAG, "No permission to record! Please allow and then relaunch the app!")
                }
            }else{

                // ensure mic data stops
                mAudioEmitter?.stop()
                mAudioEmitter = null
            }
        }


    }



    fun obtenerdestinos() {
        useInsecureSSL()
        //Convertir a SHA1 el campo de ocntraseña
        val manager: FuelManager by lazy { FuelManager() }
        //Usamos el metodo request de FUUEL Manager, junto a la lusta de parametros
        manager.request(Method.GET, "https://devavaya.ddns.net/hotel/api.php/records/usuarios_tb?include=id,nombre").responseString { req, res, result ->
            val (data, error) = result
            //Si no tenemos ningun error, procedemos a hacer la llamada, ya que el servidor respondio con un 200 y tendremos el Token de LLamada
            when (error) {
                null -> {
                    //Imprimimos el Response en el LogCat solo para asegurar que se hizo bien la peticion
                    Log.d("RESPONSES", data)
                    // creamos una variable llamada gson para la Funcion GSON() para que sea mas accesible
                    var gson = Gson()
                    //Asignamos a la variable Login el metodo gson?.fromJson(data, Login.Response::class.java) y le pasamos el response JSON para su conversion a un objeto que Android puede manejar
                    var Login = gson.fromJson(data, UsersData::class.java)
                    if (Login.records.isEmpty()) {
                        Log.d("Mensajes", "Ha ocurrido un error")
                    } else {
                        var spinnerArray = arrayOfNulls<String>(Login.records.size)

                        var spinnerMap = HashMap<Int, String>()
                        for (i in 0 until Login.records.size) {

                            spinnerMap[i] = Login.records[i].id.toString()
                            spinnerArray[i] = Login.records[i].nombre
                        }

                        var destinospinner = activity!!.findViewById(R.id.user_spinner) as Spinner
                        var adapter = ArrayAdapter<String>(activity!!.applicationContext, android.R.layout.simple_spinner_item, spinnerArray)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        destinospinner.adapter = adapter
                        /*set click listener*/
                        destinospinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                 destino = spinnerMap[destinospinner.selectedItemPosition]
                                Log.d("Spinner" ,destino)
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


    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO)
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        fun newInstance(): Dashboard = Dashboard()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            mPermissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }

        // bail out if audio recording is not available
        if (!mPermissionToRecord) {


        }
    }

}
