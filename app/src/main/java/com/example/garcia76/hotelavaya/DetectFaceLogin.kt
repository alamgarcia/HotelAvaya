package com.example.garcia76.hotelavaya

import android.content.Intent
import android.graphics.ImageFormat
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.Resolution
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.*
import io.fotoapparat.view.CameraView


class DetectFaceLogin : AppCompatActivity() {
    var camerav : CameraView? = null
    var fotoapparat: Fotoapparat? = null
    var mGraphicOverlay: GraphicOverlay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect_face_login)
        mGraphicOverlay = findViewById(R.id.graphic_overlay)

// Real-time contour detection of multiple faces
        var options = FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                .build()


        //Configuracion de Camara
        var camerav = findViewById<CameraView>(R.id.cameraView)
        var metadata = FirebaseVisionImageMetadata.Builder()
                .setWidth(640)   // 480x360 is typically sufficient for
                .setHeight(480)
                .setFormat(ImageFormat.NV21)
                .setRotation(FirebaseVisionImageMetadata.ROTATION_270)
                .build()
        var cameraConfiguration = CameraConfiguration(
                pictureResolution = firstAvailable(
                        { Resolution(480,360)
                            Resolution(640,480) },
                        highestResolution()
                ),
                previewResolution = firstAvailable(
                        { Resolution(480,360)
                            Resolution(640,480) },
                        highestResolution()
                ),
                previewFpsRange = highestFps(),
                focusMode = firstAvailable(

                        autoFocus(),
                        continuousFocusPicture(),// if continuous focus is not available on device, auto focus will be used
                        fixed()                            // if even auto focus is not available - fixed focus mode will be used
                ),
                flashMode = firstAvailable(              // (optional) similar to how it is done for focus mode, this time for flash
                        autoRedEye(),
                        autoFlash(),
                        torch(),
                        off()
                ),
                antiBandingMode = firstAvailable(       // (optional) similar to how it is done for focus mode & flash, now for anti banding
                        auto(),
                        hz50(),
                        hz60(),
                        none()
                ),
                jpegQuality = manualJpegQuality(50),     // (optional) select a jpeg quality of 90 (out of 0-100) values
                sensorSensitivity = lowestSensorSensitivity(), // (optional) we want to have the lowest sensor sensitivity (ISO)
                frameProcessor = { frame ->
                    Log.d("Frame", "Rotation: "+frame.rotation.toString())
                    Log.d("Frame", "Width: "+frame.size.width.toString())
                    Log.d("Frame", "Width: "+frame.size.height.toString())
                    var image = FirebaseVisionImage.fromByteArray(frame.image, metadata)
                    var detector = FirebaseVision.getInstance().getVisionFaceDetector(options)
                    detector.detectInImage(image)
                            .addOnSuccessListener { faces ->
                                processFaceContourDetectionResult(faces)
                            }
                .addOnFailureListener { e ->
                    e.printStackTrace() }
                })
        fotoapparat = Fotoapparat(
                context = this,
                view = camerav,                   // view which will draw the camera preview
                scaleType = ScaleType.CenterCrop,    // (optional) we want the preview to fill the view
                lensPosition = front(),               // (optional) we want back camera
                cameraConfiguration = cameraConfiguration, // (optional) define an advanced configuration
                logger = loggers(                    // (optional) we want to log camera events in 2 places at once
                        logcat()                   // ... in logcat
                ),
                cameraErrorCallback = { error ->
                    Log.d("Camara", "Ha ocurrido un error")
                }   // (optional) log fatal errors
        )

    }


  /*   fun createFotoapparat(position:LensPosition):Fotoapparat {
        var cameraView = findViewById<CameraView>(R.id.cameraView)
        var rectanglesView = findViewById<RectanglesView>(R.id.rectanglesView)
        return Fotoapparat
                .with(this)
                .into(cameraView)
                .lensPosition(lensPosition(position))
                .frameProcessor(
                        FaceDetectorProcessor.with(this)
                                .listener {
                                    faces -> rectanglesView.setRectangles(faces)
                                    Log.d("Camara", "Rostros Detectados: " + faces.size)
                                            }
                                .build()
                )
                .logger(loggers(
                        logcat(),
                        fileLogger(this)
                ))
                .build()
    }*/

    override fun onStart() {
        super.onStart()

        fotoapparat?.start()

    }

    override fun onStop() {
        super.onStop()
        fotoapparat?.stop()
    }

    fun processFaceContourDetectionResult(faces: List<FirebaseVisionFace>) {
        if (faces.isEmpty()) {
            Log.d("Caras", "No se detectaron")
            return
        }
        mGraphicOverlay?.clear();
        for (i in faces.indices) {
            var face = faces[i]
            var faceGraphic = FaceContourGraphic(mGraphicOverlay)
            mGraphicOverlay?.add(faceGraphic)
            faceGraphic.updateFace(face)
            val bounds = face.boundingBox
            val rotY = face.headEulerAngleY  // Head is rotated to the right rotY degrees
            val rotZ = face.headEulerAngleZ  // Head is tilted sideways rotZ degrees

            Log.d("Datos", "Limites: "+bounds)
            Log.d("Datos", "Rot Y: "+rotY)
            Log.d("Datos", "RotX: "+rotZ)


            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
            // nose available):
            val leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)
            leftEar?.let {
                val leftEarPos = leftEar.position
                Log.d("Datos", "leftEarPos: "+leftEarPos)
            }

            // If contour detection was enabled:
            val leftEyeContour = face.getContour(FirebaseVisionFaceContour.LEFT_EYE).points
            val upperLipBottomContour = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).points
            Log.d("Datos", "leftEyeContour: "+leftEyeContour)
            Log.d("Datos", "upperLipBottomContour: "+upperLipBottomContour)


            // If classification was enabled:
            if (face.smilingProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                val smileProb = face.smilingProbability
                Log.d("Datos", "smileProb: "+smileProb)

            }
            if (face.rightEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                val rightEyeOpenProb = face.rightEyeOpenProbability
                Log.d("Datos", "rightEyeOpenProb: "+rightEyeOpenProb)

            }

            // If face tracking was enabled:
            if (face.trackingId != FirebaseVisionFace.INVALID_ID) {
                val id = face.trackingId
                Log.d("Datos", "Datos: "+id)

            }
            fotoapparat?.stop()

            Handler().postDelayed({

                val intent = Intent(this, HomeActivity::class.java)
                // Pasar Datos a la siguiente actividad
                // intent.putExtra("keyIdentifier", value)
                // Iniciar la Actividad
                startActivity(intent)
            }, 1000)




        }
    }

}