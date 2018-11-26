package com.example.garcia76.hotelavaya

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.Loggers.*
import io.fotoapparat.parameter.ScaleType

import kotlinx.android.synthetic.main.activity_detect_face_login.*
import io.fotoapparat.FotoapparatSwitcher
import io.fotoapparat.facedetector.view.RectanglesView
import io.fotoapparat.view.CameraView
import io.fotoapparat.log.Loggers.logcat
import io.fotoapparat.log.Loggers.loggers
import com.example.garcia76.hotelavaya.R.id.rectanglesView
import java.nio.file.Files.size
import io.fotoapparat.facedetector.processor.FaceDetectorProcessor
import com.example.garcia76.hotelavaya.R.id.cameraView
import io.fotoapparat.facedetector.Rectangle
import io.fotoapparat.parameter.LensPosition
import io.fotoapparat.parameter.selector.LensPositionSelectors.*


class DetectFaceLogin : AppCompatActivity() {
    var cameraView: CameraView? = null
    var rectanglesView: RectanglesView? = null

     var fotoapparatSwitcher: FotoapparatSwitcher? = null
     var frontFotoapparat: Fotoapparat? = null
     var backFotoapparat: Fotoapparat? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect_face_login)


        var frontFotoapparat = createFotoapparat(LensPosition.FRONT);
       var backFotoapparat = createFotoapparat(LensPosition.BACK)
        fotoapparatSwitcher = FotoapparatSwitcher.withDefault(frontFotoapparat)


    }

    private fun createFotoapparat(position:LensPosition):Fotoapparat {
        var cameraView = findViewById<CameraView>(R.id.cameraView)
        var rectanglesView = findViewById<RectanglesView>(R.id.rectanglesView)

        return Fotoapparat
                .with(this)
                .into(cameraView)
                .lensPosition(lensPosition(position))
                .frameProcessor(
                        FaceDetectorProcessor.with(this)
                                .listener { faces ->
                                    Log.d("&&&", "Detected faces: " + faces.size)
                                    rectanglesView.setRectangles(faces)
                                }
                                .build()
                )
                .logger(loggers(
                        logcat(),
                        fileLogger(this)
                ))
                .build()
    }

    override fun onStart() {
        super.onStart()
        fotoapparatSwitcher?.start()

    }

    override fun onStop() {
        super.onStop()
        fotoapparatSwitcher?.stop()

    }
    }