package com.example.ricescan.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.io.InputStream

data class DiseaseResult(
    val diseaseName: String,
    val confidence: Float,
    val displayName: String
)

class DiseaseClassifier(private val context: Context) {

    private var classifier: ImageClassifier? = null
    private val modelFileName = "rice_disease_model.tflite"
    private val inputSize = 224

    // Labels matching your model training order
    private val labels = listOf(
        "healthy",
        "leaf_blast",
        "bacterial_blight",
        "brown_spot",
        "tungro"
    )

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            // Check if model file exists in assets
            val assetFiles = context.assets.list("") ?: emptyArray()
            if (!assetFiles.contains(modelFileName)) {
                Log.w("DiseaseClassifier", "Model file not found in assets yet")
                return
            }

            val options = ImageClassifier.ImageClassifierOptions.builder()
                .setMaxResults(5)
                .setScoreThreshold(0.01f)
                .build()

            classifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelFileName,
                options
            )
            Log.d("DiseaseClassifier", "Model loaded successfully")

        } catch (e: Exception) {
            Log.e("DiseaseClassifier", "Error loading model: ${e.message}")
            classifier = null
        }
    }

    fun classify(imageUri: Uri): DiseaseResult {
        return try {
            val bitmap = loadBitmapFromUri(imageUri)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

            if (classifier == null) {
                // Model not ready yet — return mock result for UI testing
                return getMockResult()
            }

            val tensorImage = TensorImage.fromBitmap(resizedBitmap)
            val results: List<Classifications> = classifier!!.classify(tensorImage)

            if (results.isEmpty() || results[0].categories.isEmpty()) {
                return DiseaseResult("unknown", 0f, "Unknown")
            }

            val topResult = results[0].categories.maxByOrNull { it.score }!!
            val label = topResult.label.lowercase().replace(" ", "_")
            val confidence = topResult.score

            DiseaseResult(
                diseaseName = label,
                confidence = confidence,
                displayName = getLabelDisplayName(label)
            )

        } catch (e: Exception) {
            Log.e("DiseaseClassifier", "Classification error: ${e.message}")
            getMockResult()
        }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open image")
        return BitmapFactory.decodeStream(inputStream)
            ?: throw Exception("Cannot decode image")
    }

    private fun getLabelDisplayName(label: String): String {
        return when (label) {
            "healthy" -> "Healthy Rice Leaf"
            "leaf_blast" -> "Rice Leaf Blast"
            "bacterial_blight" -> "Bacterial Blight"
            "brown_spot" -> "Brown Spot"
            "tungro" -> "Tungro Disease"
            else -> "Unknown"
        }
    }

    // Mock result used when model is not loaded yet (for UI testing)
    private fun getMockResult(): DiseaseResult {
        return DiseaseResult(
            diseaseName = "brown_spot",
            confidence = 0.91f,
            displayName = "Brown Spot"
        )
    }

    fun close() {
        classifier?.close()
    }
}